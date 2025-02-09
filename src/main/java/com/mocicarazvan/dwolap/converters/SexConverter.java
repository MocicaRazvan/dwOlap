package com.mocicarazvan.dwolap.converters;

import com.mocicarazvan.dwolap.enums.SexTip;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;


@Converter(autoApply = true)
@Component
public class SexConverter extends StringEnumConverter<SexTip> {

    public SexConverter() {
        super(SexTip.class);
    }
}
