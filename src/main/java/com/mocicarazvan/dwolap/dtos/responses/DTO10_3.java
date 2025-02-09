package com.mocicarazvan.dwolap.dtos.responses;

public record DTO10_3(
        String tip,
        String numeZona,
        String numeJudet,
        String numeOrasJudet,
        Long cnt,
        Double sumaIncasare,
        Double rataIncasare,
        Integer rankDescCntTipLocatie,
        Integer rankDescCntLocatie,
        Integer rankDescSumaIncasareTipLocatie,
        Integer rankDescSumaIncasareLocatie,
        Double rtrCntTipLocatie,
        Double rtrCntLocatie,
        Double rtrSumaIncasareTipLocatie,
        Double rtrSumaIncasareLocatie
) {
}
