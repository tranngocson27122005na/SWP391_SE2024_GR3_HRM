package com.hrm.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateFormats {
    private static final String ISO = "yyyy-MM-dd";

    private DateFormats() {
    }

    public static Date parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            SimpleDateFormat fmt = new SimpleDateFormat(ISO);
            fmt.setLenient(false);
            return fmt.parse(raw.trim());
        } catch (ParseException e) {
            return null;
        }
    }
}
