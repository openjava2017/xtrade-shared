package com.diligrp.xtrade.shared.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字符串-数值转化工具类
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public class NumberUtils {
    private static Logger LOG = LoggerFactory.getLogger(NumberUtils.class);

    public static int str2Int(String number, int defaultValue) {
        if (ObjectUtils.isEmpty(number)) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            // Never ignore any exception
            LOG.error("Invalid number format", nfe);
            return defaultValue;
        }
    }

    public static long str2Long(String number, long defaultValue) {
        if (ObjectUtils.isEmpty(number)) {
            return defaultValue;
        }

        try {
            return Long.parseLong(number);
        } catch (NumberFormatException nfe) {
            // Never ignore any exception
            LOG.error("Invalid number format", nfe);
            return defaultValue;
        }
    }

    public static boolean isNumeric(String str) {
        if (ObjectUtils.isEmpty(str)) {
            return false;
        } else {
            int sz = str.length();

            for(int i = 0; i < sz; ++i) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }
}
