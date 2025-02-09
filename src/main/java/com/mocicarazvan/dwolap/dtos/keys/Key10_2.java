package com.mocicarazvan.dwolap.dtos.keys;


import com.mocicarazvan.dwolap.enums.AngajatTip;
import com.mocicarazvan.dwolap.enums.CofetarieTip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Key10_2 {
    private Integer anStart;
    private AngajatTip tipAngajat;
    private CofetarieTip tipCofetarie;
}
