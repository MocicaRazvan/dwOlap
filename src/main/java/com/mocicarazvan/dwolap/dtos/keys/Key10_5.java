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
public class Key10_5 {
    private ProdusTip tipProdus;
    private CofetarieTip tipCofetarie;
    private String oras;
}
