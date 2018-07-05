package com.ihomey.linkuphome.widget.dashboardview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ihomey.linkuphome.R;

import static com.ihomey.linkuphome.ExtKt.dip2px;


/**
 * Created by anderson on 2016/6/5.
 */
public class DashboardView extends View {


    // Status
    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_NUMCOUNT = "status_numCount";

    private DashboardViewAttr dashboardViewattr;
    private int progressStrokeWidth;//进度弧的宽度
    private String unit = "";//显示单位
    private float startNum;
    private int mTextSize;//文字的大小
    private int mTextColor;//设置文字颜色
    private int mTikeCount;//刻度的个数

    //画笔
    private Paint paintProgress;
    private Paint paintText;
    private Paint paintNum;
    private RectF rectF2;

    private int OFFSET = 30;
    private int START_ARC = 118;
    private int DURING_ARC = 304;

    private Context mContext;
    private int mWidth, mHight;
    float percent;
    float numCount;
    private ColorTemperatureListener mCircleTemperatureListener;

    private long startTime;
    private long endTime;

    private float downX;
    private float downY;

    private float moveX;
    private float moveY;

    private boolean isClockwise;

    private float mCurrentRadian;
    private float mPreRadian;

    public DashboardView(Context context) {
        this(context, null);
        init(context);
    }

    public DashboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public DashboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dashboardViewattr = new DashboardViewAttr(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
        initAttr();
        initPaint();

    }

