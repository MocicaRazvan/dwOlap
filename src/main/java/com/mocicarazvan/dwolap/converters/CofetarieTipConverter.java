package com.mocicarazvan.dwolap.converters;

import com.mocicarazvan.dwolap.enums.CofetarieTip;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;


@Converter(autoApply = true)
@Component
public class CofetarieTipConverter extends StringEnumConverter<CofetarieTip> {

    public CofetarieTipConverter() {
        super(CofetarieTip.class);
    }
}
