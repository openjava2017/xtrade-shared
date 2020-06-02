package com.diligrp.xtrade.shared.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JsonUtils {
    private static ObjectMapper objectMapper = initObjectMapper();


    private static ObjectMapper initObjectMapper(){
        Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder = new Jackson2ObjectMapperBuilder();
        initObjectMapperBuilder(jackson2ObjectMapperBuilder);
        ObjectMapper objectMapper = jackson2ObjectMapperBuilder.createXmlMapper(false).build();
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory());
                //.withSerializerModifier(new NullBeanSerializerModifier()));
        return objectMapper;
    }


    public static void initObjectMapperBuilder(Jackson2ObjectMapperBuilder builder){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        builder.serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        builder.deserializerByType(LocalDateTime.class,new LocalDateTimeDeserializer(formatter));
        builder.timeZone(TimeZone.getTimeZone("GMT+8"));
        //序列化java.util.Date类型
        builder.dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.featuresToDisable(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                SerializationFeature.FAIL_ON_EMPTY_BEANS
        );
        builder.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }


    public static <T> T fromJsonString(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Deserialize json exception", ex);
        }
    }

    public static <T> T fromJsonString(String json, TypeReference<T> jsonTypeReference){
        try {
            return objectMapper.readValue(json, jsonTypeReference);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Deserialize json array exception", ex);
        }
    }

    public static <T> String toJsonString(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Serialize json exception", ex);
        }
    }

    /**
     * Description: 此modifier主要做的事情为：
     * 当序列化类型为array，list、set时，当值为空时，序列化成[]
     * 为date,localdate,localdatetime 时，当值为空时，序列化成0
     * 为数值类型时，当值为空时，序列化成0
     * 为字符类型时，当值为空时，序列化成“”
     */
    public static class NullBeanSerializerModifier extends BeanSerializerModifier {
        private JsonSerializer<Object> arrayJsonSerializer = new ArrayJsonSerializer();
        private JsonSerializer<Object> strJsonSerializer = new StrJsonSerializer();
       // private JsonSerializer<Object> dateJsonSerializer = new DateJsonSerializer();
        private JsonSerializer<Object> numberJsonSerializer = new NumJsonSerializer();
        private JsonSerializer<Object> mapJsonSerializer = new MapJsonSerializer();
        @Override
        public List<BeanPropertyWriter> changeProperties(
                SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
            // 循环所有的beanPropertyWriter
            for (BeanPropertyWriter writer : beanProperties) {
                // 判断字段的类型，如果是array，list，set则注册nullSerializer
                if (isArrayType(writer)) {
                    //给writer注册一个自己的nullSerializer
                    writer.assignNullSerializer(arrayJsonSerializer);
                } else if (isNumber(writer)) {
                    writer.assignNullSerializer(numberJsonSerializer);
                } else if (isStr(writer)) {
                    writer.assignNullSerializer(strJsonSerializer);
                } else if (isMap(writer)){
                    writer.assignNullSerializer(mapJsonSerializer);
                }
            }
            return beanProperties;
        }

        /**
         * 数组以及集合序列化实现类
         */
        public class ArrayJsonSerializer extends JsonSerializer<Object> {
            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                if (value == null) {
                    jgen.writeStartArray();
                    jgen.writeEndArray();
                } else {
                    jgen.writeObject(value);
                }
            }
        }

        /**
         * 字符串序列化实现类
         */
        public class StrJsonSerializer extends JsonSerializer<Object> {
            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                if (value == null) {
                    jgen.writeString("");
                } else {
                    jgen.writeObject(value);
                }
            }
        }

        /**
         * 数字序列化实现类
         */
        public class NumJsonSerializer extends JsonSerializer<Object> {
            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                if (value == null) {
                    jgen.writeNumber(0);
                } else {
                    jgen.writeObject(value);
                }
            }
        }

        /**
         * 数字序列化实现类
         */
        public class MapJsonSerializer extends JsonSerializer<Object> {
            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                if (value == null) {
                    jgen.writeObject(new HashMap<>());
                } else {
                    jgen.writeObject(value);
                }
            }
        }

        /**
         * 判断数组类型
         */
        boolean isArrayType(BeanPropertyWriter writer) {
            Class<?> clazz = writer.getType().getRawClass();
            return clazz.isArray() || clazz.equals(List.class) || clazz.equals(Set.class);
        }

        /**
         * 判断数字类型
         */
        boolean isNumber(BeanPropertyWriter writer) {
            Class<?> clazz = writer.getType().getRawClass();
            return clazz.equals(Short.class) || clazz.equals(Integer.class)
                    || clazz.equals(Long.class) || clazz.equals(BigDecimal.class)
                    || clazz.equals(Double.class) || clazz.equals(Float.class);
        }

        /**
         * 判断字符类型
         */
        boolean isStr(BeanPropertyWriter writer) {
            Class<?> clazz = writer.getType().getRawClass();
            return clazz.equals(String.class) || clazz.equals(Character.class)
                    || clazz.equals(StringBuilder.class) || clazz.equals(StringBuffer.class);
        }

        /**
         * 判断字符类型
         */
        boolean isMap(BeanPropertyWriter writer) {
            Class<?> clazz = writer.getType().getRawClass();
            return clazz.equals(Map.class) ;
        }

    }
}
