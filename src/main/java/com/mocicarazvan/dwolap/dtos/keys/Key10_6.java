package com.mocicarazvan.dwolap.dtos.keys;

import com.mocicarazvan.dwolap.enums.CofetarieTip;
import com.mocicarazvan.dwolap.enums.ProdusTip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Key10_6 {
    private String lunaSemestruAn;
    private CofetarieTip tipCofetarie;
    private ProdusTip tipProdus;
    private String lunaNume;
}
