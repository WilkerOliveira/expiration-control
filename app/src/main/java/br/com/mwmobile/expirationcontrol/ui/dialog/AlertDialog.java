package br.com.mwmobile.expirationcontrol.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.ListAdapter;

import br.com.mwmobile.expirationcontrol.R;

/**
 * Class to manage the Alert Dialog
 */
public class AlertDialog extends DialogFragment {

    private int alertType;
    private String message;
    private String title;
    private DialogInterface.OnClickListener firstButtonEvent;
    private DialogInterface.OnClickListener secondButtonEvent;
    private boolean multiList;
    private ListAdapter listAdapter;
    private int textFirstButton;
    private int textSecondButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        android.app.AlertDialog.Builder builder;

        switch (alertType) {
            case DialogType.OK_CANCEL:
                builder = getOkCancelDialog();
                break;
            case DialogType.OK:
                builder = getOkDialog();
                break;
            case DialogType.MULTI_CHOICE:
                builder = getMultiChoiceDialog();
                break;
            default:
                builder = getYesNoDialog();
                break;
        }

        return builder.create();
    }

    private android.app.AlertDialog.Builder getOkDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

        builder.setMessage(this.message).setPositiveButton(R.string.ok_button, firstButtonEvent);

        return builder;
    }

    private android.app.AlertDialog.Builder getOkCancelDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

        builder.setMessage(this.message).setPositiveButton(R.string.ok_button, firstButtonEvent).setNegativeButton(R.string.cancel_button, secondButtonEvent);

        return builder;
    }

    private android.app.AlertDialog.Builder getYesNoDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

        builder.setMessage(this.message).setPositiveButton(R.string.yes_button, firstButtonEvent).setNegativeButton(R.string.no_button, secondButtonEvent);

        return builder;
    }

    private android.app.AlertDialog.Builder getMultiChoiceDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

        builder.setMessage(this.message).setPositiveButton(getTextFirstButton(), firstButtonEvent).setNegativeButton(getTextSecondButton(), secondButtonEvent);

        return builder;
    }

    public void setFirstButtonEvent(DialogInterface.OnClickListener firstButtonEvent) {
        this.firstButtonEvent = firstButtonEvent;
    }

    public void setAlertType(int alertType) {
        this.alertType = alertType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTextFirstButton() {
        return textFirstButton;
    }

    public int getTextSecondButton() {
        return textSecondButton;
    }


}
