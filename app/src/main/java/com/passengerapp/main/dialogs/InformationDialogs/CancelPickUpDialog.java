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
public class CancelPickUpDialog extends BaseDialog {
    public CancelPickUpDialog(Context context) {
        super(context);
        setContentView(R.layout.popup_pickup_canceled);

        Button ivCancelOk = (Button) this.findViewById(R.id.ivCancelOk);
        ivCancelOk.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    dismiss();
                }
                return false;
            }
        });
    }

    public void setText(String text) {
        TextView txtCaslReason = (TextView) this.findViewById(R.id.txtCaslReason);
        txtCaslReason.setText(text);
    }
}
