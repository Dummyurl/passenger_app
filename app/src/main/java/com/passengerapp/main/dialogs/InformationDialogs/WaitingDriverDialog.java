package com.passengerapp.main.dialogs.InformationDialogs;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.passengerapp.R;

/**
 * Created by adventis on 10/7/15.
 */
public class WaitingDriverDialog extends BaseDialog {

    public WaitingDriverDialog(Context context) {
        super(context);

        this.setContentView(R.layout.popup_progressdialog);
        Button ivDrnConfCancel = (Button) findViewById(R.id.ivDrnConfCancel);
        ivDrnConfCancel.setOnTouchListener(new View.OnTouchListener() {

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
