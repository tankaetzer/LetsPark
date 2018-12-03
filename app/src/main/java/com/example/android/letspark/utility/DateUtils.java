package com.example.android.letspark.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Provide method in formatting date.
 */
public class DateUtils {

    /**
     * @param unixTime unix time in millisecond
     * @return human readable date in following form: 2018-12-31 07:35 pm
     */
    public static String convertAndFormatUnixTimeToDateTime(long unixTime) {
        Date date = new Date(unixTime);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a"
                , new Locale("ms", "MY"));
        return dateFormatter.format(date);
    }

    /**
     * @param unixTime unix time in millisecond
     * @return human readable time in following form: 07:35 pm
     */
    public static String convertAndFormatUnixTimeToTime(long unixTime) {
        Date date = new Date(unixTime);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm a"
                , new Locale("ms", "MY"));
        return dateFormatter.format(date);
    }
}
