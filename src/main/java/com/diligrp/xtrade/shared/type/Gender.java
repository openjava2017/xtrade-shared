package com.diligrp.xtrade.shared.type;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author: brenthuang
 * @date: 2020/03/24
 */
public enum Gender implements IEnumType {

    //男
    MALE("男", 1),

    //女
    FEMALE("女", 2);

    private String name;
    private int code;

    Gender(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static Optional<Gender> getGender(int code) {
        Stream<Gender> GENDERS = Arrays.stream(Gender.values());
        return GENDERS.filter(gender -> gender.getCode() == code).findFirst();
    }

    public static String getName(int code) {
        Stream<Gender> GENDERS = Arrays.stream(Gender.values());
        Optional<String> result = GENDERS.filter(gender -> gender.getCode() == code)
                .map(Gender::getName).findFirst();
        return result.isPresent() ? result.get() : null;
    }

    public static List<Gender> getGenderList() {
        return Arrays.asList(Gender.values());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return name;
    }
}
