package com.mocicarazvan.dwolap.dtos.keys;

import com.mocicarazvan.dwolap.enums.PlataTip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Key10_3 {
    private PlataTip tip;
    private String numeZona;
    private String numeJudet;
    private String numeOrasJudet;
}
