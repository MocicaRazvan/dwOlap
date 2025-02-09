package com.mocicarazvan.dwolap.converters.factories;


import com.mocicarazvan.dwolap.converters.*;
import com.mocicarazvan.dwolap.enums.EnumGetValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConvertorFactory {

    private final AngajatTipConverter angajatTipConverter;
    private final CofetarieTipConverter cofetarieTipConverter;
    private final PlataTipConverter plataTipConverter;
    private final ProdusTipConverter produsTipConverter;
    private final SexConverter sexConverter;

    @SuppressWarnings("unchecked")
    public <E extends Enum<E> & EnumGetValue> StringEnumConverter<E> getStringEnumConverter(Class<E> enumType) {
        return switch (enumType.getSimpleName()) {
            case "AngajatTip" -> (StringEnumConverter<E>) angajatTipConverter;
            case "CofetarieTip" -> (StringEnumConverter<E>) cofetarieTipConverter;
            case "PlataTip" -> (StringEnumConverter<E>) plataTipConverter;
            case "ProdusTip" -> (StringEnumConverter<E>) produsTipConverter;
            case "SexTip" -> (StringEnumConverter<E>) sexConverter;
            default -> throw new IllegalArgumentException("Invalid enum type: " + enumType);
        };
    }


}
