package com.example.android.letspark.utility;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Provide method in formatting number.
 */
public class NumberUtils {

    /**
     * This method format any number according to Malaysia currency.
     *
     * @param rate parking price rate for an hour
     * @return formatted price in following form: RM0.80
     */
    public static String formatAndDisplayMalaysiaCurrency(double rate) {
        NumberFormat numberFormat = NumberFormat
                .getCurrencyInstance(new Locale("ms", "MY"));

        return numberFormat.format(rate);
    }
}