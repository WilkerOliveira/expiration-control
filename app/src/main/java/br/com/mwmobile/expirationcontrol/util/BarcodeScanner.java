package br.com.mwmobile.expirationcontrol.util;

import android.app.Activity;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * BarcodeScanner class
 *
 * @author Wilker Oliveira - wilker.oliveira@gmail.com
 * @version 1.0.0
 * @since 03/12/2017
 */

public class BarcodeScanner {

    /**
     * Returna an Instance of  MaterialBarcodeScannerBuilder
     *
     * @param text    Text to display while searching
     * @param context Activity context
     * @return Instance of MaterialBarcodeScanner
     */
    public static MaterialBarcodeScannerBuilder newBuilderInstance(String text, Activity context) {
        return new MaterialBarcodeScannerBuilder()
                .withActivity(context)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withBarcodeFormats(Barcode.AZTEC | Barcode.EAN_13 | Barcode.CODE_93)
                .withText(text);
    }
}
