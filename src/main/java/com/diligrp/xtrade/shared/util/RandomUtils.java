package com.diligrp.xtrade.shared.util;

import java.util.Random;
import java.util.UUID;

/**
 * @Auther: miaoguoxin
 * @Date: 2019/7/20 22:16
 */
public class RandomUtils {
    /**
     * 生成随机字符串, a-z A-Z 0-9
     */
    public static String randomString(int length){
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++){
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }


    /**
     * 生成8位随机码（数字0~9+大写字母A~Z）
     */
    public static String randomCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int a = Math.abs((new Random()).nextInt(32));
            if (a <= 9) {
                sb.append((char) (a + 48));
            } else if (a < 33) {
                if ((a + 55) == 79 || (a + 55) == 73) {
                    sb.append((char) (a + 63));
                } else {
                    sb.append((char) (a + 55));
                }
            }
        }
        return sb.toString();
    }


    /**
     * 功能描述:获取随机字符串
     */
    public static String randomString(int length, String original) {
        if (length <= 0 || ObjectUtils.isEmpty(original)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int len = original.length();
        for (int i = 0; i < length; i++) {
            int round = (int) Math.round(Math.random() * (len - 1));
            sb.append(original.charAt(round));
        }
        return sb.toString();
    }

    /**
     * UUID随机数，移除 "-"
     */
    public static String randomUUID() {
        UUID uuid = UUID.randomUUID();
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();
        return (digits(mostSigBits >> 32, 8) +
                digits(mostSigBits >> 16, 4) +
                digits(mostSigBits, 4) +
                digits(leastSigBits >> 48, 4)  +
                digits(leastSigBits, 12));
    }

    public static String randomCaptcha() {
        return String.valueOf((int) ((Math.random() * ((1 << 3) + 1) + 1) * 1000));
    }

    /**
    * 生成随机区间数字
    */
    public static int randomInt(int min, int max) {
        return min + (int)(Math.random() * (max + 1 - min));
    }

    private static String digits(long val, int digits)
    {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1).toUpperCase();
    }
}