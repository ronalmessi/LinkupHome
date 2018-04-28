package com.ihomey.linkuphome.widget;

import android.content.Context;
import android.util.AttributeSet;

import java.util.List;

public class SingleSelectToggleGroup extends ToggleButtonGroup {
    private static final String LOG_TAG = SingleSelectToggleGroup.class.getSimpleName();

    public SingleSelectToggleGroup(Context context) {
        this(context, null);
    }

    public SingleSelectToggleGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onButtonClick(int position) {
        uncheckAll();
        mButtons.get(position).setChecked(true, isAnimationEnabled());
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChange(position, true);
        }
        if (mOnCheckedPositionChangeListener != null) {
            mOnCheckedPositionChangeListener.onCheckedPositionChange(getCheckedPositions());
        }
    }

    @Override
    public void setButtons(List<String> text) {
        super.setButtons(text);
        if (text.size() > 0) {
            mButtons.get(1).setChecked(true);
        }
    }

    @Override
    public void setCheckedAt(int position, boolean isChecked) {
        if (isChecked) {
            onButtonClick(position);
        } else {
            uncheckAll();
            super.setCheckedAt(position, isChecked);
        }
    }
}
