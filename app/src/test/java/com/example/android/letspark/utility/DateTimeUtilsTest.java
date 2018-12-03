package com.example.android.letspark.utility;

import org.junit.Test;

import static com.example.android.letspark.utility.DateUtils.convertAndFormatUnixTimeToDateTime;
import static com.example.android.letspark.utility.DateUtils.convertAndFormatUnixTimeToTime;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DateTimeUtilsTest {

    @Test
    public void convertUnixTimeToDateTimeTest() {
        String dateTime = convertAndFormatUnixTimeToDateTime(Long.valueOf("1542022506879"));
        assertThat(String.valueOf(dateTime), is("2018-11-12 07:35 PM"));
    }

    @Test
    public void convertUnixTimeToTimeTest() {
        String dateTime = convertAndFormatUnixTimeToTime(Long.valueOf("1542022506879"));
        assertThat(String.valueOf(dateTime), is("07:35 PM"));
    }
}
