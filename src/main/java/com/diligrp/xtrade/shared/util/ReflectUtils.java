package com.diligrp.xtrade.shared.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 利用反射进行操作的一个工具类
 */
public class ReflectUtils {
    /**
     * 利用反射获取指定对象的指定属性
     */
    public static Object getFieldValue(Object target, String fieldName) {
        Object result = null;
        Field field = ReflectUtils.getField(target, fieldName);
        if (field != null) {
            field.setAccessible(true);
            try {
                result = field.get(target);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new RuntimeException("Illegal access or argument exception", ex);
            }
        }
        return result;
    }

    /**
     * 利用反射获取指定对象里面的指定属性
     */
    private static Field getField(Object obj, String fieldName) {
        Field field = null;
        for (Class<?> clazz = obj.getClass();
             clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                // 当前类没有此方法则向父类查找，都没有就返回NULL
            }
        }
        return field;
    }

    /**
     * 利用反射设置指定对象的指定属性为指定的值
     */
    public static void setFieldValue(Object obj, String fieldName, String fieldValue) {
        Field field = ReflectUtils.getField(obj, fieldName);
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(obj, fieldValue);
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new RuntimeException("Illegal access or argument exception", ex);
            }
        }
    }

    /**
     * 调用对象方法, 包含private/protected修饰的方法.
     */
    public static Object invokeMethod(final Object target, final String methodName, final Class<?>[] parameterTypes,
        final Object[] parameters) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        Method method = getDeclaredMethod(target, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method ["
                + methodName + "] parameterType " + Arrays.toString(parameterTypes)
                + " on target [" + target + "]");
        }
        method.setAccessible(true);
        return method.invoke(target, parameters);
    }

    /**
     * 在target对象上查找方法，如果当前类定义未定义则向父类查找，都未查找到则返回Null.
     */
    protected static Method getDeclaredMethod(Object target, String methodName, Class<?>[] parameterTypes) {
        AssertUtils.notNull(target, "target must be not null");
        for (Class<?> superClass = target.getClass(); superClass != Object.class;
             superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException sme) {
                // 当前类未定义Method则向父类查找
            }
        }
        return null;
    }
}