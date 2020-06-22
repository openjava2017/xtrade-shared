package com.diligrp.xtrade.shared.domain;

import com.diligrp.xtrade.shared.constant.Constants;
import com.diligrp.xtrade.shared.exception.MessageEnvelopException;
import com.diligrp.xtrade.shared.security.RsaCipher;
import com.diligrp.xtrade.shared.util.AssertUtils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * 消息信封模型将安全数据与业务数据进行分离
 * 用于接口数据安全校验：数据签名验签，防止数据在传输过程中被篡改
 *
 * @author: brenthuang
 * @date: 2020/04/03
 */
public class MessageEnvelop {
    /**
     * 调用者APPID：消息发送者标识
     */
    private Long appId;

    /**
     * 消息接收者：请求的服务点标识
     * 格式：componentId:methodName
     */
    private String recipient;

    /**
     * 服务访问Token
     */
    private String accessToken;

    /**
     * 签名数据-BASE64编码
     */
    private String signature;

    /**
     * 业务数据-BASE64编码
     */
    private String payload;

    /**
     * 业务数据字符流的编码格式
     */
    private String charset;

    /**
     * 数据信封状态
     */
    private Integer state = 0;

    /**
     * 数据封包，根据我方的私钥进行数据签名
     *
     * @param privateKey - Base64编码的私钥字符串
     */
    public void packEnvelop(String privateKey) {
        AssertUtils.notEmpty(privateKey, "privateKey missed");
        AssertUtils.notEmpty(payload, "payload missed");

        try {
            byte[] data = payload.getBytes(getCharset());
            PrivateKey secretKey = RsaCipher.getPrivateKey(privateKey);
            byte[] sign = RsaCipher.sign(data, secretKey);
            this.signature = Base64.getEncoder().encodeToString(sign);
        } catch (MessageEnvelopException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new MessageEnvelopException("数据封包失败: 签名失败", ex);
        }
    }

    /**
     * 数据拆包，根据对方的公钥进行数据验签
     *
     * @param publicKey - Base64编码的公钥字符串
     */
    public void unpackEnvelop(String publicKey) {
        AssertUtils.notEmpty(publicKey, "publicKey missed");
        AssertUtils.notEmpty(payload, "payload missed");
        AssertUtils.notEmpty(signature, "signature missed");

        try {
            byte[] data = payload.getBytes(getCharset());
            byte[] sign = Base64.getDecoder().decode(signature);
            PublicKey secretKey = RsaCipher.getPublicKey(publicKey);
            boolean result = RsaCipher.verify(data, sign, secretKey);
            if (!result) {
                throw new MessageEnvelopException(1010, "数据拆包异常：验签失败");
            }
        } catch (MessageEnvelopException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new MessageEnvelopException("数据拆包失败", ex);
        }
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getCharset() {
        return charset == null ? Constants.CHARSET_UTF8 : charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public static MessageEnvelop of(String recipient, String payload) {
        return MessageEnvelop.of(recipient, payload, Constants.CHARSET_UTF8);
    }

    public static MessageEnvelop of(String recipient, String payload, String charset) {
        return MessageEnvelop.of(null, recipient, null, payload, null, charset);
    }

    public static MessageEnvelop of(Long appId, String recipient, String accessToken, String payload, String signature) {
        return MessageEnvelop.of(null, recipient, null, payload, signature, Constants.CHARSET_UTF8);
    }

    public static MessageEnvelop of(Long appId, String recipient, String accessToken,
                                    String payload, String signature, String charset) {
        MessageEnvelop envelop = new MessageEnvelop();
        envelop.setAppId(appId);
        envelop.setRecipient(recipient);
        envelop.setAccessToken(accessToken);
        envelop.setPayload(payload);
        envelop.setCharset(charset);
        envelop.setState(0);
        return envelop;
    }
}