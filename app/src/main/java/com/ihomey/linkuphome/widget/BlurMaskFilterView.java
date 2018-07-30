package com.ihomey.linkuphome.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.ihomey.linkuphome.R;

public class BlurMaskFilterView extends View {

    private Context mContext;

    public BlurMaskFilterView(Context context) {
        super(context);
        mContext = context;
    }

    public BlurMaskFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public BlurMaskFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap srcBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_splash_logo);
        int xOffset = (mContext.getResources().getDisplayMetrics().widthPixels - srcBitmap.getWidth()) / 2;
        int yOffset = (mContext.getResources().getDisplayMetrics().heightPixels - srcBitmap.getHeight()) / 2;
        Bitmap shadowBitmap = srcBitmap.extractAlpha();
        Paint mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#DDFFFFFF"));
        mPaint.setMaskFilter(new BlurMaskFilter(dp2px(getResources(),5.5f), BlurMaskFilter.Blur.SOLID));
        canvas.drawBitmap(shadowBitmap, xOffset, yOffset, mPaint);
        canvas.drawBitmap(srcBitmap, xOffset, yOffset, null);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }


    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

}
