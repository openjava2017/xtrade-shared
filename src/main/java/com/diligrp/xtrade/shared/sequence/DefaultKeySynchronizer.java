package com.diligrp.xtrade.shared.sequence;

import com.diligrp.xtrade.shared.dao.ISequenceKeyDao;
import com.diligrp.xtrade.shared.domain.PersistentSequenceKey;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * SequenceKey数据同步的实现类
 *
 * @author: brenthuang
 * @date: 2020/03/24
 */
public class DefaultKeySynchronizer implements IKeySynchronizer {

    private ISequenceKeyDao sequenceKeyDao;

    public DefaultKeySynchronizer() {
    }

    public DefaultKeySynchronizer(ISequenceKeyDao sequenceKeyDao) {
        this.sequenceKeyDao = sequenceKeyDao;
    }

    /**
     * {@inheritDoc}
     *
     * 乐观锁实现需Spring事务传播属性使用REQUIRES_NEW，数据库事务隔离级别READ_COMMITTED
     * 为了防止业务层事务的数据隔离级别干扰导致数据的重复读（无法读取到最新的数据记录），
     * 因此新启一个Spring事务（一个新数据库连接）并将数据隔离级别设置成READ_COMMITTED;
     * Mysql默认隔离级别为REPEATABLE_READ
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public PersistentSequenceKey loadSequenceKey(String key, String scope) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("key", key);
        params.put("scope", scope);
        return sequenceKeyDao.loadSequenceKey(params);
    }

    /**
     * {@inheritDoc}
     *
     * 乐观锁实现需Spring事务传播属性使用REQUIRES_NEW，数据库事务隔离级别READ_COMMITTED
     * 为了防止业务层事务的数据隔离级别干扰导致数据的重复读（无法读取到最新的数据记录），
     * 因此新启一个Spring事务（一个新数据库连接）并将数据隔离级别设置成READ_COMMITTED;
     * Mysql默认隔离级别为REPEATABLE_READ
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public PersistentSequenceKey findSequenceKeyById(Long id) {
        return sequenceKeyDao.findSequenceKeyById(id);
    }

    /**
     * {@inheritDoc}
     *
     * 乐观锁实现需Spring事务传播属性使用REQUIRES_NEW
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public boolean compareAndSet(Long id, Long newValue, Long version) {
        Map<String, Object> params = new HashMap<>(4);
        params.put("id", id);
        params.put("newValue", newValue);
        params.put("version", version);
        return sequenceKeyDao.compareAndSet(params) > 0;
    }

    /**
     * {@inheritDoc}
     *
     * 数据库的行锁只有在事务提交之后才会释放，如果这里使用业务层的Spring事务行锁将不能很快释放，这样势必会降低此代码块的并发性能。
     * 因此新开一个Spring事务，与业务层事务独立以提高此SequenceKey的并发性
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public PersistentSequenceKey synchronizeSequenceKey(Long id) {
        //悲观锁添加行锁 - 多JVM多线程场景下自动实现线程同步
        //通过SELECT FOR UPDATE锁定了行，当事务提交时将自动释放行锁
        PersistentSequenceKey persistentKey = sequenceKeyDao.lockSequenceKey(id);

        if (persistentKey != null) {
            //当PersistentKey过期, 则startWith重新设置成1并刷新过期日期为今天，否则设置startWith+=incSpan
            //SequenceKey的过期日期不等于今天则认为过期，而不是严格判断日期的先后
            LocalDate today = persistentKey.getToday();
            if (!today.equals(persistentKey.getExpiredDate())) {
                persistentKey.setStartWith(1L);
            }

            Long startWith = persistentKey.getStartWith();
            Long newStartWith = startWith + persistentKey.getIncSpan();
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            params.put("newValue", newStartWith);
            params.put("expiredDate", today);

            sequenceKeyDao.unlockSequenceKey(params);
        }

        return persistentKey;
    }
}
