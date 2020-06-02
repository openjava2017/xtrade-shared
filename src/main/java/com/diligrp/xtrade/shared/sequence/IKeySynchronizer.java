package com.diligrp.xtrade.shared.sequence;

import com.diligrp.xtrade.shared.domain.PersistentSequenceKey;

/**
 * SequenceKey数据同步基础类
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public interface IKeySynchronizer {
    /**
     * 加载指定的SequenceKey
     *
     * @param key - SequenceKey的唯一标识
     * @param scope - 使用范围
     * @return SequenceKey
     */
    PersistentSequenceKey loadSequenceKey(String key, String scope);

    /**
     * 根据KeyId查询SequenceKey
     *
     * @param id - KeyId
     * @return SequenceKey
     */
    PersistentSequenceKey findSequenceKeyById(Long id);

    /**
     * 乐观锁实现 - 比较并设置某个SequenceKey的起始值
     *
     * @param id - SequenceKey的数据库主键
     * @param newValue - SequenceKey起始值的新值
     * @param version - 原数据版本
     * @return true-设置成功，false-设置失败
     */
    boolean compareAndSet(Long id, Long newValue, Long version);


    /**
     * 通过悲观锁实现同步从数据库获取基于过期日期的SequenceKey
     *
     * 根据数据库主键锁定数据记录(加行锁)，根据SequenceKey的过期日期更新下一个startWith值
     * 当SequenceKey过期时startWith将重新设置为1，否则startWith + 1
     *
     * @param id - KeyId
     * @return SequenceKey
     */
    PersistentSequenceKey synchronizeSequenceKey(Long id);
}
