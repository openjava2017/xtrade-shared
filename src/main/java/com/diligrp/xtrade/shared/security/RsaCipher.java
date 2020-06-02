package com.diligrp.xtrade.shared.security;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA算法工具类
 *
 * @author: brenthuang
 * @date: 2017/12/28
 */
public class RsaCipher {
    private static final String KEY_ALGORITHM = "RSA";

    private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    public static String[] generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(512, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        String[] keyPairArray = new String[2];
        keyPairArray[0] = HexUtils.encodeHexStr(privateKey.getEncoded(), false);
        keyPairArray[1] = HexUtils.encodeHexStr(publicKey.getEncoded(), false);

        return keyPairArray;
    }

    public static byte[] encrypt(byte[] data, Key secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, Key secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initSign(privateKey, new SecureRandom());
        signature.update(data);
        return signature.sign();
    }

    public static boolean verify(byte[] data, byte[] sign, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(sign);
    }

    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes = HexUtils.decodeHex(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes = HexUtils.decodeHex(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }
}
