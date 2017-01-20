package com.passengerapp.main.dialogs.InformationDialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by adventis on 10/7/15.
 */
public class BaseDialog extends Dialog  {
    public IInformationDialogsActions delegate;
    public BaseDialog(Context context) {
        super(context);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setDelegate(IInformationDialogsActions delegate) {
        this.delegate = delegate;
    }
}
