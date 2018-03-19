package com.amplify.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

public class SoftKeyboardHandledLinearLayout extends LinearLayout {
	private boolean isKeyboardShown;
	private SoftKeyboardVisibilityChangeListener listener;
 
	public SoftKeyboardHandledLinearLayout(Context context) {
		super(context);
	}
 
	public SoftKeyboardHandledLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
 
	@SuppressLint("NewApi")
	public SoftKeyboardHandledLinearLayout(Context context, AttributeSet attrs,
                                           int defStyle) {
		super(context, attrs, defStyle);
	}
 
	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			// Keyboard is hidden <<< RIGHT
			if (isKeyboardShown) {
				isKeyboardShown = false;
				listener.onSoftKeyboardHide();
			}
		}
		return super.dispatchKeyEventPreIme(event);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
    	final int actualHeight = getHeight();
    	if (actualHeight > proposedheight) {
			// Keyboard is shown
			if (!isKeyboardShown) {
				isKeyboardShown = true;
				listener.onSoftKeyboardShow();
			}
    	} else {
	        // Keyboard is hidden <<< this doesn't work sometimes, so I don't use it
    	}
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public void setOnSoftKeyboardVisibilityChangeListener(SoftKeyboardVisibilityChangeListener listener) {
		this.listener = listener;
	}
	
	// Callback
	public interface SoftKeyboardVisibilityChangeListener {
		public void onSoftKeyboardShow();
		public void onSoftKeyboardHide();
	}	
}
