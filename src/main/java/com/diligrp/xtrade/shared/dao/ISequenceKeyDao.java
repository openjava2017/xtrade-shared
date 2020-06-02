package com.diligrp.xtrade.shared.dao;

import com.diligrp.xtrade.shared.domain.PersistentSequenceKey;
import com.diligrp.xtrade.shared.mybatis.MybatisMapperSupport;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * KeySequence数据操作
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public interface ISequenceKeyDao extends MybatisMapperSupport {
    /**
     * 加载指定的SequenceKey
     *
     * @param params - 参数列表：key/scope
     * @return SequenceKey
     */
    PersistentSequenceKey loadSequenceKey(Map<String, Object> params);

    /**
     * 根据KeyId查询SequenceKey
     *
     * @param id - KeyId
     * @return SequenceKey
     */
    PersistentSequenceKey findSequenceKeyById(Long id);

    /**
     * 比较并设置某个SequenceKey的起始值，数据库乐观锁简易实现
     *
     * @param params - 参数列表：id/newValue/version
     * @return 1-更新成功，0-更新失败
     */
    int compareAndSet(Map<String, Object> params);

    /**
     * 悲观锁实现 - 根据数据库主键锁定数据记录
     *
     * @param id - KeyId
     * @return SequenceKey
     */
    PersistentSequenceKey lockSequenceKey(Long id);

    /**
     * 悲观锁解锁实现 - 根据数据库主键解锁数据记录
     *
     * @param params - 参数列表：id/newValue/expiredDate
     * @return 1-更新成功，0-更新失败
     */
    int unlockSequenceKey(Map<String, Object> params);
}
