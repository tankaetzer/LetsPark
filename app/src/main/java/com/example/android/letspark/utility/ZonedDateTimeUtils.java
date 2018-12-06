package com.example.android.letspark.utility;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;

/**
 * This class stores all date and time fields, to a precision of nanoseconds, and a time-zone,
 * with a zone offset used to handle ambiguous local date-times.
 */
public class ZonedDateTimeUtils {

    public static long durationBetweenTwoDateTime(long endTime) {
        Instant instantEndTime = Instant.ofEpochMilli(endTime);
        // Malaysia standard offset is +8
        DateTimeZone zoneUTC = DateTimeZone.forOffsetHours(+8);
        DateTime dtEndTime = new DateTime(instantEndTime, zoneUTC);

        DateTime today = new DateTime();
        DateTime todayFivePm = today.withHourOfDay(17).withMinuteOfHour(0).withSecondOfMinute(0);

        Duration duration = new Duration(dtEndTime, todayFivePm);
        return duration.getStandardHours();
    }
}
