package com.amplify.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by bitware on 10/3/18.
 */

public class TextViewSemibold extends TextView{

    public TextViewSemibold(Context context) {
        super(context);
        setFont();
    }

    public TextViewSemibold(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public TextViewSemibold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TextViewSemibold(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setFont();
    }
    public void setFont(){

        Typeface typedValue = Typeface.createFromAsset(getContext().getAssets(), "lato_semibold.ttf");
        setTypeface(typedValue);
    }
}
