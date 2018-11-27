package com.example.android.letspark.utility;

import org.junit.Test;

import static com.example.android.letspark.utility.DateUtils.convertAndFormatUnixTimeToDateTime;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DateTimeUtilsTest {

    @Test
    public void convertUnixTimeToDateTimeTest() {
        String dateTime = convertAndFormatUnixTimeToDateTime(Long.valueOf("1542022506879"));
        assertThat(String.valueOf(dateTime), is("2018-11-12 07:35 PM"));
    }
}
