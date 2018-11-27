package com.example.android.letspark.utility;

import org.junit.Test;

import static com.example.android.letspark.utility.NumberUtils.formatAndDisplayMalaysiaCurrency;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class NumberUtilsTest {

    @Test
    public void formatAndDisplayMalaysiaCurrencyTest() {
        String rate = formatAndDisplayMalaysiaCurrency(0.6);

        assertThat(rate, is("RM0.60"));
    }
}
