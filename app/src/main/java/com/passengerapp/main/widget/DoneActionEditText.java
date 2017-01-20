package com.passengerapp.main.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * Created by adventis on 9/15/15.
 */

public class DoneActionEditText extends EditText
{
    public DoneActionEditText(Context context)
    {
        super(context);
    }

    public DoneActionEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DoneActionEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs)
    {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }
}
