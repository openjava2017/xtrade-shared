package com.diligrp.xtrade.shared.domain;

import com.diligrp.xtrade.shared.exception.QrCodeException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单二维码，用于扫码付款
 */
public class OrderQrCode extends QrCode {
    private static final String PROTO_ID = "od";

    private static final int DATA_SIZE = 26;

    // 是否支持过期
    private boolean expiredSupport;
    // 二维码时间戳
    private long timestamp;
    // 市场编码
    private int marketId;
    // 订单ID
    private long orderId;
    // 卖家账号
    private long accountId;

    private OrderQrCode(String source) {
        super(source);

        if (!PROTO_ID.equalsIgnoreCase(proto)) {
            throw new QrCodeException(1010, "无效二维码: 不支持此类二维码协议");
        }
    }

    private OrderQrCode(boolean expiredSupport, int marketId, long orderId, long accountId) {
        this.proto = PROTO_ID;
        this.expiredSupport = expiredSupport;
        //UNIX时间戳，单位秒
        this.timestamp = System.currentTimeMillis() / 1000;
        this.marketId = marketId;
        this.orderId = orderId;
        this.accountId = accountId;
    }

    @Override
    protected void pack() {
        //1个字节特征码 + 8个字节时间戳 + 1个字节市场编码 + 8个字节订单ID + 8个字节用户账号
        ByteBuffer packet = ByteBuffer.allocate(DATA_SIZE).order(ByteOrder.BIG_ENDIAN);
        //一个字节的属性码: 0-永久 1-临时
        byte feature = (byte) (expiredSupport ? 1 : 0);
        packet.put(feature);

        packet.putLong(timestamp);
        packet.put((byte) marketId);
        packet.putLong(orderId);
        packet.putLong(accountId);
        packet.flip();
        data = packet.array();

        //签名
        sign();
    }

    @Override
    protected void unpack() {
        if (!verify()) {
            throw new QrCodeException(QRCODE_ERROR, "无效二维码: 数据验签失败");
        }

        if (data == null || data.length != DATA_SIZE) {
            throw new QrCodeException(QRCODE_ERROR, "无效二维码: 业务数据格式错误");
        }

        ByteBuffer packet = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);
        byte feature = packet.get();
        expiredSupport = feature == 1 ? true : false;
        timestamp = packet.getLong();
        marketId = packet.get();
        orderId = packet.getLong();
        accountId = packet.getLong();
    }

    @Override
    public Map<String, Object> dumpData() {
        var data = new HashMap<String, Object>();
        data.put("protocol", proto);
        data.put("timestamp", timestamp);
        data.put("marketId", marketId);
        data.put("orderId", orderId);
        data.put("accountId", accountId);

        return data;
    }

    /**
     * 协议是否支持此类二维码
     *
     * 比如：OrderQrCode类只支持od协议的二维码数据od://MTU2MjcyODU2ODoxMjM0NTY.bNyAdr0aw4pjorA_KKqn0w
     */
    public static boolean protoSupport(String qrCode) {
        return qrCode.startsWith(PROTO_ID);
    }

    public boolean expiredSupport() {
        return expiredSupport;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getMarketId() {
        return marketId;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getAccountId() {
        return accountId;
    }

    public static OrderQrCode from(String source) {
        //电子卡账号二维码信息格式  od://MTU2MjcyODU2ODoxMjM0NTY.bNyAdr0aw4pjorA_KKqn0w；
        OrderQrCode qrCode = new OrderQrCode(source);
        qrCode.unpack();
        return qrCode;
    }

    public static OrderQrCode of(boolean expiredSupport, int marketId, long orderId, long accountId) {
        // 明文信息 = 属性码(一个字节）+ 时间戳(八个字节) + 市场编码(一个字节) + 用户账号(八个字节)
        OrderQrCode qrCode = new OrderQrCode(expiredSupport, marketId, orderId, accountId);
        qrCode.pack();
        return qrCode;
    }

    public static void main(String[] args) {
        OrderQrCode qrCode = OrderQrCode.of(false, 8, 123456, 654321);
        String qrCodeStr = qrCode.toString();
        System.out.println(qrCodeStr);
        qrCode = OrderQrCode.from(qrCodeStr);
        System.out.println(qrCode.getMarketId());
        System.out.println(qrCode.getOrderId());
        System.out.println(qrCode.getAccountId());
    }
}
