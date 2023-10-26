package com.diligrp.xtrade.shared.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * 金额格式转化工具类
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public class CurrencyUtils {
    private static final int DEFAULT_SCALE = 2;
    private static final Locale CURRENT_LOCALE = Locale.CHINA;
    private static final BigDecimal YUAN_CENT_UNIT = new BigDecimal(100);

    public static String toCurrency(Long cent) {
        if (cent == null) {
            return null;
        }

        BigDecimal amount = new BigDecimal(cent);
        BigDecimal yuan = amount.divide(YUAN_CENT_UNIT).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(CURRENT_LOCALE);
        StringBuffer currency = new StringBuffer();
        numberFormat.format(yuan, currency, new FieldPosition(0));
        if (cent < 0) {
            correctSymbol(currency);
        }
        return currency.toString();
    }

    /**
     * 100000 -> 1,000.00
     */
    public static String toNoSymbolCurrency(Long cent) {
        if (cent == null) {
            return null;
        }

        BigDecimal amount = new BigDecimal(cent);
        BigDecimal yuan = amount.divide(YUAN_CENT_UNIT).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(CURRENT_LOCALE);
        StringBuffer currency = new StringBuffer();
        numberFormat.format(yuan, currency, new FieldPosition(0));
        if (cent < 0) {
            correctSymbol(currency);
        }
        return currency.substring(1);
    }

    /**
     * 100000 -> 1000.00
     */
    public static String cent2TenNoSymbol(Long cent) {

        BigDecimal yuan = point2ten(cent);
        if (null == yuan) {
            yuan = new BigDecimal(0L);
        }
        return yuan.toString();
    }

    public static Long yuan2Cent(BigDecimal yuan) {
        if (yuan == null) {
            return null;
        }

        BigDecimal amount = yuan.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
        BigDecimal cent = amount.multiply(YUAN_CENT_UNIT);
        return cent.longValue();
    }

    private static BigDecimal point2ten(Long point) {
        if (null == point) {
            point = 0L;
        }
        BigDecimal centBigDecimal = new BigDecimal(point);
        BigInteger divisor = BigInteger.valueOf(1L);
        for (int i = 0; i < DEFAULT_SCALE; i++) {
            divisor = divisor.multiply(BigInteger.valueOf(10L));
        }
        return centBigDecimal.divide(new BigDecimal(divisor)).setScale(DEFAULT_SCALE);
    }

    public static String convert2Percent(Long total, Long percentNumber) {
        if (percentNumber == 0L || total == 0L) {
            return "0.00%";
        }
        double percent = percentNumber.doubleValue() / total.doubleValue() * 100;
        BigDecimal bigDecimal = new BigDecimal(percent);
        return bigDecimal.setScale(2, RoundingMode.HALF_UP) + "%";
    }

    /**
     * $-100.00 => -$100.00
     */
    private static void correctSymbol(StringBuffer currency) {
        char negativeSymbol = currency.charAt(0);
        char currencySymbol = currency.charAt(1);
        currency.setCharAt(0, currencySymbol);
        currency.setCharAt(1, negativeSymbol);
    }
}
