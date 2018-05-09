package com.ihomey.linkuphome.control

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.ihomey.library.base.BaseActivity
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.ActivityBaseControlBinding
import com.ihomey.linkuphome.dip2px


/**
 * Created by dongcaizheng on 2017/12/21.
 */
abstract class BaseControlActivity : BaseActivity() {

    protected lateinit var mViewDataBinding: ActivityBaseControlBinding

    abstract fun initData()

    abstract fun initViewPager(viewPager: ViewPager, controlBaseBnv: BottomNavigationView)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_base_control)
        disableShiftMode(mViewDataBinding.controlBaseBnv)
        initViewPager(mViewDataBinding.controlBaseVp, mViewDataBinding.controlBaseBnv)
        mViewDataBinding.controlBaseVp.addOnPageChangeListener(null)
        initData()
        initViewPager(mViewDataBinding.controlBaseVp, mViewDataBinding.controlBaseBnv)
    }

    fun setPageItem(position: Int) {
        mViewDataBinding.controlBaseVp.currentItem = position
    }

    private fun disableShiftMode(view: BottomNavigationView) {
        val menuView = view.getChildAt(0) as BottomNavigationMenuView
        try {
            val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
            shiftingMode.isAccessible = true
            shiftingMode.setBoolean(menuView, false)
            shiftingMode.isAccessible = false
            for (i in 0 until menuView.childCount) {
                val item = menuView.getChildAt(i) as BottomNavigationItemView
                val icon = item.findViewById<ImageView>(android.support.design.R.id.icon)
                val largeLabel = item.findViewById<TextView>(android.support.design.R.id.largeLabel)
                val smallLabel = item.findViewById<TextView>(android.support.design.R.id.smallLabel)
                largeLabel.setSingleLine(false)
                smallLabel.setSingleLine(false)
                largeLabel.setLineSpacing(0f, 0.8f)
                smallLabel.setLineSpacing(0f, 0.8f)
                largeLabel.gravity = Gravity.CENTER_HORIZONTAL
                smallLabel.gravity = Gravity.CENTER_HORIZONTAL
                icon.scaleX = 1.5f
                icon.scaleY = 1.5f
                val baselineLayout = largeLabel.parent as android.support.design.internal.BaselineLayout
                baselineLayout.setPadding(0, 0, 0, 0)
                val layoutParams = baselineLayout.layoutParams as FrameLayout.LayoutParams
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL
                layoutParams.topMargin = dip2px(42f)
                baselineLayout.layoutParams = layoutParams
                item.setShiftingMode(false)
                item.setChecked(item.itemData.isChecked)
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

}