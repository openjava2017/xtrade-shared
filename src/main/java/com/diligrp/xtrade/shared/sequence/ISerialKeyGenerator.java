package com.diligrp.xtrade.shared.sequence;

import java.time.LocalDate;

/**
 * SerialKey基础类
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public interface ISerialKeyGenerator {
    /**
     * 获取下一个序列号
     *
     * @return 下一个序列号
     */
    String nextSerialNo(IDatedIdStrategy strategy);

    public static interface IDatedIdStrategy {
        String id(LocalDate date, long sequence);
    }
}
