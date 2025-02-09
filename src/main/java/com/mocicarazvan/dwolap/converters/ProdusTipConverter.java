package com.mocicarazvan.dwolap.converters;

import com.mocicarazvan.dwolap.enums.ProdusTip;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;


@Converter(autoApply = true)
@Component
public class ProdusTipConverter extends StringEnumConverter<ProdusTip> {

    public ProdusTipConverter() {
        super(ProdusTip.class);
    }
}
