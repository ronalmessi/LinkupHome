package com.ihomey.linkuphome.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.ihomey.linkuphome.R;

/**
 * 文件说明:自定义温度计
 * 温度计分为三层，每层包含一个底部circle和上部rect。
 * 第一层：温度计的外部边框
 * 第二层：温度计的背景
 * 第三层：温度计的真实刻度值
 * 温度计的左边还包含一个刻度尺。
 * Created by xcz on 16/11/17.
 */

public class Thermometer extends View {
    //温度计外部边框中的 circle半径和rect半径（即宽度的一半）
    private float outerCircleRadius;
//    private float outerRectRadius;
//    private Paint outerPaint;
    //温度计背景中的 circle半径和rect半径（即宽度的一半）
    private float middleCircleRadius;
    private float middleRectRadius;
    private Paint middlePaint;
    private Paint mTextPaint;
    //温度计真实刻度值中的 circle半径和rect半径（即宽度的一半）
    private float innerCircleRadius;
    private float innerRectRadius;
    private Paint innerPaint;

    private Paint degreePaint;

    private static final float DEGREE_WIDTH = 30;//温度计左边刻度的宽度（x轴的宽度）
    private static final float MAX_TEMP = 80;//最大温度值
    private static final float MIN_TEMP = -40;//最小温度值
    private static final float RANGE_TEMP = 120;//温度区间 50-(-30)=80
    private float currentTemp = -40;//当前温度

    /**
     * 设置温度
     *
     * @param currentTemp 当前温度
     */
    public void setCurrentTemp(float currentTemp) {
        if (currentTemp > MAX_TEMP) {
            this.currentTemp = MAX_TEMP;
        } else if (currentTemp < MIN_TEMP) {
            this.currentTemp = MIN_TEMP;
        } else {
            this.currentTemp = currentTemp;
        }
        invalidate();
    }

    public Thermometer(Context context) {
        super(context);
        init(context, null);
    }

    public Thermometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Thermometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    /**
     * 初始化
     *
     * @param context Context参数
     * @param attrs   AttributeSet参数
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Thermometer);
        outerCircleRadius = typedArray.getDimension(R.styleable.Thermometer_radius, 20f);
        int middleColor = typedArray.getColor(R.styleable.Thermometer_middleColor, Color.WHITE);
        int innerColor = typedArray.getColor(R.styleable.Thermometer_innerColor, Color.RED);
        typedArray.recycle();

        middleCircleRadius = outerCircleRadius - 10;//middle和outer之间的间隙
        middleRectRadius = outerCircleRadius / 2 - 10;
        middlePaint = new Paint();
        middlePaint.setColor(middleColor);
        middlePaint.setStyle(Paint.Style.FILL);

        innerCircleRadius = middleCircleRadius/2+5;//middle和inner之间的间隙
        innerRectRadius = middleRectRadius/3;
        innerPaint = new Paint();
        innerPaint.setColor(innerColor);
        innerPaint.setStyle(Paint.Style.FILL);

        degreePaint = new Paint();
        degreePaint.setStrokeWidth(5);//温度计刻度尺的宽度
        degreePaint.setColor(middleColor);
        degreePaint.setStyle(Paint.Style.FILL);


        mTextPaint = new Paint();
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setTextSize(40);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //View的长宽
        int height = getHeight();
        int width = getWidth();

        //底部圆形的圆心位置
        int CircleCenterX = width / 2;
        float CircleCenterY = height - outerCircleRadius;

        float outerStartY = 0;  //outer的起始Y坐标
        float middleStartY = outerStartY + 5;//middle的起始Y坐标

        float innerEffectStartY = middleStartY + middleRectRadius;//有效的inner Rect的起始Y
        float innerEffectEndY = CircleCenterY - middleCircleRadius;//有效的inner Rect的结束Y
        float innerRectHeight = innerEffectEndY - innerEffectStartY;//inner Rect的有效长度，即温度的有效范围
        float innerStartY = innerEffectEndY - (currentTemp -MIN_TEMP) / RANGE_TEMP * innerRectHeight;//inner的起始Y坐标
        //画中间层的圆头矩形
        RectF middleRect = new RectF();
        middleRect.left = CircleCenterX - middleRectRadius;
        middleRect.top = middleStartY;
        middleRect.right=CircleCenterX + middleRectRadius;
        middleRect.bottom = CircleCenterY;
        canvas.drawRoundRect(middleRect, middleRectRadius, middleRectRadius, middlePaint);
        //画中间层的圆
        canvas.drawCircle(CircleCenterX, CircleCenterY, middleCircleRadius, middlePaint);


        Paint innerBgPaint = new Paint();
        innerBgPaint.setColor(Color.parseColor("#CBCBCB"));
        innerBgPaint.setStyle(Paint.Style.FILL);

        //画内部的背景圆头矩形
        canvas.drawRect(CircleCenterX - innerRectRadius, innerEffectStartY, CircleCenterX + innerRectRadius, innerEffectEndY, innerBgPaint);

        //画内部的圆头矩形
        canvas.drawRect(CircleCenterX - innerRectRadius, innerStartY, CircleCenterX + innerRectRadius, CircleCenterY, innerPaint);
        //画内部的圆
        canvas.drawCircle(CircleCenterX, CircleCenterY, innerCircleRadius, innerPaint);

        //画刻度
        float tmp = innerEffectStartY;//innerEffectStartY 刻度的起始位置
        while (tmp <= innerEffectEndY) {//innerEffectEndY 刻度的终止位置
            float rangY=(innerEffectEndY-innerEffectStartY)/12;
            if((tmp-innerEffectStartY)/rangY%2==0){
                if((int)(tmp-innerEffectStartY)/rangY/2==4){
                    Rect bounds = new Rect();
                    mTextPaint.getTextBounds("0", 0, "0".length(), bounds);
                    canvas.drawText("0", CircleCenterX - innerCircleRadius-DEGREE_WIDTH*5/3-bounds.width()*2, tmp+bounds.height()/2, mTextPaint);
                }
                canvas.drawLine(CircleCenterX - innerCircleRadius-10-DEGREE_WIDTH*5/3, tmp, CircleCenterX - innerCircleRadius-10, tmp, degreePaint);
            }else{
                canvas.drawLine(CircleCenterX - innerCircleRadius-10-DEGREE_WIDTH, tmp, CircleCenterX - innerCircleRadius-10, tmp, degreePaint);
            }
            tmp += (innerEffectEndY - innerEffectStartY) / 12;
        }
    }
}
