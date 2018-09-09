package com.training.dr.androidtraining.presentation.common.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.training.dr.androidtraining.R;
import com.training.dr.androidtraining.presentation.common.events.OnResultDialog;

/**
 * Created by dr on 05.02.2017.
 */

public class LogOutDialog extends DialogFragment implements DialogInterface.OnClickListener {
    public static final int RESULT_NO = 656565;
    public static final int RESULT_YES = 656365;

    private OnResultDialog callback;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        callback = (OnResultDialog) getActivity();
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.log_out))
                .setPositiveButton(R.string.button_yes, this)
                .setNegativeButton(R.string.button_no, this)
                .setMessage(R.string.logout_dialog_question);
        return adb.create();
    }

    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                callback.onDialogRespond(dialog, RESULT_YES);
                break;
            case Dialog.BUTTON_NEGATIVE:
                callback.onDialogRespond(dialog, RESULT_NO);
                break;
        }
    }
}