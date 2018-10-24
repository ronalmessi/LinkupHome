package com.ihomey.linkuphome.bed

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.control.BaseControlFragment
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.databinding.FragmentControlBedSettingBinding
import com.ihomey.linkuphome.main.BleLampFragment
import net.cachapa.expandablelayout.ExpandableLayout

class BedControlSettingFragment : BaseControlFragment() {

    private lateinit var mViewDataBinding: FragmentControlBedSettingBinding

    fun newInstance(): BedControlSettingFragment {
        return BedControlSettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initController(5)
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_control_bed_setting, container, false)
        mViewDataBinding.handlers = ToolBarEventHandler()
        mViewDataBinding.rlControlSettingCycling.setOnClickListener {
            if (mViewDataBinding.elControlSettingCycling.isExpanded) {
                mViewDataBinding.elControlSettingCycling.collapse()
            } else {
                mViewDataBinding.elControlSettingCycling.expand()
            }
        }
        val arrowDrawable = ContextCompat.getDrawable(context, R.drawable.ic_bed_control_setting_arrow)
        val tintArrowDrawable = DrawableCompat.wrap(arrowDrawable!!).mutate()
        DrawableCompat.setTint(tintArrowDrawable, ContextCompat.getColor(context, R.color.control_tab_item_selected_color))
        mViewDataBinding.elControlSettingCycling.setOnExpansionUpdateListener { _, state ->
            if (state == ExpandableLayout.State.EXPANDED || state == ExpandableLayout.State.EXPANDING) {
                mViewDataBinding.ivControlSettingArrow.background = tintArrowDrawable
            } else if (state == ExpandableLayout.State.COLLAPSED || state == ExpandableLayout.State.COLLAPSING) {
                mViewDataBinding.ivControlSettingArrow.setBackgroundResource(R.drawable.ic_bed_control_setting_arrow)
            }
        }
        return mViewDataBinding.root
    }

    override fun updateViewData(controlDevice: ControlDevice?) {
        if (controlDevice != null) {
            mViewDataBinding.control = controlDevice
            mControlDevice = controlDevice
//            if (mControlDevice != null) {
//                if (mControlDevice!!.state.changeMode != -1) {
//                    mViewDataBinding.sstgCyclingSpeed.setCheckedAt(mControlDevice!!.state.changeMode, true)
//                }
//            }
            mViewDataBinding.rlControlSettingSyncTime.setOnClickListener(this)
            mViewDataBinding.rlControlSettingSceneMode.setOnClickListener(this)
            mViewDataBinding.rlControlSettingAlarm.setOnClickListener(this)
            mViewDataBinding.sstgCyclingSpeed.setOnCheckedChangeListener(this)
        } else {
            mViewDataBinding.control = null
            mControlDevice = null
        }
    }

    override fun onStart() {
        super.onStart()
        mViewDataBinding.elControlSettingCycling.collapse()
        val bleLampFragment = parentFragment.parentFragment as BleLampFragment
        bleLampFragment.hideBottomNavigationView()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mViewDataBinding.rlControlSettingSceneMode.setOnClickListener(null)
        mViewDataBinding.rlControlSettingAlarm.setOnClickListener(null)
        mViewDataBinding.sstgCyclingSpeed.setOnCheckedChangeListener(null)
    }
}