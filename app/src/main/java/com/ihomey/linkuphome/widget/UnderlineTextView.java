package com.ihomey.linkuphome.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;

public class UnderlineTextView extends AppCompatTextView {

    private Paint mLinePaint;

    private int mSpaceHeight;

    public UnderlineTextView(Context context) {
        super(context);
        init();
    }

    public UnderlineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UnderlineTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(getTextSize() / 10);
        mLinePaint.setColor(getTextColors().getDefaultColor());

        Paint.FontMetricsInt fontMetricsInt = getPaint().getFontMetricsInt();

        mSpaceHeight = Math.abs(fontMetricsInt.bottom - fontMetricsInt.ascent - getLineHeight()) - (
                fontMetricsInt.bottom
                        - fontMetricsInt.descent);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TextUtils.isEmpty(getText())) {
            return;
        }

        int lineCount = getLineCount();
        Layout layout = getLayout();

        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();

        for (int i = 0; i < lineCount; i++) {
            float currentXStart = layout.getLineLeft(i) + paddingLeft;
            float currentXEnd = layout.getLineRight(i) + paddingLeft;
            int currentY = layout.getLineBottom(i) + paddingTop - mSpaceHeight;
            canvas.drawLine(currentXStart, currentY, currentXEnd, currentY, mLinePaint);
        }
    }

    public void setLineWeight(float value) {
        mLinePaint.setStrokeWidth(value);
    }

    public void setLineColor(int color) {
        mLinePaint.setColor(color);
    }
}