    private void initPaint() {
        //初始化画笔

        paintProgress = new Paint();
        paintProgress.setAntiAlias(true);
        paintProgress.setStrokeWidth(progressStrokeWidth);
        paintProgress.setStyle(Paint.Style.STROKE);
        paintProgress.setDither(true);

        paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setColor(mTextColor);
        paintText.setStyle(Paint.Style.FILL_AND_STROKE);//实心画笔
        paintText.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "VARSITY_REGULAR.TTF"));
        paintText.setDither(true);
        paintNum = new Paint();
        paintNum.setAntiAlias(true);
        paintNum.setColor(Color.BLACK);
        paintNum.setTypeface(Typeface.DEFAULT_BOLD);
        paintNum.setFakeBoldText(true);
        paintNum.setStrokeWidth(dip2px(mContext, 2.1f));
        paintNum.setStyle(Paint.Style.FILL);
        paintNum.setDither(true);
    }


    private void initShader() {
        updateOval();
    }

    private void initAttr() {
        mTikeCount = 70;
        mTextSize = dashboardViewattr.getmTextSize();
        mTextColor = dashboardViewattr.getTextColor();
        progressStrokeWidth = dashboardViewattr.getProgressStrokeWidth();
        unit = dashboardViewattr.getUnit();
        startNum = dashboardViewattr.getStartNumber();
        if (dashboardViewattr.getPadding() == 0) {
            OFFSET = progressStrokeWidth + 10;
        } else {
            OFFSET = dashboardViewattr.getPadding();
        }

        // 开启硬件加速
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHight = getHeight();
        initShader();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int realWidth = startMeasure(widthMeasureSpec);
        int realHeight = startMeasure(heightMeasureSpec);

        setMeasuredDimension(realWidth, realHeight);
    }


    private int startMeasure(int msSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(msSpec);
        int size = MeasureSpec.getSize(msSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = dip2px(mContext, 200);
        }
        return result;
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState());
        bundle.putFloat(STATUS_NUMCOUNT, numCount);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            numCount = bundle.getFloat(STATUS_NUMCOUNT);
            return;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.percent = numCount / 304;
        //绘制指针
        drawerPointer(canvas, percent);
        canvas.translate(mWidth / 2, mHight / 2);//移动坐标原点到中心

        //绘制进度弧
        drawProgress(canvas, percent);

        //绘制刻度
        drawerNum(canvas);
        //绘制矩形和文字
        drawText(canvas);

    }


    private void drawText(Canvas canvas) {
        if (TextUtils.isEmpty(unit)) return;
        float length;
        paintText.setTextSize(mTextSize);
        int temperatureValue = (int) startNum + ((int) (numCount / 4.34)) * 50;
        String temperatureValueStr = temperatureValue + unit;
        length = paintText.measureText(temperatureValueStr);
        canvas.drawText(temperatureValueStr, -length / 2, mHight / 3 + dip2px(mContext, 6), paintText);
    }

    public int getCurrentColorTemperature() {
        return (int) startNum + ((int) (numCount / 4.34) * 50);
    }

    public void setCurrentColorTemperature(int temperature) {
        if (temperature == 0) {
            temperature = 3000;
        }
        mCurrentRadian = (float) Math.toRadians(((temperature - (int) startNum) / 50) * 4.34);
        setPercent((int) (Math.ceil((temperature - startNum) / 50 * 4.34)));
    }

    private void drawerPointer(Canvas canvas, float percent) {
        canvas.save();
        float angel = DURING_ARC * (percent - 0.5f);
        canvas.rotate(angel, mWidth / 2, mHight / 2);//指针与外弧边缘持平
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.color_temperature_icon_cursor);
        canvas.drawBitmap(bitmap, getMeasuredWidth() / 2 - bitmap.getWidth() / 2, getMeasuredHeight() / 2 - bitmap.getHeight() / 2, paintNum);
        canvas.restore();
    }

    private void drawerNum(Canvas canvas) {
        canvas.save(); //记录画布状态
        canvas.rotate(-(180 - START_ARC + 90f), 0, 0);
        int numY = -mHight / 2 + OFFSET + progressStrokeWidth;
        float rAngle = 280 / ((mTikeCount) * 1.0f); //n根线，只需要n-1个区间
        for (int i = 0; i < numCount / 4; i++) {
            canvas.save(); //记录画布状态
            canvas.rotate(rAngle * i, 0, 0);

            canvas.drawLine(0, numY - dip2px(getContext(), 80.6f), 0, numY - dip2px(getContext(), 35.5f), paintNum);//画短刻度线
            canvas.restore();
        }
        canvas.restore();
    }

    private void drawProgress(Canvas canvas, float percent) {
        int temperature = (int) startNum + (int) (numCount / 4.34 * 50);
        if (temperature >= 2700 && temperature < 3000) {
            paintProgress.setColor(Color.parseColor("#AAFBC86E"));
        } else if (temperature >= 3000 && temperature < 3500) {
            paintProgress.setColor(Color.parseColor("#AAFACE81"));
        } else if (temperature >= 3500 && temperature < 4000) {
            paintProgress.setColor(Color.parseColor("#AAF7D296"));
        } else if (temperature >= 4000 && temperature < 4500) {
            paintProgress.setColor(Color.parseColor("#AAFCD9B1"));
        } else if (temperature >= 4500 && temperature < 5000) {
            paintProgress.setColor(Color.parseColor("#AAE8CFB5"));
        } else if (temperature >= 5000 && temperature < 5500) {
            paintProgress.setColor(Color.parseColor("#AAF7EAD6"));
        } else if (temperature >= 5500 && temperature < 6000) {
            paintProgress.setColor(Color.parseColor("#AAFDF6E6"));
        } else if (temperature >= 6000) {
            paintProgress.setColor(Color.parseColor("#AAFFFFFF"));
        }

        if (percent > 1.0f) {
            percent = 1.0f; //限制进度条在弹性的作用下不会超出
        }
        if (!(percent <= 0.0f)) {
            canvas.drawArc(rectF2, START_ARC, percent * DURING_ARC, false, paintProgress);
        }
    }

    private void updateOval() {
        rectF2 = new RectF((-mWidth / 2) + OFFSET + getPaddingLeft() - dip2px(mContext, 15.5f), getPaddingTop() - (mHight / 2) + OFFSET - dip2px(mContext, 15.5f),
                (mWidth / 2) - getPaddingRight() - OFFSET + dip2px(mContext, 15.5f),
                (mWidth / 2) - getPaddingBottom() - OFFSET + dip2px(mContext, 15.5f));
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();//float DownX
                downY = event.getY();//float DownY
                moveX = 0;
                moveY = 0;
                mPreRadian = getRadian(event.getX(), event.getY());
                startTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX += Math.abs(event.getX() - downX);//X轴距离
                moveY += Math.abs(event.getY() - downY);//y轴距离
                downX = event.getX();
                downX = event.getY();
                float temp = getRadian(event.getX(), event.getY());
                mCurrentRadian += (temp - mPreRadian);
                if (mCurrentRadian > 2 * Math.PI) {
                    mCurrentRadian = mCurrentRadian - (float) (2 * Math.PI);
                } else if (mCurrentRadian < 0) {
                    mCurrentRadian = mCurrentRadian + (float) (2 * Math.PI);
                }
                isClockwise = new Float(Math.toDegrees(temp)).compareTo(new Float(Math.toDegrees(mPreRadian))) >= 0;
                mPreRadian = temp;
                if (isClockwise && (int) Math.toDegrees(mCurrentRadian) >= 305) {
                    mCurrentRadian = (float) Math.toRadians(305f);
                } else if (!isClockwise && (int) Math.toDegrees(mCurrentRadian) >= 305) {
                    mCurrentRadian = (float) Math.toRadians(0);
                }
                setPercent((int) Math.toDegrees(mCurrentRadian));
                break;
            case MotionEvent.ACTION_UP:
                if (mCircleTemperatureListener != null) {
                    int temperatureValue = (int) startNum + ((int) (numCount / 4.34)) * 50;
                    mCircleTemperatureListener.onColorTemperatureValueChanged(temperatureValue);
                }
                break;
        }
        return true;
    }


    // Use tri to cal radian
    private float getRadian(float x, float y) {
        float mCx = getWidth() / 2;
        float mCy = getHeight() / 2;
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


    /**
     * 设置百分比
     *
     * @param numCount
     */
    public void setPercent(int numCount) {
        setAnimator(numCount);
    }

    private void setAnimator(final float numCount) {
        DashboardView.this.numCount = numCount;
        invalidate();
    }


    /**
     * 设置字体颜色
     *
     * @param mTextColor
     */
    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    /**
     * 设置单位
     *
     * @param unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * set timer listener
     *
     * @param mCircleTemperatureListener
     */
    public void setColorTemperatureListener(ColorTemperatureListener mCircleTemperatureListener) {
        this.mCircleTemperatureListener = mCircleTemperatureListener;
    }


    public interface ColorTemperatureListener {
        /**
         * launch timer set value changed event
         *
         * @param temperature
         */
        void onColorTemperatureValueChanged(int temperature);
    }


}
