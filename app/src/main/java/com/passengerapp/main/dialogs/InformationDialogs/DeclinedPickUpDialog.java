package com.passengerapp.main.dialogs.InformationDialogs;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.passengerapp.R;

/**
 * Created by adventis on 10/7/15.
 */
public class DeclinedPickUpDialog extends BaseDialog {
    public DeclinedPickUpDialog(Context context) {
        super(context);
        setContentView(R.layout.popup_driver_declined);

        Button btnDeclineOk = (Button) findViewById(R.id.btnDeclineOk);
        btnDeclineOk.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    dismiss();
                }
                return false;
            }
        });
    }
}
