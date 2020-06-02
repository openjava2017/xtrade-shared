package com.diligrp.xtrade.shared.type;

/**
 * Mybatis自定义TypeHandler实现枚举对象-数值转化的基础类
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public interface IEnumType {

    /**
     * 枚举对象对应的数值
     *
     * @return 返回枚举对象对应的数值
     */
    int getCode();

    /**
     * 枚举对象对应的描述
     *
     * @return 返回枚举对象对应的描述
     */
    String getName();

    /**
     * 枚举对象字符串描述
     *
     * @return 返回枚举对象字符串描述
     */
    @Override
    String toString();
}
