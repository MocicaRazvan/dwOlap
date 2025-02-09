package com.mocicarazvan.dwolap.repositories;


import com.mocicarazvan.dwolap.converters.factories.ConvertorFactory;
import com.mocicarazvan.dwolap.dtos.ChartDto;
import com.mocicarazvan.dwolap.dtos.keys.*;
import com.mocicarazvan.dwolap.dtos.responses.*;
import com.mocicarazvan.dwolap.enums.AngajatTip;
import com.mocicarazvan.dwolap.enums.CofetarieTip;
import com.mocicarazvan.dwolap.enums.PlataTip;
import com.mocicarazvan.dwolap.enums.ProdusTip;
import com.mocicarazvan.dwolap.utils.ReduceTemp;
import lombok.RequiredArgsConstructor;
import org.hibernate.type.SqlTypes;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OlapRepository {
    private final JdbcClient jdbcClient;
    private final ConvertorFactory convertorFactory;

    //todo schimba rangul in github
    public ChartDto<String> get10_1(
            Integer rankLimit
    ) {
        return jdbcClient.sql("""
                            select * from (
                            SELECT
                                i.id_cofetarie,
                                COUNT(*) AS cnt,
                                AVG(i.salariu) AS salariu_mediu,
                                SUM(i.salariu) AS salariu_total,
                                MAX(i.salariu) AS salariu_maxim,
                                MIN(i.salariu) AS salariu_minim,
                                DENSE_RANK() OVER (
                                    ORDER BY
                                        AVG(i.salariu) desc
                                ) AS rang_salariu_mediu_desc_cofetarie
                            FROM
                                informatii_angajat i
                                JOIN istoric_angajat a ON i.id_istoric = a.id_istoric
                                JOIN timp ts ON i.id_timp_start = ts.id_timp
                                LEFT JOIN timp tf ON i.id_timp_final = tf.id_timp
                            WHERE
                                tf.id_timp IS NULL
                                AND a.tip_angajat = 'cofetar'
                            GROUP BY
                                i.id_cofetarie)
                            where (:rankLimit is null or rang_salariu_mediu_desc_cofetarie <= :rankLimit)
                            order by rang_salariu_mediu_desc_cofetarie desc
                        """)
                .param("rankLimit", rankLimit, SqlTypes.INTEGER)
                .query(DTO10_1.class)
                .stream().parallel()
                .reduce(new ReduceTemp<String>(), (acc, el) -> {
                    acc.getLabels().add(el.idCofetarie().toString());
                    acc.appendToMap("cnt", el.cnt());
                    acc.appendToMap("salariuMediu", el.salariuMediu());
                    acc.appendToMap("salariuTotal", el.salariuTotal());
                    acc.appendToMap("salariuMaxim", el.salariuMaxim());
                    acc.appendToMap("salariuMinim", el.salariuMinim());
                    return acc;
                }, ReduceTemp::merge)
                .map(ReduceTemp::getChartDto);
    }


    public ChartDto<Key10_2> get102(
            Integer isGroupedTipAngajat,
            Integer isGroupedAnStart,
            Integer isGroupedTipCofetarie,
            Integer rankLimitCurent,
            Integer rankLimitIstoric
    ) {
        return jdbcClient.sql("""
                            WITH
                                data_status_cte AS (
                                    SELECT
                                        a.id_angajat,
                                        i.id_timp_final,
                                        a.tip_angajat,
                                        ts.an AS an_start,
                                        c.tip AS tip_cofetarie,
                                        CASE
                                            WHEN i.id_timp_final IS NULL THEN 'cur'
                                            ELSE 'historical'
                                            END AS data_status,
                                        i.salariu
                                    FROM
                                        informatii_angajat i
                                            JOIN istoric_angajat a ON i.id_istoric = a.id_istoric
                                            JOIN timp ts ON i.id_timp_start = ts.id_timp
                                            JOIN cofetarie c ON i.id_cofetarie = c.id_cofetarie
                                )
                            select * from (
                            SELECT
                                an_start AS an_start,
                                tip_angajat AS tip_angajat,
                               tip_cofetarie AS tip_cofetarie,
                                MAX(
                                        CASE
                                            WHEN data_status = 'cur' THEN salariu_mediu
                                            END
                                ) AS salariu_mediu_curent,
                                MAX(
                                        CASE
                                            WHEN data_status = 'historical' THEN salariu_mediu
                                            END
                                ) AS salariu_mediu_istoric,
                                MAX(
                                        CASE
                                            WHEN data_status = 'cur' THEN salariu_total
                                            END
                                ) AS salariu_total_curent,
                                MAX(
                                        CASE
                                            WHEN data_status = 'historical' THEN salariu_total
                                            END
                                ) AS salariu_total_istoric,
                                MAX(
                                        CASE
                                            WHEN data_status = 'cur' THEN salariu_maxim
                                            END
                                ) AS salariu_maxim_curent,
                                MAX(
                                        CASE
                                            WHEN data_status = 'historical' THEN salariu_maxim
                                            END
                                ) AS salariu_maxim_istoric,
                                MAX(
                                        CASE
                                            WHEN data_status = 'cur' THEN salariu_minim
                                            END
                                ) AS salariu_minim_curent,
                                MAX(
                                        CASE
                                            WHEN data_status = 'historical' THEN salariu_minim
                                            END
                                ) AS salariu_minim_istoric,
                                DENSE_RANK() OVER (
                                    PARTITION BY
                                        grouping_id (tip_angajat, an_start, tip_cofetarie)
                                    ORDER BY
                                        MAX(
                                                CASE
                                                    WHEN data_status = 'cur' THEN salariu_mediu
                                                    END
                                        ) desc
                                    ) AS rank_curr,
                                DENSE_RANK() OVER (
                                    PARTITION BY
                                        grouping_id (tip_angajat, an_start, tip_cofetarie)
                                    ORDER BY
                                        MAX(
                                                CASE
                                                    WHEN data_status = 'historical' THEN salariu_mediu
                                                    END
                                        ) desc
                                    ) AS rank_hist,
                                ratio_to_report (
                                        MAX(
                                                CASE
                                                    WHEN data_status = 'cur' THEN salariu_mediu
                                                    END
                                        )
                                ) OVER (
                                            PARTITION BY
                                                grouping_id (tip_angajat, an_start, tip_cofetarie)
                                            ) AS ratio_cur,
                                ratio_to_report (
                                        MAX(
                                                CASE
                                                    WHEN data_status = 'historical' THEN salariu_mediu
                                                    END
                                        )
                                ) OVER (
                                            PARTITION BY
                                                grouping_id (tip_angajat, an_start, tip_cofetarie)
                                            ) AS ratio_hist
                            FROM
                                (
                                    SELECT
                                        tip_angajat,
                                        an_start,
                                        tip_cofetarie,
                                        data_status,
                                        AVG(salariu) AS salariu_mediu,
                                        SUM(salariu) AS salariu_total,
                                        MAX(salariu) AS salariu_maxim,
                                        MIN(salariu) AS salariu_minim
                                    FROM
                                        data_status_cte
                                    GROUP BY
                                        tip_angajat,
                                        an_start,
                                        tip_cofetarie,
                                        data_status
                                ) salary_data
                            GROUP BY
                                GROUPING sets (
                                (tip_angajat, an_start, tip_cofetarie),
                                (tip_angajat, tip_cofetarie),
                                (tip_angajat, an_start),
                                (tip_cofetarie),
                                (tip_angajat),
                                (an_start),
                                ()
                                )
                            having (:isGroupedTipAngajat is null or grouping(tip_angajat) = :isGroupedTipAngajat)
                            and (:isGroupedAnStart is null or grouping(an_start) = :isGroupedAnStart)
                            and (:isGroupedTipCofetarie is null or grouping(tip_cofetarie) = :isGroupedTipCofetarie)
                            ORDER BY
                                an_start,
                                tip_angajat,
                                tip_cofetarie,
                                rank_curr)
                            where (
                                :rankLimitCurent IS NULL
                                OR rank_curr <= :rankLimitCurent
                            )
                            and (
                                :rankLimitIstoric IS NULL
                                OR rank_hist <= :rankLimitIstoric
                            )
                        """)
                .param("isGroupedTipAngajat", isGroupedTipAngajat, SqlTypes.INTEGER)
                .param("isGroupedAnStart", isGroupedAnStart, SqlTypes.INTEGER)
                .param("isGroupedTipCofetarie", isGroupedTipCofetarie, SqlTypes.INTEGER)
                .param("rankLimitCurent", rankLimitCurent, SqlTypes.INTEGER)
                .param("rankLimitIstoric", rankLimitIstoric, SqlTypes.INTEGER)
                .query(DTO10_2.class)
                .stream().parallel()
                .reduce(new ReduceTemp<Key10_2>(), (acc, el) -> {
                    acc.getLabels().add(Key10_2.builder()
                            .anStart(el.anStart())
                            .tipAngajat(convertorFactory.getStringEnumConverter(AngajatTip.class)
                                    .convertToEntityAttribute(el.tipAngajat()))
                            .tipCofetarie(convertorFactory.getStringEnumConverter(CofetarieTip.class)
                                    .convertToEntityAttribute(el.tipCofetarie()))
                            .build());
                    acc.appendToMap("salariuMediuCurent", el.salariuMediuCurent());
                    acc.appendToMap("salariuMediuIstoric", el.salariuMediuIstoric());
                    acc.appendToMap("salariuTotalCurent", el.salariuTotalCurent());
                    acc.appendToMap("salariuTotalIstoric", el.salariuTotalIstoric());
                    acc.appendToMap("salariuMaximCurent", el.salariuMaximCurent());
                    acc.appendToMap("salariuMaximIstoric", el.salariuMaximIstoric());
                    acc.appendToMap("salariuMinimCurent", el.salariuMinimCurent());
                    acc.appendToMap("salariuMinimIstoric", el.salariuMinimIstoric());
                    acc.appendToMap("rankCurr", el.rankCurr());
                    acc.appendToMap("rankHist", el.rankHist());
                    return acc;
                }, ReduceTemp::merge)
                .map(ReduceTemp::getChartDto);
    }

    public ChartDto<Key10_3> get103(
            Integer isGroupedTipPlata,
            Integer isGroupedZona,
            Integer isGroupedJudet,
            Integer isGroupedOras,
            Long timpStart,
            Long timpEnd,
            Integer rankLimitDescSumaIncasare,
            Integer rankLimitDescCnt,
            Integer rankLimitDescSumaIncasareTipLocatie,
            Integer rankLimitDescCntTipLocatie,
            Integer rankLimitDescSumaIncasareLocatie,
            Integer rankLimitDescCntLocatie
    ) {
        return jdbcClient.sql("""
                                select * from (
                                SELECT
                                    p.tip,
                                    l.nume_zona,
                                    l.nume_judet,
                                    l.nume_oras_judet,
                                    COUNT(*) AS cnt,
                                    SUM(i.suma_incasare) AS suma_incasare,
                                    SUM(i.suma_total_comanda) / SUM(i.suma_incasare) AS rata_incasare,
                                    DENSE_RANK() OVER (
                                        PARTITION BY
                                            grouping_id (
                                                p.tip,
                                                l.nume_zona,
                                                l.nume_judet,
                                                l.nume_oras_judet
                                            )
                                        ORDER BY
                                            COUNT(*) desc
                                    ) AS rank_desc_cnt_tip_locatie,
                                    DENSE_RANK() OVER (
                                        PARTITION BY
                                            grouping_id (l.nume_zona, l.nume_judet, l.nume_oras_judet)
                                        ORDER BY
                                            COUNT(*) desc
                                    ) AS rank_desc_cnt_locatie,
                                    DENSE_RANK() OVER (
                                        PARTITION BY
                                            grouping_id (
                                                p.tip,
                                                l.nume_zona,
                                                l.nume_judet,
                                                l.nume_oras_judet
                                            )
                                        ORDER BY
                                            SUM(i.suma_incasare) desc
                                    ) AS rank_desc_suma_incasare_tip_locatie,
                                    DENSE_RANK() OVER (
                                        PARTITION BY
                                            grouping_id (l.nume_zona, l.nume_judet, l.nume_oras_judet)
                                        ORDER BY
                                            SUM(i.suma_incasare) desc
                                    ) AS rank_desc_suma_incasare_locatie,
                                    ratio_to_report (COUNT(*)) OVER (
                                        PARTITION BY
                                            grouping_id (
                                                p.tip,
                                                l.nume_zona,
                                                l.nume_judet,
                                                l.nume_oras_judet
                                            )
                                    ) AS rtr_cnt_tip_locatie,
                                    ratio_to_report (COUNT(*)) OVER (
                                        PARTITION BY
                                            grouping_id (l.nume_zona, l.nume_judet, l.nume_oras_judet)
                                    ) AS rtr_cnt_locatie,
                                    ratio_to_report (SUM(i.suma_incasare)) OVER (
                                        PARTITION BY
                                            grouping_id (
                                                p.tip,
                                                l.nume_zona,
                                                l.nume_judet,
                                                l.nume_oras_judet
                                            )
                                    ) AS rtr_suma_incasare_tip_locatie,
                                    ratio_to_report (SUM(i.suma_incasare)) OVER (
                                        PARTITION BY
                                            grouping_id (l.nume_zona, l.nume_judet, l.nume_oras_judet)
                                    ) AS rtr_suma_incasare_locatie
                                FROM
                                    incasare_timp_part i
                                    JOIN plata p ON i.id_incasare = p.id_plata
                                    JOIN locatie l ON i.id_locatie = l.id_locatie
                                    JOIN timp t ON i.id_timp = t.id_timp
                                WHERE
                                    (:timpStart is null or t.id_timp >= :timpStart)
                                    and (:timpEnd is null or t.id_timp <= :timpEnd)
                                GROUP BY
                                    ROLLUP (
                                        p.tip,
                                        l.nume_zona,
                                        l.nume_judet,
                                        l.nume_oras_judet
                                    )
                            having (:isGroupedTipPlata is null or grouping(p.tip) = :isGroupedTipPlata)
                            and (:isGroupedZona is null or grouping(l.nume_zona) = :isGroupedZona)
                            and (:isGroupedJudet is null or grouping(l.nume_judet) = :isGroupedJudet)
                            and (:isGroupedOras is null or grouping(l.nume_oras_judet) = :isGroupedOras))t
                            where (
                                :rankLimitDescSumaIncasare IS NULL
                                OR rank_desc_suma_incasare_tip_locatie <= :rankLimitDescSumaIncasare
                            )
                            and (
                                :rankLimitDescCnt IS NULL
                                OR rank_desc_cnt_tip_locatie <= :rankLimitDescCnt
                            )
                            and (
                                :rankLimitDescSumaIncasareTipLocatie IS NULL
                                OR rank_desc_suma_incasare_tip_locatie <= :rankLimitDescSumaIncasareTipLocatie
                            )
                            and (
                                :rankLimitDescCntTipLocatie IS NULL
                                OR rank_desc_cnt_tip_locatie <= :rankLimitDescCntTipLocatie
                            )
                            and (
                                :rankLimitDescSumaIncasareLocatie IS NULL
                                OR rank_desc_suma_incasare_locatie <= :rankLimitDescSumaIncasareLocatie
                            )
                            and (
                                :rankLimitDescCntLocatie IS NULL
                                OR rank_desc_cnt_locatie <= :rankLimitDescCntLocatie
                            )
                        
                        """)
                .param("isGroupedTipPlata", isGroupedTipPlata, SqlTypes.INTEGER)
                .param("isGroupedZona", isGroupedZona, SqlTypes.INTEGER)
                .param("isGroupedJudet", isGroupedJudet, SqlTypes.INTEGER)
                .param("isGroupedOras", isGroupedOras, SqlTypes.INTEGER)
                .param("timpStart", timpStart, SqlTypes.BIGINT)
                .param("timpEnd", timpEnd, SqlTypes.BIGINT)
                .param("rankLimitDescSumaIncasare", rankLimitDescSumaIncasare, SqlTypes.INTEGER)
                .param("rankLimitDescCnt", rankLimitDescCnt, SqlTypes.INTEGER)
                .param("rankLimitDescSumaIncasareTipLocatie", rankLimitDescSumaIncasareTipLocatie, SqlTypes.INTEGER)
                .param("rankLimitDescCntTipLocatie", rankLimitDescCntTipLocatie, SqlTypes.INTEGER)
                .param("rankLimitDescSumaIncasareLocatie", rankLimitDescSumaIncasareLocatie, SqlTypes.INTEGER)
                .param("rankLimitDescCntLocatie", rankLimitDescCntLocatie, SqlTypes.INTEGER)
                .query(DTO10_3.class)
                .stream().parallel()
                .reduce(new ReduceTemp<Key10_3>(), (acc, el) -> {
                    acc.getLabels().add(Key10_3.builder()
                            .tip(convertorFactory.getStringEnumConverter(PlataTip.class)
                                    .convertToEntityAttribute(el.tip()))
                            .numeZona(el.numeZona())
                            .numeJudet(el.numeJudet())
                            .numeOrasJudet(el.numeOrasJudet())
                            .build());
                    acc.appendToMap("cnt", el.cnt());
                    acc.appendToMap("sumaIncasare", el.sumaIncasare());
                    acc.appendToMap("rataIncasare", el.rataIncasare());
                    acc.appendToMap("rankDescCntTipLocatie", el.rankDescCntTipLocatie());
                    acc.appendToMap("rankDescCntLocatie", el.rankDescCntLocatie());
                    acc.appendToMap("rankDescSumaIncasareTipLocatie", el.rankDescSumaIncasareTipLocatie());
                    acc.appendToMap("rankDescSumaIncasareLocatie", el.rankDescSumaIncasareLocatie());
                    acc.appendToMap("rtrCntTipLocatie", el.rtrCntTipLocatie());
                    acc.appendToMap("rtrCntLocatie", el.rtrCntLocatie());
                    acc.appendToMap("rtrSumaIncasareTipLocatie", el.rtrSumaIncasareTipLocatie());
                    acc.appendToMap("rtrSumaIncasareLocatie", el.rtrSumaIncasareLocatie());
                    return acc;
                }, ReduceTemp::merge)
                .map(ReduceTemp::getChartDto);

    }

    //todo in pdf add id cofeatarie la select
    public ChartDto<Key10_4> get104(
            Integer isGroupedByIdCofetarie,
            Long timpStart,
            Long timpEnd,
            Integer rankLimit
    ) {
        return jdbcClient.sql("""
                        select * from (
                        SELECT
                                c.id_client,
                                cft.id_cofetarie,
                                MAX(c.nume)    AS nume,
                                MAX(c.prenume) AS prenume,
                                MAX(cft.tip)   AS cofetarie_tip,
                                COUNT(*)       nr_comenzi,
                                SUM(cf.suma)   suma_totala,
                                DENSE_RANK()
                                OVER(PARTITION BY GROUPING_ID(cft.id_cofetarie, c.id_client)
                                     ORDER BY
                                         SUM(cf.suma) DESC
                                )              d_rank,
                                SUM(cf.suma) - LEAD(SUM(cf.suma),
                                 1)
                                               OVER(PARTITION BY GROUPING_ID(cft.id_cofetarie, c.id_client)
                                                    ORDER BY
                                                        SUM(cf.suma) DESC
                                )              AS dif,
                                RATIO_TO_REPORT(SUM(cf.suma))
                                OVER()         AS rtr
                            FROM
                                     comanda_finalizata_timp_part cf
                                JOIN client    c ON cf.id_client = c.id_client
                                JOIN cofetarie cft ON cf.id_cofetarie = cft.id_cofetarie
                            WHERE
                                (
                                    :timpStart IS NULL
                                    OR cf.id_timp >= :timpStart
                                )
                                AND (
                                    :timpEnd IS NULL
                                    OR cf.id_timp <= :timpEnd
                                )
                                AND cft.tip = 'cu-servire'
                            GROUP BY
                                GROUPING SETS ( c.id_client, ( c.id_client,
                                                               cft.id_cofetarie ) )
                            HAVING
                                ( :isGroupedByIdCofetarie IS NULL
                                  OR GROUPING(cft.id_cofetarie) = :isGroupedByIdCofetarie )
                        )  temp
                        
                        where (
                            :rankLimit IS NULL
                            OR d_rank <= :rankLimit
                        )
                        order by d_rank
                        """)
                .param("isGroupedByIdCofetarie", isGroupedByIdCofetarie, SqlTypes.INTEGER)
                .param("timpStart", timpStart, SqlTypes.BIGINT)
                .param("timpEnd", timpEnd, SqlTypes.BIGINT)
                .param("rankLimit", rankLimit, SqlTypes.INTEGER)
                .query(DTO10_4.class)
                .stream().parallel()
                .reduce(new ReduceTemp<Key10_4>(), (acc, el) -> {
                    acc.getLabels().add(Key10_4.builder()
                            .idClient(el.idClient())
                            .idCofetarie(el.idCofetarie())
                            .nume(el.nume())
                            .prenume(el.prenume())
                            .cofetarieTip(convertorFactory.getStringEnumConverter(CofetarieTip.class)
                                    .convertToEntityAttribute(el.cofetarieTip()))
                            .build());
                    acc.appendToMap("nrComenzi", el.nrComenzi());
                    acc.appendToMap("sumaTotala", el.sumaTotala());
                    acc.appendToMap("dRank", el.dRank());
                    acc.appendToMap("dif", el.dif());
                    acc.appendToMap("rtr", el.rtr());
                    return acc;
                }, ReduceTemp::merge)
                .map(ReduceTemp::getChartDto);

    }

    public ChartDto<Key10_5> get105(
            Integer isGroupedTipCofetarie,
            Integer isGroupedOras,
            Integer rankLimit
    ) {
        return jdbcClient.sql("""
                            select * from (
                            SELECT
                                p.tip AS tip_produs,
                                c.tip AS tip_cofetarie,
                                l.nume_oras_judet AS oras,
                                grouping_id (p.tip, c.tip, l.nume_oras_judet) AS grupare,
                                SUM(cf.suma) AS suma,
                                COUNT(*) AS nr_comenzi,
                                AVG(cf.suma) AS medie,
                                DENSE_RANK() OVER (
                                    PARTITION BY
                                        grouping_id (p.tip, c.tip, l.nume_oras_judet)
                                    ORDER BY
                                        SUM(cf.suma) desc
                                ) d_rank,
                                SUM(cf.suma) - lead (SUM(cf.suma), 1) OVER (
                                    PARTITION BY
                                        grouping_id (p.tip, c.tip, l.nume_oras_judet)
                                    ORDER BY
                                        SUM(cf.suma) desc
                                ) AS dif,
                                ratio_to_report (SUM(cf.suma)) OVER (
                                    PARTITION BY
                                        grouping_id (p.tip, c.tip, l.nume_oras_judet)
                                ) AS rtr
                            FROM
                                comanda_finalizata cf
                                JOIN produs p ON cf.id_produs = p.id_produs
                                JOIN cofetarie c ON cf.id_cofetarie = c.id_cofetarie
                                JOIN locatie l ON cf.id_locatie = l.id_locatie
                            GROUP BY
                                GROUPING sets (
                                    (p.tip, c.tip, l.nume_oras_judet),
                                    (p.tip, c.tip),
                                    (p.tip, l.nume_oras_judet),
                                    (p.tip)
                                )
                            HAVING
                                (:isGroupedTipCofetarie IS NULL OR grouping(c.tip) = :isGroupedTipCofetarie)
                                AND (:isGroupedOras IS NULL OR grouping(l.nume_oras_judet) = :isGroupedOras)
                            )temp
                            where (
                                :rankLimit IS NULL
                                OR d_rank <= :rankLimit
                            )
                                     order by d_rank
                        """)
                .param("isGroupedTipCofetarie", isGroupedTipCofetarie, SqlTypes.INTEGER)
                .param("isGroupedOras", isGroupedOras, SqlTypes.INTEGER)
                .param("rankLimit", rankLimit, SqlTypes.INTEGER)
                .query(DTO10_5.class)
                .stream().parallel()
                .reduce(new ReduceTemp<Key10_5>(), (acc, el) -> {
                    acc.getLabels().add(Key10_5.builder()
                            .tipProdus(convertorFactory.getStringEnumConverter(ProdusTip.class)
                                    .convertToEntityAttribute(el.tipProdus()))
                            .tipCofetarie(convertorFactory.getStringEnumConverter(CofetarieTip.class)
                                    .convertToEntityAttribute(el.tipCofetarie()))
                            .oras(el.oras())
                            .build());
                    acc.appendToMap("suma", el.suma());
                    acc.appendToMap("nrComenzi", el.nrComenzi());
                    acc.appendToMap("medie", el.medie());
                    acc.appendToMap("dRank", el.dRank());
                    acc.appendToMap("dif", el.dif());
                    acc.appendToMap("rtr", el.rtr());
                    return acc;
                }, ReduceTemp::merge)
                .map(ReduceTemp::getChartDto);
    }

    public ChartDto<Key10_6> get106(
            Integer isGroupedTipCofetarie,
            Integer isGroupedTipProdus,
            Integer rankLimitCuDiscount,
            Integer rankLimitFaraDiscount,
            List<String> luniSemestruAn
    ) {
        return jdbcClient.sql("""
                        WITH
                            cmd AS (
                                SELECT
                                    id_comanda,
                                    MAX(discount) AS discount
                                FROM
                                    comanda_finalizata
                                GROUP BY
                                    id_comanda
                            )
                        select * from (
                                        SELECT
                            t.luna_semestru_an,
                            c.tip AS tip_cofetarie,
                            p.tip AS tip_produs,
                            MAX(t.luna_nume) AS luna_nume,
                            COUNT(
                                DISTINCT CASE
                                    WHEN cmd.discount IS NULL THEN cf.id_comanda
                                END
                            ) AS nr_comenzi_fara_discount,
                            COUNT(
                                DISTINCT CASE
                                    WHEN cmd.discount IS NOT NULL THEN cf.id_comanda
                                END
                            ) AS nr_comenzi_cu_discount,
                            SUM(
                                CASE
                                    WHEN cmd.discount IS NULL THEN cf.pret_cantitate_discount_produs
                                    ELSE 0
                                END
                            ) AS suma_fara_discount,
                            SUM(
                                CASE
                                    WHEN cmd.discount IS NOT NULL THEN cf.pret_cantitate_discount_produs
                                    ELSE 0
                                END
                            ) AS suma_cu_discount,
                            SUM(cf.pret_cantitate_discount_produs) AS suma_totala,
                            DENSE_RANK() OVER (
                                PARTITION BY
                                    grouping_id (t.luna_semestru_an, p.tip, c.tip)
                                ORDER BY
                                    SUM(
                                        CASE
                                            WHEN cmd.discount IS NULL THEN cf.pret_cantitate_discount_produs
                                            ELSE 0
                                        END
                                    ) desc
                            ) AS rang_fara_discount,
                            SUM(
                                CASE
                                    WHEN cmd.discount IS NULL THEN cf.pret_cantitate_discount_produs
                                    ELSE 0
                                END
                            ) - SUM(
                                CASE
                                    WHEN cmd.discount IS NOT NULL THEN cf.pret_cantitate_discount_produs
                                    ELSE 0
                                END
                            ) AS dif_fara_cu_discount,
                            COUNT(
                                DISTINCT CASE
                                    WHEN cmd.discount IS NULL THEN cf.id_comanda
                                END
                            ) - COUNT(
                                DISTINCT CASE
                                    WHEN cmd.discount IS NOT NULL THEN cf.id_comanda
                                END
                            ) AS dif_nr_comenzi_fara_cu_discount,
                            DENSE_RANK() OVER (
                                PARTITION BY
                                    grouping_id (t.luna_semestru_an, p.tip, c.tip)
                                ORDER BY
                                    SUM(
                                        CASE
                                            WHEN cmd.discount IS NOT NULL THEN cf.pret_cantitate_discount_produs
                                            ELSE 0
                                        END
                                    ) desc
                            ) AS rang_cu_discount,
                            SUM(
                                CASE
                                    WHEN cmd.discount IS NULL THEN cf.pret_cantitate_discount_produs
                                END
                            ) / COUNT(
                                DISTINCT CASE
                                    WHEN cmd.discount IS NULL THEN cf.id_comanda
                                END
                            ) AS medie_fara_discount,
                            SUM(
                                CASE
                                    WHEN cmd.discount IS NOT NULL THEN cf.pret_cantitate_discount_produs
                                END
                            ) / COUNT(
                                DISTINCT CASE
                                    WHEN cmd.discount IS NOT NULL THEN cf.id_comanda
                                END
                            ) AS medie_cu_discount,
                            grouping_id (t.luna_semestru_an, p.tip, c.tip) AS grupare,
                            grouping_id (t.luna_semestru_an) AS grupare_luna,
                            grouping_id (p.tip) AS grupare_produs,
                            grouping_id (c.tip) AS grupare_cofetarie
                                        FROM
                            comanda_finalizata cf
                            JOIN produs p ON cf.id_produs = p.id_produs
                            JOIN cofetarie c ON cf.id_cofetarie = c.id_cofetarie
                            JOIN timp t ON cf.id_timp = t.id_timp
                            JOIN cmd ON cf.id_comanda = cmd.id_comanda
                            WHERE
                                (
                                    :luniSemestruAn IS NULL
                                    OR t.luna_semestru_an IN (:luniSemestruAn)
                                )
                                        GROUP BY
                            GROUPING sets (
                                t.luna_semestru_an,
                                (t.luna_semestru_an, p.tip),
                                (t.luna_semestru_an, c.tip)
                            )
                                        HAVING
                            (:isGroupedTipCofetarie IS NULL OR grouping(c.tip) = :isGroupedTipCofetarie)
                            AND (:isGroupedTipProdus IS NULL OR grouping(p.tip) = :isGroupedTipProdus)
                                        ) temp
                                        where (
                                            :rankLimitCuDiscount IS NULL
                                            OR rang_cu_discount <= :rankLimitCuDiscount
                                        )
                                        and (
                                            :rankLimitFaraDiscount IS NULL
                                            OR rang_fara_discount <= :rankLimitFaraDiscount
                                        )
                        """)
                .param("isGroupedTipCofetarie", isGroupedTipCofetarie, SqlTypes.INTEGER)
                .param("isGroupedTipProdus", isGroupedTipProdus, SqlTypes.INTEGER)
                .param("rankLimitCuDiscount", rankLimitCuDiscount, SqlTypes.INTEGER)
                .param("rankLimitFaraDiscount", rankLimitFaraDiscount, SqlTypes.INTEGER)
                .param("luniSemestruAn", bindStringList(luniSemestruAn))
                .query(DTO10_6.class)
                .stream().parallel()
                .reduce(new ReduceTemp<Key10_6>(), (acc, el) -> {
                    acc.getLabels().add(Key10_6.builder()
                            .lunaSemestruAn(el.lunaSemestruAn())
                            .tipCofetarie(convertorFactory.getStringEnumConverter(CofetarieTip.class)
                                    .convertToEntityAttribute(el.tipCofetarie()))
                            .tipProdus(convertorFactory.getStringEnumConverter(ProdusTip.class)
                                    .convertToEntityAttribute(el.tipProdus()))
                            .lunaNume(el.lunaNume())
                            .build());
                    acc.appendToMap("nrComenziFaraDiscount", el.nrComenziFaraDiscount());
                    acc.appendToMap("nrComenziCuDiscount", el.nrComenziCuDiscount());
                    acc.appendToMap("sumaFaraDiscount", el.sumaFaraDiscount());
                    acc.appendToMap("sumaCuDiscount", el.sumaCuDiscount());
                    acc.appendToMap("sumaTotala", el.sumaTotala());
                    acc.appendToMap("rangFaraDiscount", el.rangFaraDiscount());
                    acc.appendToMap("difFaraCuDiscount", el.difFaraCuDiscount());
                    acc.appendToMap("difNrComenziFaraCuDiscount", el.difNrComenziFaraCuDiscount());
                    acc.appendToMap("rangCuDiscount", el.rangCuDiscount());
                    acc.appendToMap("medieFaraDiscount", el.medieFaraDiscount());
                    acc.appendToMap("medieCuDiscount", el.medieCuDiscount());
                    return acc;
                }, ReduceTemp::merge)
                .map(ReduceTemp::getChartDto);
    }

    public ChartDto<Key10_7> get107(
            Integer rankLimit,
            List<String> semestreAn
    ) {
        return jdbcClient.sql("""
                        select * from (
                        SELECT
                            p.id_produs,
                            t.semestru_an,
                            COUNT(*) AS nr_comenzi,
                            SUM(cf.suma) AS suma_totala,
                            DENSE_RANK() OVER (
                                PARTITION BY
                                    t.semestru_an
                                ORDER BY
                                    SUM(cf.suma) desc
                            ) AS rang_sem,
                            ratio_to_report (SUM(cf.suma)) OVER (
                                PARTITION BY
                                    t.semestru_an
                            ) AS rtr_sem
                        FROM
                            comanda_finalizata cf
                            JOIN produs p ON cf.id_produs = p.id_produs
                            JOIN timp t ON cf.id_timp = t.id_timp
                        WHERE
                            p.tip = 'suc'
                        and (
                            :semestreAn IS NULL
                            OR t.semestru_an IN (:semestreAn)
                        )
                        GROUP BY
                            t.semestru_an,
                            p.id_produs
                        )temp
                        where (
                            :rankLimit IS NULL
                            OR rang_sem <= :rankLimit
                        )
                        order by rang_sem
                        """)
                .param("rankLimit", rankLimit, SqlTypes.INTEGER)
                .param("semestreAn", bindStringList(semestreAn))

                .query(DTO10_7.class)
                .stream().parallel()
                .reduce(new ReduceTemp<Key10_7>(), (acc, el) -> {
                    acc.getLabels().add(Key10_7.builder()
                            .idProdus(el.idProdus())
                            .semestruAn(el.semestruAn())
                            .build());
                    acc.appendToMap("nrComenzi", el.nrComenzi());
                    acc.appendToMap("sumaTotala", el.sumaTotala());
                    acc.appendToMap("rangSem", el.rangSem());
                    acc.appendToMap("rtrSem", el.rtrSem());
                    return acc;
                }, ReduceTemp::merge)
                .map(ReduceTemp::getChartDto);
    }

    public ChartDto<Key10_8> get108(
            Long timpStart,
            Long timpEnd,
            Integer rankLimit
    ) {
        return jdbcClient.sql("""
                        select  * from (
                        SELECT
                            c.tip,
                            SUM(cf.suma) AS suma_totala,
                            DENSE_RANK() OVER (
                                ORDER BY
                                    SUM(cf.suma) desc
                            ) AS rang,
                            ratio_to_report (SUM(cf.suma)) OVER () AS rtr,
                            COUNT(*) AS nr_comenzi,
                            SUM(cf.suma) - lead (SUM(cf.suma), 1) OVER (
                                ORDER BY
                                    SUM(cf.suma) desc
                            ) AS dif
                        FROM
                            comanda_finalizata_timp_part cf
                            JOIN cofetarie c ON cf.id_cofetarie = c.id_cofetarie
                            JOIN timp t ON cf.id_timp = t.id_timp
                        WHERE
                            (:timpStart IS NULL OR t.id_timp >= :timpStart)
                            AND (:timpEnd IS NULL OR t.id_timp <= :timpEnd)
                        GROUP BY
                            c.tip)
                            where (
                                :rankLimit IS NULL
                                OR rang <= :rankLimit
                            )
                            order by rang
                        """)
                .param("timpStart", timpStart, SqlTypes.BIGINT)
                .param("timpEnd", timpEnd, SqlTypes.BIGINT)
                .param("rankLimit", rankLimit, SqlTypes.INTEGER)
                .query(DTO10_8.class)
                .stream().parallel()
                .reduce(new ReduceTemp<Key10_8>(), (acc, el) -> {
                    acc.getLabels().add(Key10_8.builder()
                            .tip(convertorFactory.getStringEnumConverter(CofetarieTip.class)
                                    .convertToEntityAttribute(el.tip()))
                            .build());
                    acc.appendToMap("sumaTotala", el.sumaTotala());
                    acc.appendToMap("rang", el.rang());
                    acc.appendToMap("rtr", el.rtr());
                    acc.appendToMap("nrComenzi", el.nrComenzi());
                    acc.appendToMap("dif", el.dif());
                    return acc;
                }, ReduceTemp::merge)
                .map(ReduceTemp::getChartDto);
    }

    public ChartDto<Key10_9> get109(
            Long timpStart,
            Long timpEnd,
            Integer rankLimit
    ) {
        return jdbcClient.sql("""
                                select * from (
                                SELECT
                                    p.tip,
                                    SUM(i.suma_incasare) AS suma_totala,
                                    DENSE_RANK() OVER (
                                        ORDER BY
                                            SUM(i.suma_incasare) desc
                                    ) AS rang,
                                    ratio_to_report (SUM(i.suma_incasare)) OVER () AS rtr,
                                    COUNT(*) AS nr_comenzi,
                                    SUM(i.suma_incasare) - lead (SUM(i.suma_incasare), 1) OVER (
                                        ORDER BY
                                            SUM(i.suma_incasare) desc
                                    ) AS dif
                                FROM
                                    incasare_timp_part i
                                    JOIN plata p ON i.id_incasare = p.id_plata
                                    JOIN timp t ON i.id_timp = t.id_timp
                                WHERE
                                    (:timpStart IS NULL OR t.id_timp >= :timpStart)
                                    AND (:timpEnd IS NULL OR t.id_timp <= :timpEnd)
                                GROUP BY
                                    p.tip
                                )
                                where (
                                    :rankLimit IS NULL
                                    OR rang <= :rankLimit
                                )
                                order by rang
                        
                        """)
                .param("timpStart", timpStart, SqlTypes.BIGINT)
                .param("timpEnd", timpEnd, SqlTypes.BIGINT)
                .param("rankLimit", rankLimit, SqlTypes.INTEGER)
                .query(DTO10_9.class)
                .stream().parallel()
                .reduce(new ReduceTemp<Key10_9>(), (acc, el) -> {
                    acc.getLabels().add(Key10_9.builder()
                            .tip(convertorFactory.getStringEnumConverter(PlataTip.class)
                                    .convertToEntityAttribute(el.tip()))
                            .build());
                    acc.appendToMap("nrComenzi", el.nrComenzi());
                    acc.appendToMap("sumaTotala", el.sumaTotala());
                    acc.appendToMap("rang", el.rang());
                    acc.appendToMap("rtr", el.rtr());
                    acc.appendToMap("dif", el.dif());
                    return acc;
                }, ReduceTemp::merge)
                .map(ReduceTemp::getChartDto);
    }

    private Object bindStringList(List<String> list) {
        return list == null ? new SqlParameterValue(Types.VARCHAR, null) : list;
    }

}
