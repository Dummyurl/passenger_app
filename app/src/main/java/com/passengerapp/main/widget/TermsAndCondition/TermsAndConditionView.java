package com.passengerapp.main.widget.TermsAndCondition;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.passengerapp.R;

/**
 * Created by adventis on 10/1/15.
 */
public class TermsAndConditionView extends LinearLayout {
    private CheckBox termsAndConditionsCheckbox;
    private OnShowTermsAndCondition activityShowTermsAndConditionsCallback;

    public TermsAndConditionView(Context context) {
        super(context);
    }

    public TermsAndConditionView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.custom_view_terms_and_condition, this, true);

        termsAndConditionsCheckbox = (CheckBox) getChildAt(0);
        TextView termsAndConditionDescribe = (TextView)getChildAt(2);
        termsAndConditionDescribe.setPaintFlags(termsAndConditionDescribe.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        termsAndConditionDescribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activityShowTermsAndConditionsCallback != null) {
                    activityShowTermsAndConditionsCallback.showTermsAndConditions();
                }
            }
        });
    }

    public boolean isAccepted() {
        return termsAndConditionsCheckbox.isChecked();
    }

    public void setParentActivity(OnShowTermsAndCondition parentActivity) {
        activityShowTermsAndConditionsCallback = parentActivity;
    }


}
