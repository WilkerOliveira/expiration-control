package br.com.mwmobile.expirationcontrol.ui.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import br.com.mwmobile.expirationcontrol.R;

/**
 * Preferences Manager
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 26/11/2017
 */

public class PreferencesManager {

    /**
     * Get the Expiration Days
     *
     * @param context Context
     * @return Expiration Days
     */
    public static String getExpirationDays(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            return preferences.getString(context.getString(R.string.expiration_days_alert), context.getString(R.string.default_expiration_days));

        }
        return "";
    }
}
