package it.thinkopen.academy.spring;

import java.time.*;
import java.time.format.DateTimeFormatter;

public final class Utils {
    public static final DateTimeFormatter DEFAULT_DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DEFAULT_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static Long toSeconds(final LocalDateTime ldt) {
        if(ldt == null) return null;
        final ZoneId zoneId = ZoneId.systemDefault();
        return ldt.atZone(zoneId).toEpochSecond();
    }

    public static LocalDateTime toLocalDateTime(final Long secs) {
        if(secs == null) return null;
        final ZoneId zoneId = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(secs), zoneId);
    }

    public static LocalDateTime parseExpirationDate(LocalDateTime date, String exp) {
        if(exp.isEmpty())
            return null;

        try {
            return LocalDateTime.parse(exp, DEFAULT_DATETIME_FORMAT);
        }catch (Exception e){}

        try {
            final LocalDate ld = LocalDate.parse(exp, DEFAULT_DATE_FORMAT);
            return LocalDateTime.of(ld.getYear(), ld.getMonth(), ld.getDayOfMonth(), 0, 0, 0);
        }catch (Exception e){}

        try {
            final LocalTime lt = LocalTime.parse(exp, DEFAULT_TIME_FORMAT);
            return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), lt.getHour(), lt.getMinute(), lt.getSecond());
        }catch (Exception e){}

        return null;
    }
}
