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

    public DialogInterface.OnClickListener getFirstButtonEvent() {
        return firstButtonEvent;
    }

    public void setFirstButtonEvent(DialogInterface.OnClickListener firstButtonEvent) {
        this.firstButtonEvent = firstButtonEvent;
    }

    public DialogInterface.OnClickListener getSecondButtonEvent() {
        return secondButtonEvent;
    }

    public void setSecondButtonEvent(DialogInterface.OnClickListener secondButtonEvent) {
        this.secondButtonEvent = secondButtonEvent;
    }

    public int getAlertType() {
        return alertType;
    }

    public void setAlertType(int alertType) {
        this.alertType = alertType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isMultiList() {
        return multiList;
    }

    public void setMultiList(boolean multiList) {
        this.multiList = multiList;
    }

    public ListAdapter getListAdapter() {
        return listAdapter;
    }

    public void setListAdapter(ListAdapter listAdapter) {
        this.listAdapter = listAdapter;
    }

    public int getTextFirstButton() {
        return textFirstButton;
    }

    public void setTextFirstButton(int textFirstButton) {
        this.textFirstButton = textFirstButton;
    }

    public int getTextSecondButton() {
        return textSecondButton;
    }

    public void setTextSecondButton(int textSecondButton) {
        this.textSecondButton = textSecondButton;
    }


}
