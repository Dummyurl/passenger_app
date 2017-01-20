package com.passengerapp.main.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

/**
 * Created by adventis on 3/24/15.
 */
public class SimpleInfoDialog extends Dialog {

    private final String TAG = "SimpleInfoDialog";

    private void writeLog(String msg) {
        Log.d(TAG, msg);
    }

    public SimpleInfoDialog(Context context) {
        super(context);
        writeLog("SimpleInfoDialog(Context context)");
        initDialog();

    }

    public SimpleInfoDialog(Context context, int theme) {
        super(context, theme);
        writeLog("SimpleInfoDialog(Context context, int theme)");
        initDialog();
    }

    protected SimpleInfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        writeLog("SimpleInfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener)");
        initDialog();
    }

    private void initDialog() {
        writeLog("initDialog()");
    }
}
