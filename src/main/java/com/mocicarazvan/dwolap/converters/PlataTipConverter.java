package com.mocicarazvan.dwolap.converters;

import com.mocicarazvan.dwolap.enums.PlataTip;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;


@Converter(autoApply = true)
@Component
public class PlataTipConverter extends StringEnumConverter<PlataTip> {

    public PlataTipConverter() {
        super(PlataTip.class);
    }
}
