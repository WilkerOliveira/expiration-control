package br.com.mwmobile.expirationcontrol.repository.converter;

import android.arch.persistence.room.TypeConverter;

import java.math.BigDecimal;

/**
 * Converter to BigDecimal values
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 24/09/2017
 */
public class BigDecimalConverter {

    @TypeConverter
    public static BigDecimal toBigDecimal(String value) {
        return new BigDecimal(value);
    }

    @TypeConverter
    public static String toString(BigDecimal value) {
        return value == null ? null : value.toString();
    }
}