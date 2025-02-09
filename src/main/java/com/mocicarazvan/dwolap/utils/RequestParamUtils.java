package com.mocicarazvan.dwolap.utils;

import java.time.LocalDate;
import java.time.ZoneId;

public class RequestParamUtils {

    private static final String UTC_ZONE = "UTC";

    public static Long localDateToUnix(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay().toEpochSecond(
                ZoneId.of(UTC_ZONE).getRules().getOffset(localDate.atStartOfDay())
        );

    }

    public static Integer groupQuery(Boolean gr) {
        if (gr == null) {
            return null;
        }
        return gr ? 0 : 1;
    }
}
