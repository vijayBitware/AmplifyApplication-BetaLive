package com.amplify.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by bitware on 24/3/17.
 */

public class TextViewRegular extends TextView {

    public TextViewRegular(Context context) {
        super(context);
        setFont();
    }

    public TextViewRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public TextViewRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TextViewRegular(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setFont();
    }

    public void setFont(){

        Typeface typedValue = Typeface.createFromAsset(getContext().getAssets(), "MyriadPro-Regular.ttf");
        setTypeface(typedValue);
    }
}
