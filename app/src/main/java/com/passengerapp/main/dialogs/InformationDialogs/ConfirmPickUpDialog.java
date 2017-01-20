package com.passengerapp.main.dialogs.InformationDialogs;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.passengerapp.R;

/**
 * Created by adventis on 10/7/15.
 */
public class ConfirmPickUpDialog extends BaseDialog {

    public ConfirmPickUpDialog(Context context) {
        super(context);
        setContentView(R.layout.popup_driver_confirmed);

        ImageView btnSetConfrm = (ImageView) findViewById(R.id.btnSetConfrm);
        ImageView btnConfrmOk = (ImageView) findViewById(R.id.btnConfrmOk);
        LinearLayout dialogViewerId = (LinearLayout) findViewById(R.id.dialogViewerId);

        btnSetConfrm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    dismiss();
                    if(delegate != null)
                        delegate.acceptPickUp();
                }
                return true;
            }
        });
        btnConfrmOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        dialogViewerId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismiss();
            }
        });
    }
}
