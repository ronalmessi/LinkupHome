package com.ihomey.linkuphome.widget;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

/**
 * Created by qikai on 16/1/28.
 */
public class DragShadowBuilder extends View.DragShadowBuilder {

    final Point touchPoint = new Point();

    //生成影像
    public DragShadowBuilder(View view, Point touchPoint) {
        super(view);
        this.touchPoint.set(touchPoint.x, touchPoint.y);
    }

    //设置影像参数
    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
        int width = getView().getWidth();
        int height = getView().getHeight();
        shadowSize.set(width, height);
        shadowTouchPoint.set(touchPoint.x, touchPoint.y);
    }

    //画影像
    @Override
    public void onDrawShadow(Canvas canvas) {
        super.onDrawShadow(canvas);
    }
}
