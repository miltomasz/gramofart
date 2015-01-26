package com.soldiersofmobile.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.soldiersofmobile.R;
import com.soldiersofmobile.utils.FontCache;

public class CustomFontEditText extends EditText {
    public static final String DEFAULT_FONT = "Roboto-Regular.ttf";

    public CustomFontEditText(Context context) {
        super(context);
        setCustomFont(context, DEFAULT_FONT);
    }

    public CustomFontEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomFontEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        String customFont = a.getString(R.styleable.CustomFontTextView_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public void setCustomFont(Context ctx, String font) {
        if(font == null) {
            return;
        }
        Typeface tf = FontCache.get(font, ctx);
        if(tf != null) {
            setTypeface(tf);
        }
    }

}
