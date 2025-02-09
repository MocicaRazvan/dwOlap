package com.mocicarazvan.dwolap.converters;

import com.mocicarazvan.dwolap.enums.EnumGetValue;
import jakarta.persistence.AttributeConverter;

import java.util.Arrays;


public abstract class StringEnumConverter<E extends Enum<E> & EnumGetValue> implements AttributeConverter<E, String> {
    private final Class<E> attributeClass;

    protected StringEnumConverter(Class<E> attributeClass) {
        this.attributeClass = attributeClass;
    }


    @Override
    public String convertToDatabaseColumn(E attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public E convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }


        return Arrays.stream(attributeClass.getEnumConstants())
                .filter(e -> e.getValue().equalsIgnoreCase(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value: " + dbData));
    }
}
