package com.ihomey.linkuphome.device1;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2016/12/28.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {


    private int leftSpace;
    private int topSpace;
    private int rightSpace;
    private int bottomSpace;

    public SpaceItemDecoration(int leftSpace,int topSpace,int rightSpace,int bottomSpace) {
        this.leftSpace=leftSpace;
        this.topSpace=topSpace;
        this.rightSpace=rightSpace;
        this.bottomSpace=bottomSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left=leftSpace;
        outRect.right=rightSpace;
        outRect.bottom=bottomSpace;
        outRect.top=topSpace;
    }
}
