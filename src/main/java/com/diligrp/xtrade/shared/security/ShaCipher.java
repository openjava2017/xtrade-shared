package com.diligrp.xtrade.shared.security;

import java.security.MessageDigest;

/**
 * SHA散列算法工具类
 *
 * @author: brenthuang
 * @date: 2017/12/28
 */
public class ShaCipher {
    private static final String KEY_SHA = "SHA";

    public static byte[] encrypt(byte[] data) throws Exception {
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
        sha.update(data);

        return sha.digest();
    }
}
