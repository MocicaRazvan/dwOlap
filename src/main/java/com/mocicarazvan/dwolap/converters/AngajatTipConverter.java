package com.mocicarazvan.dwolap.converters;

import com.mocicarazvan.dwolap.enums.AngajatTip;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;


@Converter(autoApply = true)
@Component
public class AngajatTipConverter extends StringEnumConverter<AngajatTip> {

    public AngajatTipConverter() {
        super(AngajatTip.class);
    }
}
