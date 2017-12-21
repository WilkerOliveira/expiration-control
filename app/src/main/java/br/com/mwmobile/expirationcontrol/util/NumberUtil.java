package br.com.mwmobile.expirationcontrol.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Number Util class
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 29/09/2017
 */

public class NumberUtil {

    /**
     * Parse the value to Currency BigDecimal
     *
     * @param value Value to parse
     * @return BigDecimal value
     */
    public static BigDecimal toCurrencyBigDecimal(String value) {
        String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance(Locale.getDefault()).getCurrency().getSymbol());

        String cleanString = value.replaceAll(replaceable, "").replaceAll("[^\\d.,]", "");

        if (TextUtils.isEmpty(cleanString)) return BigDecimal.ZERO;

        return new BigDecimal(cleanString).setScale(
                2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR
        );
    }

    /**
     * Parse the currency value to String
     *
     * @param value Value to parse
     * @return String value
     */
    public static String currencyToString(BigDecimal value) {
        if (value != null) {
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.getDefault());

            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);

            return nf.format(value);
        }
        return "";
    }

}
