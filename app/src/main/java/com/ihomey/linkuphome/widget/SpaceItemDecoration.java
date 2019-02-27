package com.ihomey.linkuphome.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2016/12/28.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int leftSpace;
    private int rightSpace;
    private int topSpace;
    private int bottomtSpace;

    public SpaceItemDecoration(int leftSpace,int rightSpace,int topSpace,int bottomtSpace) {
        this.leftSpace=leftSpace;
        this.rightSpace=rightSpace;
        this.topSpace=topSpace;
        this.bottomtSpace=bottomtSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right=rightSpace;
        outRect.bottom=bottomtSpace;
        outRect.left=leftSpace;
        outRect.top=topSpace;
    }
}
