package com.mocicarazvan.dwolap.dtos.responses;

public record DTO10_4(
        Long idClient, Long idCofetarie,
        String nume, String prenume, String cofetarieTip,
        Long nrComenzi, Double sumaTotala,
        Integer dRank, Double dif, Double rtr
) {
}
