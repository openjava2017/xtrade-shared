package com.diligrp.xtrade.shared.domain;

import com.diligrp.xtrade.shared.exception.QrCodeException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

public abstract class QrCode {
    protected static final int QRCODE_ERROR = 505001;

    private static final int HEADER_SIZE = 10;

    protected static final String PROTO_SEPARATOR = "://";

    private static final String SIGN_ALG = "HmacMD5";

    private static final String SIGN_PASSWORD = "qazwsx73";

    private static final String DOT = ".";

    private static final String UTF_8 = "utf-8";

    //业务数据
    private byte[] data = new byte[0];
    //Base64编码后的签名数据
    private String signature;

    // 二维码版本
    private byte version;
    // 二维码时间戳
    private long timestamp;
    // 用途
    private byte useFor;

    protected QrCode(byte version, long timestamp, byte useFor) {
        this.version = version;
        this.timestamp = timestamp;
        this.useFor = useFor;
    }

    protected QrCode(String source) {
        // 二维码信息格式  protocol://明文数据.签名数据
        int index = source.indexOf(PROTO_SEPARATOR);
        if (index == -1) {
            throw new QrCodeException(QRCODE_ERROR, "无效二维码: 协议无效");
        }
        String protocol = source.substring(0, index);
        if (!protocol().equals(protocol)) {
            throw new QrCodeException(1010, "无效二维码: 不支持此类二维码协议");
        }

        String content = source.substring(index + 3);
        //分别截取业务数据和签名数据
        String[] packet = content.split("\\.");
        if (packet.length != 2) {
            throw new QrCodeException(QRCODE_ERROR, "无效二维码: 数据格式不正确");
        }
        data = Base64.getDecoder().decode(packet[0]);
        signature =  packet[1];
    }

    protected void sign(String password) {
        String key = password == null ? SIGN_PASSWORD : password;
        try {
            //将明文数据通过HmacMD5进行签名
            Mac mac = Mac.getInstance(SIGN_ALG);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(UTF_8), SIGN_ALG);
            mac.init(secretKey);
            //获取签名信息
            this.signature = Base64.getEncoder().encodeToString(mac.doFinal(data));
        } catch (Exception ex) {
            throw new QrCodeException("二维码数据签名失败", ex);
        }
    }

    protected boolean verify(String password) {
        String key = password == null ? SIGN_PASSWORD : password;
        try {
            Mac mac = Mac.getInstance(SIGN_ALG);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(UTF_8), SIGN_ALG);
            mac.init(secretKey);
            //获取签名信息
            String digest = Base64.getEncoder().encodeToString(mac.doFinal(data));
            return digest.equals(signature);
        } catch (Exception ex) {
            throw new QrCodeException("二维码数据验签失败", ex);
        }
    }

    /**
     * 封装二维码通用数据
     */
    protected void pack(String password) {
        ByteBuffer packet = ByteBuffer.allocate(HEADER_SIZE).order(ByteOrder.BIG_ENDIAN);
        packet.put(version);
        packet.putLong(timestamp);
        packet.put(useFor);
        packet.flip();
        append(packet.array());
        byte[] data = doPack();
        append(data);
        //签名
        sign(password);
    }

    protected abstract byte[] doPack();

    /**
     * 验签后将data中的业务数据进行拆包
     */
    protected void unpack(String password) {
        if (!verify(password)) {
            throw new QrCodeException(QRCODE_ERROR, "无效二维码: 数据验签失败");
        }

        ByteBuffer packet = ByteBuffer.wrap(data);
        this.version = packet.get();
        this.timestamp = packet.getLong();
        this.useFor = packet.get();
        byte[] data = new byte[packet.remaining()];
        for (int i = 0; packet.hasRemaining(); i++) {
            data[i] = packet.get();
        }
        packet.clear();
        doUnpack(data); // 拆包自定义数据
    }

    protected abstract void doUnpack(byte[] data);

    protected synchronized void append(byte[] bytes) {
        byte[] packet = new byte[data.length + bytes.length];
        System.arraycopy(data, 0, packet, 0, data.length);
        System.arraycopy(bytes, 0, packet, data.length, bytes.length);
        this.data = packet;
    }

    public abstract String protocol();

    public byte version() {
        return this.version;
    }

    public long timestamp() {
        return this.timestamp;
    }

    public byte useFor() {
        return this.useFor;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(protocol()).append(PROTO_SEPARATOR).append(Base64.getEncoder().encodeToString(data)).append(DOT).append(signature);
        return builder.toString();
    }
}
