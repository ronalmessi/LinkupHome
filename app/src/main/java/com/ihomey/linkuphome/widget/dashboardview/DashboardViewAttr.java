package com.ihomey.linkuphome.widget.dashboardview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import com.ihomey.linkuphome.R;

import static com.ihomey.linkuphome.ExtKt.dip2px;
import static com.ihomey.linkuphome.ExtKt.sp2px;


/**
 * Created by anderson on 2016/6/5.
 */
public class DashboardViewAttr {
    private int mTextSize;
    private int progressStrokeWidth;
    private String unit;//单位
    private int textColor;
    private float startNum;
    private float maxNum;
    private int padding;
    public DashboardViewAttr(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DashboardView, defStyleAttr, 0);
        mTextSize = ta.getDimensionPixelSize(sp2px(context,R.styleable.DashboardView_android_textSize), 48);
        progressStrokeWidth = (int) ta.getDimension(R.styleable.DashboardView_progressStrokeWidth, 24);
        unit = ta.getString(R.styleable.DashboardView_unit);
        textColor = ta.getColor(R.styleable.DashboardView_textColor, Color.WHITE);
        startNum = ta.getInt(R.styleable.DashboardView_startNumber, 0);
        padding = dip2px(context,ta.getInt(R.styleable.DashboardView_padding, 0));
        ta.recycle();
    }

    public int getPadding() {
        return padding;
    }

    public float getStartNumber() {
        return startNum;
    }

    public float getMaxNumber() {
        return maxNum;
    }


    public int getmTextSize() {
        return mTextSize;
    }


    public int getProgressStrokeWidth() {
        return progressStrokeWidth;
    }

    public String getUnit() {
        return unit;
    }

    public int getTextColor() {
        return textColor;
    }


}
