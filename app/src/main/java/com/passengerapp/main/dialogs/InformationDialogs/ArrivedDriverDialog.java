package com.passengerapp.main.dialogs.InformationDialogs;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.passengerapp.R;

/**
 * Created by adventis on 10/7/15.
 */
public class ArrivedDriverDialog extends BaseDialog {
    public ArrivedDriverDialog(Context context) {
        super(context);
        this.setContentView(R.layout.popup_pickup_arrived);

        Button ivCancelOk = (Button) findViewById(R.id.ivArrivedOk);

        ivCancelOk.setOnTouchListener(new View.OnTouchListener() {

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

    public void setText(String text) {
        TextView arrivedtxtview = (TextView) findViewById(R.id.arrivedtxtview);
        arrivedtxtview.setText(text);
    }
}
