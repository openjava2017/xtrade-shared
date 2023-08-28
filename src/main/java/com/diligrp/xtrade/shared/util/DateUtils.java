package com.diligrp.xtrade.shared.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期格式转化工具类 - JDK1.8 TIME API
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public class DateUtils {
    public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public final static String YYYY_MM_DD = "yyyy-MM-dd";

    public final static String YYYYMMDD = "yyyyMMdd";

    public static String formatDateTime(LocalDateTime when, String format) {
        if (ObjectUtils.isNull(when)) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return when.format(formatter);
    }

    public static String formatDateTime(LocalDateTime when) {
        return formatDateTime(when, YYYY_MM_DD_HH_MM_SS);
    }

    public static String formatDate(LocalDate when, String format) {
        if (ObjectUtils.isNull(when)) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return when.format(formatter);
    }

    public static String formatDate(LocalDate when) {
        return formatDate(when, YYYY_MM_DD);
    }

    public static String formatNow(String format) {
        return formatDateTime(LocalDateTime.now(), format);
    }

    public static String formatNow() {
        return formatNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static String format(Date date) {
        return format(date, YYYY_MM_DD_HH_MM_SS);
    }
    
    public static LocalDateTime addDays(long amount) {
    	LocalDateTime localDateTime = LocalDateTime.now();
    	return localDateTime.plusDays(amount);
    }

    public static String format(Date date, String format) {
        if (ObjectUtils.isNull(date)) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);

    }

    public static LocalDateTime parseDateTime(String datetimeStr, String format) {
        if (ObjectUtils.isEmpty(datetimeStr)) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(datetimeStr, formatter);
    }

    public static LocalDateTime parseDateTime(String datetimeStr) {
        return parseDateTime(datetimeStr, YYYY_MM_DD_HH_MM_SS);
    }

    public static LocalDate parseDate(String dateStr, String format) {
        if (ObjectUtils.isEmpty(dateStr)) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(dateStr, formatter);
    }

    public static LocalDate parseDate(String dateStr) {
        return parseDate(dateStr, YYYY_MM_DD);
    }

    public static Date parse(String dateStr) {
        return parse(dateStr, YYYY_MM_DD_HH_MM_SS);
    }

    public static Date parse(String dateStr, String format) {
        if (ObjectUtils.isEmpty(dateStr)) {
            return null;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateStr);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid date format", ex);
        }
    }

    /**
    * 获取时间戳
    */
    public static long parseMilliSecond(LocalDateTime localDateTime){
        return parseMilliSecond(localDateTime,null);
    }

    public static long parseMilliSecond(LocalDateTime localDateTime, String zoneNumStr){
        //默认东八区
        if (ObjectUtils.isEmpty(zoneNumStr)){
            zoneNumStr = "+8";
        }
        return localDateTime.toInstant(ZoneOffset.of(zoneNumStr)).toEpochMilli();
    }
}
