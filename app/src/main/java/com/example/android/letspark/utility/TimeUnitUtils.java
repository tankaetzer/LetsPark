package com.example.android.letspark.utility;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Provides methods for converting time across units such as day, hour, minute and second.
 */
public class TimeUnitUtils {

    public static final String MAP_HOUR = "HOUR";

    public static final String MAP_MINUTE = "MINUTE";

    public static final String MAP_SECOND = "SECOND";

    public static Map<String, Object> getHourMinuteSecond(long unixTime) {
        long hours = TimeUnit.MILLISECONDS.toHours(unixTime)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(unixTime));

        long minutes = TimeUnit.MILLISECONDS.toMinutes(unixTime)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(unixTime));

        long seconds = TimeUnit.MILLISECONDS.toSeconds(unixTime)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(unixTime));

        Map<String, Object> time = new HashMap<>();
        time.put(MAP_HOUR, hours);
        time.put(MAP_MINUTE, minutes);
        time.put(MAP_SECOND, seconds);

        return time;
    }
}
