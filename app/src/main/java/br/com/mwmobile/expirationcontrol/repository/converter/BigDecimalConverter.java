package br.com.mwmobile.expirationcontrol.repository.converter;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

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
        if(value != null && !TextUtils.isEmpty(value)) return new BigDecimal(value);
        return null;
    }

    @TypeConverter
    public static String toString(BigDecimal value) {
        return value == null ? null : value.toString();
    }
}