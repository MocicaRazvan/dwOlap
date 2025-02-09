package com.mocicarazvan.dwolap.controllers;

import com.mocicarazvan.dwolap.dtos.ChartDto;
import com.mocicarazvan.dwolap.dtos.keys.*;
import com.mocicarazvan.dwolap.repositories.OlapRepository;
import com.mocicarazvan.dwolap.utils.RequestParamUtils;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OlapController {

    private final OlapRepository olapRepository;

    @GetMapping("/10_1")
    public ResponseEntity<ChartDto<String>> get10_1(@RequestParam(required = false) Integer rankLimit
    ) {
        return ResponseEntity.ok(
                olapRepository.get10_1(rankLimit)
        );
    }

    @GetMapping("/10_2")
    public ResponseEntity<ChartDto<Key10_2>> get10_2(
            @RequestParam(required = false) Boolean isGroupedTipAngajat,
            @RequestParam(required = false) Boolean isGroupedAnStart,
            @RequestParam(required = false) Boolean isGroupedTipCofetarie,
            @RequestParam(required = false) Integer rankLimitCurent,
            @RequestParam(required = false) Integer rankLimitIstoric

    ) {
        return ResponseEntity.ok(
                olapRepository.get102(
                        RequestParamUtils.groupQuery(isGroupedTipAngajat),
                        RequestParamUtils.groupQuery(isGroupedAnStart),
                        RequestParamUtils.groupQuery(isGroupedTipCofetarie),
                        rankLimitCurent,
                        rankLimitIstoric
                )
        );
    }

    @GetMapping("/10_3")
    public ResponseEntity<ChartDto<Key10_3>> get10_3(
            @RequestParam(required = false) Boolean isGroupedTipPlata,
            @RequestParam(required = false) Boolean isGroupedZona,
            @RequestParam(required = false) Boolean isGroupedJudet,
            @RequestParam(required = false) Boolean isGroupedOras,
            @RequestParam(required = false) LocalDate timpStart,
            @RequestParam(required = false) LocalDate timpEnd,
            @RequestParam(required = false) Integer rankLimitDescSumaIncasare,
            @RequestParam(required = false) Integer rankLimitDescCnt,
            @RequestParam(required = false) Integer rankLimitDescSumaIncasareTipLocatie,
            @RequestParam(required = false) Integer rankLimitDescCntTipLocatie,
            @RequestParam(required = false) Integer rankLimitDescSumaIncasareLocatie,
            @RequestParam(required = false) Integer rankLimitDescCntLocatie

    ) {
        return ResponseEntity.ok(
                olapRepository.get103(
                        RequestParamUtils.groupQuery(isGroupedTipPlata),
                        RequestParamUtils.groupQuery(isGroupedZona),
                        RequestParamUtils.groupQuery(isGroupedJudet),
                        RequestParamUtils.groupQuery(isGroupedOras),
                        RequestParamUtils.localDateToUnix(timpStart),
                        RequestParamUtils.localDateToUnix(timpEnd),
                        rankLimitDescSumaIncasare,
                        rankLimitDescCnt,
                        rankLimitDescSumaIncasareTipLocatie,
                        rankLimitDescCntTipLocatie,
                        rankLimitDescSumaIncasareLocatie,
                        rankLimitDescCntLocatie
                )
        );
    }

    @GetMapping("/10_4")
    public ResponseEntity<ChartDto<Key10_4>> get10_4(
            @RequestParam(required = false) Boolean isGroupedByIdCofetarie,
            @RequestParam(required = false) LocalDate timpStart,
            @RequestParam(required = false) LocalDate timpEnd,
            @RequestParam(required = false) Integer rankLimit

    ) {
        return ResponseEntity.ok(
                olapRepository.get104(
                        RequestParamUtils.groupQuery(isGroupedByIdCofetarie),
                        RequestParamUtils.localDateToUnix(timpStart),
                        RequestParamUtils.localDateToUnix(timpEnd),
                        rankLimit
                )
        );
    }

    @GetMapping("/10_5")
    public ResponseEntity<ChartDto<Key10_5>> get10_5(
            @RequestParam(required = false) Boolean isGroupedTipCofetarie,
            @RequestParam(required = false) Boolean isGroupedOras,
            @RequestParam(required = false) Integer rankLimit

    ) {
        return ResponseEntity.ok(
                olapRepository.get105(
                        RequestParamUtils.groupQuery(isGroupedTipCofetarie),
                        RequestParamUtils.groupQuery(isGroupedOras),
                        rankLimit
                )
        );
    }

    @GetMapping("/10_6")
    public ResponseEntity<ChartDto<Key10_6>> get10_6(
            @Parameter(description = "cannot have this and produs") @RequestParam(required = false) Boolean isGroupedTipCofetarie,
            @Parameter(description = "cannot have this and cofetarie") @RequestParam(required = false) Boolean isGroupedTipProdus,
            @RequestParam(required = false) Integer rankLimitCuDiscount,
            @RequestParam(required = false) Integer rankLimitFaraDiscount,
            @RequestParam(required = false) List<String> luniSemestruAn

    ) {
        return ResponseEntity.ok(
                olapRepository.get106(
                        RequestParamUtils.groupQuery(isGroupedTipCofetarie),
                        RequestParamUtils.groupQuery(isGroupedTipProdus),
                        rankLimitCuDiscount,
                        rankLimitFaraDiscount,
                        luniSemestruAn
                )
        );
    }

    @GetMapping("/10_7")
    public ResponseEntity<ChartDto<Key10_7>> get10_7(
            @RequestParam(required = false) Integer rankLimit,
            @RequestParam(required = false) List<String> semestreAn

    ) {
        return ResponseEntity.ok(
                olapRepository.get107(
                        rankLimit,
                        semestreAn
                )
        );
    }

    @GetMapping("/10_8")
    public ResponseEntity<ChartDto<Key10_8>> get10_8(
            @RequestParam(required = false) LocalDate timpStart,
            @RequestParam(required = false) LocalDate timpEnd,
            @RequestParam(required = false) Integer rankLimit

    ) {
        return ResponseEntity.ok(
                olapRepository.get108(
                        RequestParamUtils.localDateToUnix(timpStart),
                        RequestParamUtils.localDateToUnix(timpEnd),
                        rankLimit
                )
        );
    }

    @GetMapping("/10_9")
    public ResponseEntity<ChartDto<Key10_9>> get10_9(
            @RequestParam(required = false) LocalDate timpStart,
            @RequestParam(required = false) LocalDate timpEnd,
            @RequestParam(required = false) Integer rankLimit

    ) {
        return ResponseEntity.ok(
                olapRepository.get109(
                        RequestParamUtils.localDateToUnix(timpStart),
                        RequestParamUtils.localDateToUnix(timpEnd),
                        rankLimit
                )
        );
    }

}
