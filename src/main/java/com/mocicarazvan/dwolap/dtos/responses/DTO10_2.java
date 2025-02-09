package com.mocicarazvan.dwolap.dtos.responses;

public record DTO10_2(Double salariuMediuCurent, Double salariuMediuIstoric,
                      Double salariuTotalCurent, Double salariuTotalIstoric,
                      Double salariuMaximCurent, Double salariuMaximIstoric,
                      Double salariuMinimCurent, Double salariuMinimIstoric,
                      Integer rankCurr, Integer rankHist,
                      Double ratioCur, Double ratioHist,
                      Integer anStart, String tipAngajat,
                      String tipCofetarie
) {
}
