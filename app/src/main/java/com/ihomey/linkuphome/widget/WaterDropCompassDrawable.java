package com.ihomey.linkuphome.widget;

import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import com.ihomey.linkuphome.R;

public class WaterDropCompassDrawable extends android.graphics.drawable.Drawable {
    /**
     * 进度范围为0-360
     */
    public static final String PROGRESS_PROPERTY = "progress";
    /**
     * 画笔
     */
    private final Paint paint = new Paint();
    /**
     * 范围为210 - 0
     */
    private float progress = 210;
    /**
     * 动画时间
     */
    private static final int DURATION = 2000;

    /**
     * 指针的颜色
     */
    private int pointerColor;
    /**

    /**
     * 中间的文字，只有两个汉子
     */
    private String text = "health";

    /**
     * 供给属性动画调用的方法
     *
     * @param progress 进度值从210开始0结束，此区间内随意取值
     */
    public void setProgress(float progress) {
        this.progress = progress;
        invalidateSelf();
    }

    public WaterDropCompassDrawable() {

        pointerColor = Color.parseColor("#FF5ADC96");

    }

    @Override
    public void draw(Canvas canvas) {
        paint.setAntiAlias(true);
        /**
         * 初始化圆的位置
         */
        final Rect bounds = getBounds();
        //计算不同的空间的size取最大，最好宽高一样
        float size = Math.min(bounds.height(), bounds.width());


        Log.d("aa","--"+bounds.height()+"--"+bounds.width());

        PointF center = new PointF(bounds.centerX(), size / (480f / 288f));


        /**
         * 画指针不变的圆
         */
        //中心圆的半径为240直径与圆半径的比例为5.2
        paint.setColor(pointerColor);
        paint.setStyle(Paint.Style.FILL);
        float innerRadius = size / 4f;
        canvas.drawCircle(center.x, center.y, innerRadius, paint);

        /**
         * 变动的针尖
         */
        Path path = new Path();
        path.moveTo(center.x, center.y);
        double degrees1 = (1 / 4f) * Math.PI;//1/2内角度，此处决定了针尖的长度，4f处越大，针越小
        double radians = (Math.PI / 180f) * progress;
        float aX = (float) Math.cos(degrees1 + radians) * innerRadius;
        float aY = (float) Math.sin(degrees1 + radians) * innerRadius;
        path.lineTo(center.x + aX, center.y - aY);
        float bX = (float) (Math.cos(radians) * innerRadius / Math.cos(degrees1));
        float bY = (float) (Math.sin(radians) * innerRadius / Math.cos(degrees1));
        path.lineTo(center.x + bX, center.y - bY);
        float cX = (float) Math.cos(radians - degrees1) * innerRadius;
        float cY = (float) Math.sin(radians - degrees1) * innerRadius;
        path.lineTo(center.x + cX, center.y - cY);
        canvas.drawPath(path, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

//    /**
//     * 输入颜色的index和要显示的文字
//     *
//     * @param currentColorIndex 颜色的索引
//     * @param text              中间显示的文字
//     */
//    public void setCurrentColor(int currentColorIndex, String text) {
//        this.pointerColor = colors[currentColorIndex];
//        this.text = text;
//        ObjectAnimator progressAni = ObjectAnimator.ofFloat(this, WaterDropCompassDrawable.PROGRESS_PROPERTY, 210, 210 - (currentColorIndex + 0.5f) * averageAngle);
//        progressAni.setDuration(DURATION);
//        progressAni.start();
//    }
}