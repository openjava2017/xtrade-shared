package com.diligrp.xtrade.shared.config;

import com.diligrp.xtrade.shared.dao.ISequenceKeyDao;
import com.diligrp.xtrade.shared.sequence.DefaultKeySynchronizer;
import com.diligrp.xtrade.shared.sequence.IKeySynchronizer;
import com.diligrp.xtrade.shared.sequence.KeyGeneratorManager;
import com.diligrp.xtrade.shared.util.ClassUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Auther: miaoguoxin
 * @Date: 2020/3/25 10:25
 * @Description: id生成器自动配置类
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnSingleCandidate(SqlSessionFactory.class)
@AutoConfigureAfter({MybatisAutoConfiguration.class})
@ConditionalOnProperty(prefix = "xtrade", name = "key-generator.enable", havingValue = "true")
public class KeyGeneratorAutoConfiguration {

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Bean(name = "sequenceKeyDao")
    @ConditionalOnMissingBean
    public ISequenceKeyDao sequenceKeyDao() {
        MapperFactoryBean<ISequenceKeyDao> factoryBean = new MapperFactoryBean<ISequenceKeyDao>();
        factoryBean.setMapperInterface(ISequenceKeyDao.class);
        factoryBean.setSqlSessionFactory(sqlSessionFactory);

        // 解析mapper文件并注册至sqlSessionFactory
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        org.springframework.core.io.Resource mapperLocation =
                new ClassPathResource("com/diligrp/xtrade/shared/dao/ISequenceKeyDao.xml", classLoader);
        boolean exists = mapperLocation.exists();
        if (exists) {
            org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
            try {
                XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(),
                        configuration, mapperLocation.toString(), configuration.getSqlFragments());
                xmlMapperBuilder.parse();
            } catch (IOException iex) {
                throw new BeanCreationException("sequenceKeyDao mapper xml file read and parse failure", iex);
            }
        }

        try {
            return factoryBean.getObject();
        } catch (Exception ex) {
            throw new BeanCreationException("sequenceKeyDao bean cannot be created", ex);
        }
    }

    @Bean(name = "keySynchronizer")
    @ConditionalOnMissingBean
    public IKeySynchronizer keySynchronizer(ISequenceKeyDao sequenceKeyDao){
        return new DefaultKeySynchronizer(sequenceKeyDao);
    }

    @Bean(name = "keyGeneratorManager")
    @ConditionalOnMissingBean
    public KeyGeneratorManager keyGeneratorManager(IKeySynchronizer keySynchronizer) {
        return new KeyGeneratorManager(keySynchronizer);
    }
}
