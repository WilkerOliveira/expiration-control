package br.com.mwmobile.expirationcontrol.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.mwmobile.expirationcontrol.exception.CustomError;
import br.com.mwmobile.expirationcontrol.repository.local.model.Product;

/**
 * Date util class
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 28/09/2017
 */

public class DateUtil {

    /**
     * Parse a date to string
     *
     * @param date Date to parse
     * @return Date as string
     */
    public static String parseToString(Date date) {
        if (date == null) return "";

        return DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(date);
    }

    /**
     * Validate the date
     *
     * @param dateTo Date to validate
     * @throws CustomError ErrorType during validation
     */
    public static void validateDate(Date dateTo) throws CustomError {

        if (dateTo == null)
            throw new CustomError(ErrorType.INVALID_EXPIRATION_DATE.getStringErrorType());
    }

    /**
     * Return the Diff days between two dates
     *
     * @param date1 Date one
     * @param date2 Date two
     * @return Diff days
     */
    public static int getDifferenceDays(Date date1, Date date2) {
        int daysdiff = 0;
        long diff = date2.getTime() - date1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        daysdiff = (int) diffDays;
        return daysdiff;
    }

    /**
     * Set the Status of Product Expiration
     *
     * @param expirationDays Expiration Days
     * @param product        Product data
     */
    public static void setExpirationStatus(int expirationDays, Product product) {
        try {
            DateFormat sdf = SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());

            Calendar expirationDate = Calendar.getInstance();

            expirationDate.setTime(sdf.parse(DateUtil.parseToString(product.getExpiration())));

            int msDiff = DateUtil.getDifferenceDays(Calendar.getInstance().getTime(), expirationDate.getTime());

            if (msDiff <= 0) {
                product.setStatus(ExpirationStatus.EXPIRATED);
            } else if (msDiff <= expirationDays) {
                product.setStatus(ExpirationStatus.WARNING);
            } else {
                product.setStatus(ExpirationStatus.VALID_PERIOD);
            }

        } catch (ParseException e) {
            Log.e(Constants.ERROR_TAG, e.getMessage());
            //Do Nothing
        }

    }

}