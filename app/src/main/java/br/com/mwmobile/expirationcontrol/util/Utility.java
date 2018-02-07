package br.com.mwmobile.expirationcontrol.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import it.sephiroth.android.library.tooltip.Tooltip;

/**
 * Util class
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 14/01/2018
 */

public class Utility {

    /**
     * Show Tooltip helper
     *
     * @param context  Context
     * @param view     Anchor view
     * @param text     Texto to display
     * @param position Poisition to display the text
     */
    public static void showTooltipHelper(Context context, View view, String text, Tooltip.Gravity position) {
        Tooltip.make(context,
                new Tooltip.Builder(101)
                        .anchor(view, position)
                        .closePolicy(new Tooltip.ClosePolicy()
                                .insidePolicy(true, false)
                                .outsidePolicy(true, false), 3000)
                        .activateDelay(800)
                        .showDelay(300)
                        .text(text)
                        .maxWidth(500)
                        .withArrow(true)
                        .withOverlay(true)
                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
                        .build()
        ).show();
    }

    /**
     * Limit the Text
     *
     * @param text  Text to limite
     * @param limit Total limit
     * @return Text limited
     */
    public static String limitText(String text, int limit) {
        if (!TextUtils.isEmpty(text) && text.length() > limit) {
            return text.substring(0, limit) + "...";
        }

        return text;
    }
}
