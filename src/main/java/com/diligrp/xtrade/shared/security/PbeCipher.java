package com.diligrp.xtrade.shared.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;
import java.util.Random;

/**
 * PBE算法工具类
 *
 * @author: brenthuang
 * @date: 2017/12/28
 */
public class PbeCipher {
    private static final String ALGORITHM = "PBEWithSHA1AndDESede";

    public static byte[] encrypt(byte[] data, String password, byte[] salt) throws Exception {
        Key key = toKey(password);

        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, String password, byte[] salt) throws Exception {
        Key key = toKey(password);

        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

        return cipher.doFinal(data);
    }

    private static byte[] initSalt() throws Exception {
        byte[] salt = new byte[8];
        Random random = new Random();
        random.nextBytes(salt);
        return salt;
    }

    private static Key toKey(String password) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey secretKey = keyFactory.generateSecret(keySpec);

        return secretKey;
    }
}
