package com.overflow.libs.core;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.concurrent.TimeUnit.*;
public class DateUtil {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_DEFAULT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static final SimpleDateFormat DATE_TIME_FORMAT_DEFAULT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.getDefault());

    public static final List<Long> times =
            Arrays.asList(
                    DAYS.toMillis(365), DAYS.toMillis(30), DAYS.toMillis(7),
                    DAYS.toMillis(1), HOURS.toMillis(1), MINUTES.toMillis(1),
                    SECONDS.toMillis(1));

    public static final List<String> timesString = Arrays.asList("tahun", "bulan", "minggu", "hari", "jam", "menit", "detik");

    /**
     * Get relative time ago for date
     *
     * NOTE: if (duration > WEEK_IN_MILLIS) getRelativeTimeSpanString prints the
     * date.
     *
     * ALT: return getRelativeTimeSpanString(date, now, SECOND_IN_MILLIS,
     * FORMAT_ABBREV_RELATIVE);
     *
     * @param date
     *            String.valueOf(TimeUtils.getRelativeTime(1000L * Date/Time in
     *            Millis)
     * @return relative time
     */
    public static CharSequence getTimeAgo(final long date) {
        return toDuration(new Date().getTime() - date);
    }

    private static String toDuration(long duration) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times.size(); i++) {
            Long current = times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                sb.append(temp).append(" ").append(timesString.get(i)).append(" lalu");
                break;
            }
        }
        return sb.toString().isEmpty() ? "b" : sb.toString();
    }

    public static String printDate(long dateLong){
        return printDate(new Date(dateLong));
    }

    public static String printDate(Date date){
        return DATE_FORMAT.format(date);
    }

    public static String printDefaultDate(Date date){
        return DATE_FORMAT_DEFAULT.format(date);
    }

    public static String printDefaultDateTime(Date date){
        return DATE_TIME_FORMAT_DEFAULT.format(date);
    }

    public static String printDateTime(long dateLong){
        return printDateTime(new Date(dateLong));
    }

    public static String printDateTime(Date date){
        return DATETIME_FORMAT.format(date);
    }

}
