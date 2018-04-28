package com.ihomey.linkuphome.widget.toprightmenu;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Created by lgp on 2015/3/24.
 */
public class BubbleDrawable extends Drawable {
    private RectF mRect;
    private Path mPath = new Path();
    private BitmapShader mBitmapShader;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mArrowWidth;
    private float mAngle;
    private float mArrowHeight;
    private float mArrowPosition;
    private int bubbleColor;
    private Bitmap bubbleBitmap;
    private BubbleType bubbleType;

    private BubbleDrawable(Builder builder) {
        this.mRect = builder.mRect;
        this.mAngle = builder.mAngle;
        this.mArrowHeight = builder.mArrowHeight;
        this.mArrowWidth = builder.mArrowWidth;
        this.mArrowPosition = builder.mArrowPosition;
        this.bubbleColor = builder.bubbleColor;
        this.bubbleBitmap = builder.bubbleBitmap;
        this.bubbleType = builder.bubbleType;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
    }

    @Override
    public void draw(Canvas canvas) {
        setUp(canvas);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }


    private void setUp(Canvas canvas) {
        switch (bubbleType) {
            case COLOR:
                mPaint.setColor(bubbleColor);
                break;
            case BITMAP:
                if (bubbleBitmap == null)
                    return;
                if (mBitmapShader == null) {
                    mBitmapShader = new BitmapShader(bubbleBitmap,
                            Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                }
                mPaint.setShader(mBitmapShader);
                setUpShaderMatrix();
                break;
        }
        setUpTopPath(mRect,mPath);
        canvas.drawPath(mPath, mPaint);
    }


    private void setUpTopPath(RectF rect, Path path) {
        path.moveTo(rect.left + rect.right - rect.left - mArrowPosition - mArrowWidth, rect.top + mArrowHeight);
        path.lineTo(rect.right - mArrowPosition - mArrowWidth, rect.top + mArrowHeight);
        path.lineTo(rect.right - mArrowPosition - mArrowWidth / 2, rect.top);
        path.lineTo(rect.right - mArrowPosition, rect.top + mArrowHeight);

        path.arcTo(new RectF(rect.right - mAngle,
                rect.top + mArrowHeight, rect.right, mAngle + rect.top + mArrowHeight), 270, 90);
        path.arcTo(new RectF(rect.right - mAngle, rect.bottom - mAngle,
                rect.right, rect.bottom), 0, 90);
        path.arcTo(new RectF(rect.left, rect.bottom - mAngle,
                mAngle + rect.left, rect.bottom), 90, 90);
        path.arcTo(new RectF(rect.left, rect.top + mArrowHeight, mAngle
                + rect.left, mAngle + rect.top + mArrowHeight), 180, 90);
        path.close();
    }

    private void setUpShaderMatrix() {
        float scale;
        Matrix mShaderMatrix = new Matrix();
        mShaderMatrix.set(null);
        int mBitmapWidth = bubbleBitmap.getWidth();
        int mBitmapHeight = bubbleBitmap.getHeight();
        float scaleX = getIntrinsicWidth() / (float) mBitmapWidth;
        float scaleY = getIntrinsicHeight() / (float) mBitmapHeight;
        scale = Math.min(scaleX, scaleY);
        mShaderMatrix.postScale(scale, scale);
        mShaderMatrix.postTranslate(mRect.left, mRect.top);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) mRect.width();
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) mRect.height();
    }

    public static class Builder {
        public static float DEFAULT_ARROW_WITH = 25;
        public static float DEFAULT_ARROW_HEIGHT = 25;
        public static float DEFAULT_ANGLE = 20;
        public static float DEFAULT_ARROW_POSITION = 50;
        public static int DEFAULT_BUBBLE_COLOR = Color.RED;
        private RectF mRect;
        private float mArrowWidth = DEFAULT_ARROW_WITH;
        private float mAngle = DEFAULT_ANGLE;
        private float mArrowHeight = DEFAULT_ARROW_HEIGHT;
        private float mArrowPosition = DEFAULT_ARROW_POSITION;
        private int bubbleColor = DEFAULT_BUBBLE_COLOR;
        private Bitmap bubbleBitmap;
        private BubbleType bubbleType = BubbleType.COLOR;


        public Builder rect(RectF rect) {
            this.mRect = rect;
            return this;
        }

        public Builder arrowWidth(float mArrowWidth) {
            this.mArrowWidth = mArrowWidth;
            return this;
        }

        public Builder angle(float mAngle) {
            this.mAngle = mAngle * 2;
            return this;
        }

        public Builder arrowHeight(float mArrowHeight) {
            this.mArrowHeight = mArrowHeight;
            return this;
        }

        public Builder arrowPosition(float mArrowPosition) {
            this.mArrowPosition = mArrowPosition;
            return this;
        }

        public Builder bubbleColor(int bubbleColor) {
            this.bubbleColor = bubbleColor;
            bubbleType(BubbleType.COLOR);
            return this;
        }

        public Builder bubbleBitmap(Bitmap bubbleBitmap) {
            this.bubbleBitmap = bubbleBitmap;
            bubbleType(BubbleType.BITMAP);
            return this;
        }

        public Builder bubbleType(BubbleType bubbleType) {
            this.bubbleType = bubbleType;
            return this;
        }


        public BubbleDrawable build() {
            if (mRect == null) {
                throw new IllegalArgumentException("BubbleDrawable Rect can not be null");
            }
            return new BubbleDrawable(this);
        }
    }

    public enum BubbleType {
        COLOR,
        BITMAP
    }
}
