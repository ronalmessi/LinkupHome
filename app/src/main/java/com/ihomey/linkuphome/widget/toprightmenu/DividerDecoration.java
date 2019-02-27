package com.ihomey.linkuphome.widget.toprightmenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.ihomey.linkuphome.ExtKt.dip2px;


/**
 * Created by Administrator on 2016/12/29.
 */

public class DividerDecoration extends RecyclerView.ItemDecoration {
    private boolean hideBottom = false;

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private Paint mPaint;

    private int mDividerHeight = 2;

    private int mOrientation;

    public DividerDecoration(Context context, int orientation, int dividerHeight, int dividerColor, boolean hideBottom) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(context.getResources().getColor(dividerColor));
        mPaint.setStyle(Paint.Style.FILL);
        this.hideBottom = hideBottom;

        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }

    }


    public void drawVertical(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        int startIndex = 0;
        int endIndex = parent.getChildCount();
        if (hideBottom) {
            endIndex = endIndex - 1;
        }
        for (int i = startIndex; i < endIndex; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView v = new RecyclerView(parent.getContext());
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDividerHeight;
//            mDivider.setBounds(left , top, right , bottom);
//            mDivider.draw(c);
            canvas.drawRect(left+ dip2px(child.getContext(),12), top, right- dip2px(child.getContext(),12), bottom, mPaint);
        }
    }

    public void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDividerHeight;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
    }

    //获取分割线尺寸
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, mDividerHeight);
    }
}
