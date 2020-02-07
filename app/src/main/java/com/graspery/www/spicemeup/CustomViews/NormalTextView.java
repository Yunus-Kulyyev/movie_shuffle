package com.graspery.www.spicemeup.CustomViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class NormalTextView extends TextView {

    public NormalTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NormalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NormalTextView(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/helvetica.otf");
        setTypeface(tf ,Typeface.NORMAL);
    }
}