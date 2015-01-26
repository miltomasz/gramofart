package com.soldiersofmobile.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.soldiersofmobile.R;
import com.soldiersofmobile.utils.FontCache;

public class CustomFontTextView extends TextView {
    private static final int DEFAULT_ANGLE = 0;
    private int mTextAngle = DEFAULT_ANGLE;
    public static final String DEFAULT_FONT = "Roboto-Regular.ttf";

    public CustomFontTextView(Context context) {
        super(context);
        setCustomFont(context, DEFAULT_FONT);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        setPaintFlags(getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG|Paint.DEV_KERN_TEXT_FLAG|
                Paint.ANTI_ALIAS_FLAG);
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomFontTextView);
        String customFont = a.getString(R.styleable.CustomFontTextView_customFont);
        mTextAngle = a.getInt(R.styleable.CustomFontTextView_textAngle, DEFAULT_ANGLE);
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

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();

        //now we change the matrix
        //We need to rotate around the center of our text
        //Otherwise it rotates around the origin, and that's bad. 
        float py = this.getHeight() / 2.0f;
        float px = this.getWidth() / 2.0f;

        if (mTextAngle != DEFAULT_ANGLE)
            canvas.rotate(mTextAngle, px, py);

        //draw the text with the matrix applied. 
        super.onDraw(canvas);

        //restore the old matrix. 
        canvas.restore();
    }

}