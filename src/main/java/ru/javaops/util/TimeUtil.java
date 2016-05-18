package ru.javaops.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * GKislin
 * 18.05.2016
 */
public class TimeUtil {
    public static Date toDate(LocalDate ld) {
        return ld == null ? null :
                Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
