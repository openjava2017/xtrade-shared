package com.diligrp.xtrade.shared.config;

import com.diligrp.xtrade.shared.domain.BaseDo;
import com.diligrp.xtrade.shared.util.JsonUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * @Auther: miaoguoxin
 * @Date: 2020/3/18 20:27
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "xtrade", name = "json.format",havingValue = "true",matchIfMissing = true)
@Import(JacksonAutoConfiguration.JacksonBuilderConfiguration.class)
public class JacksonAutoConfiguration {
    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void customJacksonObjectMapper() {
        // 为mapper注册一个带有SerializerModifier的Factory
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory());
          //  .withSerializerModifier(new JsonUtils.NullBeanSerializerModifier()));
    }


    static class JacksonBuilderConfiguration {
        @Bean
        @ConditionalOnClass(JavaTimeModule.class)
        Jackson2ObjectMapperBuilderCustomizer customizeLocalDateTimeFormat(){
            return JsonUtils::initObjectMapperBuilder;
        }
    }

}
