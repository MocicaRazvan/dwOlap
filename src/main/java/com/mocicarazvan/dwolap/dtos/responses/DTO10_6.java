package com.mocicarazvan.dwolap.dtos.responses;

public record DTO10_6(
        String lunaSemestruAn, String tipCofetarie, String tipProdus, String lunaNume,
        Long nrComenziFaraDiscount, Long nrComenziCuDiscount,
        Double sumaFaraDiscount, Double sumaCuDiscount, Double sumaTotala,
        Integer rangFaraDiscount, Double difFaraCuDiscount, Integer difNrComenziFaraCuDiscount,
        Integer rangCuDiscount, Double medieFaraDiscount, Double medieCuDiscount
) {
}
