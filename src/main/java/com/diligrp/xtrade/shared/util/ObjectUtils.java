package com.diligrp.xtrade.shared.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用工具类
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public class ObjectUtils {
    public static boolean equals(String str1, String str2) {
        if (str1 == str2) {
            return true;
        } else if (str1 != null && str2 != null) {
            if (str1.length() != str2.length()) {
                return false;
            } else if (str1 instanceof String && str2 instanceof String) {
                return str1.equals(str2);
            } else {
                int length = str1.length();

                for(int i = 0; i < length; ++i) {
                    if (str1.charAt(i) != str2.charAt(i)) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public static boolean equals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }
        return object1 != null && object2 != null ? object1.equals(object2) : false;
    }

    public static String[] split(String str, char separator) {
        if (str == null) {
            return null;
        } else {
            int len = str.length();
            if (len == 0) {
                return new String[0];
            } else {
                int i = 0;
                int start = 0;
                boolean match = false;
                boolean lastMatch = false;
                boolean preserveAllTokens = false;
                List<String> list = new ArrayList<String>();

                while(true) {
                    while(i < len) {
                        if (str.charAt(i) == separator) {
                            if (match || preserveAllTokens) {
                                list.add(str.substring(start, i));
                                match = false;
                                lastMatch = true;
                            }

                            ++i;
                            start = i;
                        } else {
                            lastMatch = false;
                            match = true;
                            ++i;
                        }
                    }

                    if (match || preserveAllTokens && lastMatch) {
                        list.add(str.substring(start, i));
                    }

                    return (String[])list.toArray(new String[list.size()]);
                }
            }
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static <T> boolean isEmpty(List<T> array) {
        return array == null || array.isEmpty();
    }

    public static <T> boolean isNotEmpty(List<T> array) {
        return array != null && !array.isEmpty();
    }

    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    public static boolean isNull(Object obj) {
        return null == obj;
    }
}
