package com.amplify.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by bitware on 24/3/17.
 */

public class EditTextBold extends EditText {

    public EditTextBold(Context context) {
        super(context);
        setFont();
    }

    public EditTextBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public EditTextBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EditTextBold(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setFont();
    }
    public void setFont(){

        Typeface typedValue = Typeface.createFromAsset(getContext().getAssets(), "MyriadPro-Bold.otf");
        setTypeface(typedValue);
    }
}
