package com.ihomey.linkuphome.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.ihomey.linkuphome.R;

import static com.ihomey.linkuphome.ExtKt.dip2px;


public class RGBCircleView extends View {
    private static final String TAG = "CircleTimerView";

    private static final int[] colors = {R.color.c2, R.color.c3, R.color.c4, R.color.c5, R.color.c6, R.color.c7, R.color.c8, R.color.c9, R.color.c10, R.color.c11, R.color.c12,
            R.color.c13, R.color.c14, R.color.c15, R.color.c16, R.color.c17, R.color.c18, R.color.c19, R.color.c20, R.color.c21, R.color.c22, R.color.c23, R.color.c24, R.color.c1};

    // Status
    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_RADIAN = "status_radian";

    // Default dimension in dp/pt
    private static final float DEFAULT_GAP_BETWEEN_CIRCLE_AND_LINE = 5;
    private static final float DEFAULT_LINE_LENGTH = 14;
    private static final float DEFAULT_CIRCLE_BUTTON_RADIUS = 15;
    private static final float DEFAULT_CIRCLE_STROKE_WIDTH = 10;
    // Default color
    private static final int DEFAULT_HIGHLIGHT_LINE_COLOR = 0xFF891E89;
    // Paint
    private Paint mCirclePaint;

    // Dimension
    private float mGapBetweenCircleAndLine;
    private float mLineLength;
    private float mCircleButtonRadius;
    private float mCircleStrokeWidth;

    // Color
    private int mCircleColor;

    // Parameters
    private float mCx;
    private float mCy;
    private float mRadius;
    private float mCurrentRadian;
    private float mPreRadian;
    private int mCurrentTime; // seconds
    private int mCurrentIndex; // seconds

    private long startTime;
    private long endTime;

    private float downX;
    private float downY;

    private float moveX;
    private float moveY;


    // Runt
    private ColorValueListener mCircleTimerListener;

    public RGBCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public RGBCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RGBCircleView(Context context) {
        this(context, null);
    }

    private void initialize() {
        // Set default dimension or read xml attributes
        mGapBetweenCircleAndLine = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_GAP_BETWEEN_CIRCLE_AND_LINE,
                getContext().getResources().getDisplayMetrics());

        mLineLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_LINE_LENGTH, getContext().getResources()
                .getDisplayMetrics());
        mCircleButtonRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_BUTTON_RADIUS, getContext()
                .getResources().getDisplayMetrics());
        mCircleStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CIRCLE_STROKE_WIDTH, getContext()
                .getResources().getDisplayMetrics());
        // Set default color or read xml attributes
        mCircleColor = DEFAULT_HIGHLIGHT_LINE_COLOR;

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCirclePaint.setColor(mCircleColor);
        canvas.drawCircle(mCx, mCy, mRadius -dip2px(this.getContext(), 45), mCirclePaint);
        canvas.rotate((float) Math.toDegrees(mCurrentRadian), mCx, mCy);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.control_icon_arrow);
        canvas.drawBitmap(bitmap, getMeasuredWidth() / 2 - bitmap.getWidth() / 2, mCy - mRadius + dip2px(this.getContext(), 51), mCirclePaint);
        super.onDraw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                downX = event.getX();//float DownX
                downY = event.getY();//float DownY
                moveX = 0;
                moveY = 0;
                mPreRadian = getRadian(event.getX(), event.getY());
                handler.postDelayed(runnable, 200);
                break;
            case MotionEvent.ACTION_MOVE:
                moveX += Math.abs(event.getX() - downX);//X轴距离
                moveY += Math.abs(event.getY() - downY);//y轴距离
                downX = event.getX();
                downX = event.getY();
                float temp = getRadian(event.getX(), event.getY());
                mCurrentRadian += (temp - mPreRadian);
                mPreRadian = temp;
                if (mCurrentRadian > 2 * Math.PI) {
                    mCurrentRadian = mCurrentRadian - (float) (2 * Math.PI);
                } else if (mCurrentRadian < 0) {
                    mCurrentRadian = mCurrentRadian + (float) (2 * Math.PI);
                }
                int colorIndex = getIndexColor(getCurrentTime());
                setCircleBgColor(colorIndex);
                mCurrentTime = (int) (60 / (2 * Math.PI) * mCurrentRadian * 60);
                break;
            case MotionEvent.ACTION_UP:
                endTime = System.currentTimeMillis();
                handler.removeCallbacks(runnable);
                if (endTime - startTime > 200 && (moveX > 20 || moveY > 20)) {
                    if (mCurrentIndex == 0 || mCurrentIndex != getIndexColor(getCurrentTime())) {
                        if (mCircleTimerListener != null) {
                            mCircleTimerListener.onColorValueChanged(getIndex(getCurrentTime()));
                            mCurrentIndex = getIndexColor(getCurrentTime());
                        }
                    }
                } else {
                    mCurrentRadian = getRadian(event.getX(), event.getY());
                    mCurrentTime = (int) (60 / (2 * Math.PI) * mCurrentRadian * 60);
                    int colorIndex2 = getIndexColor(getCurrentTime());
                    setCircleBgColor(colorIndex2);
                    if (mCurrentIndex == 0 || mCurrentIndex != getIndexColor(getCurrentTime())) {
                        if (mCircleTimerListener != null) {
                            mCircleTimerListener.onColorValueChanged(getIndex(getCurrentTime()));
                            mCurrentIndex = getIndexColor(getCurrentTime());
                        }
                    }
                }
                break;

        }
        return true;
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mCurrentIndex == 0 || mCurrentIndex != getIndexColor(getCurrentTime())) {
                if (mCircleTimerListener != null) {
                    mCircleTimerListener.onColorValueChange(getIndex(getCurrentTime()));
                    mCurrentIndex = getIndexColor(getCurrentTime());
                }
            }
            handler.postDelayed(this, 200);
        }
    };


    private int getIndexColor(int time) {
        int d1 = time / 151;
        return colors[d1];
    }

    private int getIndex(int time) {
        int d1 = time / 151;
        return d1;
    }

    public void setCurrentRadian(float radian) {
        mCurrentRadian = radian;
        mCurrentTime = (int) (60 / (2 * Math.PI) * mCurrentRadian * 60);
        int colorIndex = getIndexColor(getCurrentTime());
        setCircleBgColor(colorIndex);
    }

    // Use tri to cal radian
    private float getRadian(float x, float y) {
        float alpha = (float) Math.atan((x - mCx) / (mCy - y));
        // Quadrant
        if (x > mCx && y > mCy) {
            // 2
            alpha += Math.PI;
        } else if (x < mCx && y > mCy) {
            // 3
            alpha += Math.PI;
        } else if (x < mCx && y < mCy) {
            // 4
            alpha = (float) (2 * Math.PI + alpha);
        }
        return alpha;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Ensure width = height
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        this.mCx = width / 2;
        this.mCy = height / 2;
        // Radius
        if (mLineLength / 2 + mGapBetweenCircleAndLine + mCircleStrokeWidth >= mCircleButtonRadius) {
            this.mRadius = width / 2 - mCircleStrokeWidth / 2;
            Log.d(TAG, "No exceed");
        } else {
            this.mRadius = width / 2 - (mCircleButtonRadius - mGapBetweenCircleAndLine - mLineLength / 2 -
                    mCircleStrokeWidth / 2);
            Log.d(TAG, "Exceed");
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putFloat(STATUS_RADIAN, mCurrentRadian);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            mCurrentRadian = bundle.getFloat(STATUS_RADIAN);
            mCurrentTime = (int) (60 / (2 * Math.PI) * mCurrentRadian * 60);
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    /**
     * set the hint text, default is 时间设置
     *
     * @param color String value
     */
    public void setCircleBgColor(int color) {
        if (color != 0) {
            mCircleColor = getResources().getColor(color);
        }
        invalidate();
    }

    /**
     * set timer listener
     *
     * @param mCircleTimerListener
     */
    public void setColorValueListener(ColorValueListener mCircleTimerListener) {
        this.mCircleTimerListener = mCircleTimerListener;
    }

    /**
     * get current time in seconds
     *
     * @return
     */
    public int getCurrentTime() {
        return mCurrentTime;
    }


    public float getCurrentRadian() {
        return mCurrentRadian;
    }

    public int getCurrentIndex() {
        return getIndex(getCurrentTime());
    }

    public interface ColorValueListener {
        /**
         * launch timer set value changed event
         *
         * @param time
         */
        void onColorValueChanged(int time);


        /**
         * launch timer set value chang event
         *
         * @param time
         */
        void onColorValueChange(int time);
    }

}
