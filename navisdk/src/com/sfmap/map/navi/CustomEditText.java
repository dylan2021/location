package com.sfmap.map.navi;

import android.content.Context;
import android.util.AttributeSet;

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {
    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }


    public void setText(String text) {
        int length = text.length();
        String result = (length <= 14 ? text : text.substring(0, 11) + "...");
        super.setText(result);
    }
}

