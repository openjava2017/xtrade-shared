package com.diligrp.xtrade.shared.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * AES算法工具类
 *
 * @author: brenthuang
 * @date: 2017/12/28
 */
public class AesCipher {
    private static final String KEY_ALGORITHM = "AES";

    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    public static String generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        keyGenerator.init(128);

        SecretKey secretKey = keyGenerator.generateKey();
        return HexUtils.encodeHexStr(secretKey.getEncoded(), false);
    }

    public static byte[] encrypt(byte[] data, String secretKey) throws Exception {
        Key key = toKey(secretKey);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, String secretKey) throws Exception {
        Key key = toKey(secretKey);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(data);
    }

    private static Key toKey(String secretKey) {
        byte[] key = HexUtils.decodeHex(secretKey);
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }

    public static void main(String[] args) throws Exception {
        String key = generateSecretKey();
        System.out.println(HexUtils.encodeHex(encrypt("5hjakla05hjakla0".getBytes("UTF-8"), key)).length);
    }
}
