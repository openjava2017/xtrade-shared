package com.diligrp.xtrade.shared.domain;

import com.diligrp.xtrade.shared.exception.QrCodeException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;

public abstract class QrCode {
    private static final String PROTO_SEPARATOR = "://";

    private static final String SIGN_ALG = "HmacMD5";

    private static final String SIGN_PASSWORD = "qazwsx73";

    private static final String DOT = ".";

    private static final String UTF_8 = "utf-8";

    protected static final int QRCODE_ERROR = 505001;

    //协议名称
    protected String proto;
    //业务数据
    protected byte[] data;
    //Base64编码后的签名数据
    protected String signature;

    protected QrCode() {
    }

    protected QrCode(String source) {
        // 二维码信息格式  protocol://明文数据.签名数据
        int index = source.indexOf(PROTO_SEPARATOR);
        if (index == -1) {
            throw new QrCodeException(QRCODE_ERROR, "无效二维码: 协议无效");
        }
        proto = source.substring(0, index);
        String content = source.substring(index + 3);

        //分别截取业务数据和签名数据
        String[] packet = content.split("\\.");
        if (packet.length != 2) {
            throw new QrCodeException(QRCODE_ERROR, "无效二维码: 数据格式不正确");
        }
        data = Base64.getDecoder().decode(packet[0]);
        signature =  packet[1];
    }

    /**
     * 签名
     */
    protected void sign() {
        sign(SIGN_PASSWORD);
    }

    protected void sign(String password) {
        try {
            //将明文数据通过HmacMD5进行签名
            Mac mac = Mac.getInstance(SIGN_ALG);
            SecretKeySpec secretKey = new SecretKeySpec(password.getBytes(UTF_8), SIGN_ALG);
            mac.init(secretKey);
            //获取签名信息
            signature = Base64.getEncoder().encodeToString(mac.doFinal(data));
        } catch (Exception ex) {
            throw new QrCodeException("二维码数据签名失败", ex);
        }
    }

    protected boolean verify() {
        return verify(SIGN_PASSWORD);
    }

    protected boolean verify(String password) {
        try {
            Mac mac = Mac.getInstance(SIGN_ALG);
            SecretKeySpec secretKey = new SecretKeySpec(password.getBytes(UTF_8), SIGN_ALG);
            mac.init(secretKey);
            //获取签名信息
            String digest = Base64.getEncoder().encodeToString(mac.doFinal(data));
            return digest.equals(signature);
        } catch (Exception ex) {
            throw new QrCodeException("二维码数据验签失败", ex);
        }
    }

    /**
     * 将业务数据进行封包至data中，并进行签名
     */
    protected abstract void pack();


    /**
     * 验签后将data中的业务数据进行拆包
     */
    protected abstract void unpack();

    public abstract Map<String, Object> dumpData();

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(proto).append(PROTO_SEPARATOR).append(Base64.getEncoder().encodeToString(data)).append(DOT).append(signature);
        return builder.toString();
    }
}
