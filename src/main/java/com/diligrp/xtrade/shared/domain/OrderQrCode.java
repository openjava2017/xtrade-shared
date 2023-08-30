package com.diligrp.xtrade.shared.domain;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * 订单二维码: od://MTU2MjcyODU2ODoxMjM0NTY.bNyAdr0aw4pjorA_KKqn0w
 */
public class OrderQrCode extends QrCode {
    private static final String PASSWORD = "qetwsx83";

    private static final String QRCODE_PROTOCOL = "od";

    // 市场编码
    private long marketId;
    // 订单ID
    private String orderId;

    public static OrderQrCode from(String source) {
        return new OrderQrCode(source);
    }

    public static OrderQrCode of(byte version, long timestamp, byte useFor, long marketId, String orderId) {
        return new OrderQrCode(version, timestamp, useFor, marketId, orderId);
    }

    private OrderQrCode(String source) {
        super(source);
        unpack(PASSWORD);
    }

    private OrderQrCode(byte version, long timestamp, byte useFor, long marketId, String orderId) {
        super(version, timestamp, useFor);
        this.marketId = marketId;
        this.orderId = orderId;
        pack(PASSWORD);
    }

    @Override
    protected byte[] doPack() {
        byte[] orderBytes = orderId.getBytes(StandardCharsets.UTF_8);
        ByteBuffer packet = ByteBuffer.allocate(orderBytes.length + 8).order(ByteOrder.BIG_ENDIAN);
        packet.putLong(marketId);
        packet.put(orderBytes);
        packet.flip();
        byte[] result = packet.array();
        return result;
    }

    @Override
    protected void doUnpack(byte[] data) {
        ByteBuffer packet = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);
        this.marketId = packet.getLong();
        byte[] bytes = new byte[packet.remaining()];
        for (int i = 0; packet.hasRemaining(); i++) {
            bytes[i] = packet.get();
        }
        packet.clear();
        this.orderId = new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public String protocol() {
        return QRCODE_PROTOCOL;
    }

    /**
     * 协议是否支持此类二维码
     *
     * 比如：OrderQrCode类只支持od协议的二维码数据od://MTU2MjcyODU2ODoxMjM0NTY.bNyAdr0aw4pjorA_KKqn0w
     */
    public static boolean protoSupport(String qrCode) {
        return qrCode.startsWith(QRCODE_PROTOCOL + PROTO_SEPARATOR);
    }

    public long marketId() {
        return marketId;
    }

    public String orderId() {
        return orderId;
    }

    public static void main(String[] args) {
        OrderQrCode qrCode = OrderQrCode.of((byte)1, 100001, (byte)2, 6543, "12345678");
        String qrCodeStr = qrCode.toString();
        System.out.println(qrCodeStr);
        qrCode = OrderQrCode.from(qrCodeStr);
        System.out.println(qrCode.marketId());
        System.out.println(qrCode.orderId());
    }
}
