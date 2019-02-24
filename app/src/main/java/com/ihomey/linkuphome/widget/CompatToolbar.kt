package com.ihomey.linkuphome.widget

import android.content.Context
import android.os.Build
import androidx.appcompat.widget.Toolbar
import android.util.AttributeSet
import android.util.Log
import com.ihomey.linkuphome.dip2px

class CompatToolbar : Toolbar {

    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup()
    }

    private fun setup() {
        var compatPaddingTop = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            compatPaddingTop = getStatusBarHeight()
        }
        this.setPadding(context.dip2px(10f), paddingTop + compatPaddingTop + context.dip2px(12f), context.dip2px(12f), context.dip2px(12f))
    }

    private fun getStatusBarHeight(): Int {
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

}