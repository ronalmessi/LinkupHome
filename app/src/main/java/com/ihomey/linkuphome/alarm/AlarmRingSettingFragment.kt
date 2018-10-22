package com.ihomey.linkuphome.alarm

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.AlarmSettingAdapter
import com.ihomey.linkuphome.data.vo.Alarm
import com.ihomey.linkuphome.databinding.FragmentAlarmSettingRingBinding
import com.ihomey.linkuphome.main.BleLampFragment
import com.ihomey.linkuphome.ringTypeNames
import com.ihomey.linkuphome.widget.DividerItemDecoration
import com.jackandphantom.blurimage.BlurImage


class AlarmRingSettingFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener {


    private lateinit var mViewDataBinding: FragmentAlarmSettingRingBinding
    private var alarmViewModel: AlarmViewModel? = null

    private var alarmSettingAdapter: AlarmSettingAdapter = AlarmSettingAdapter(true, R.layout.item_alarm_setting, ringTypeNames)

    fun newInstance(): AlarmRingSettingFragment {
        return AlarmRingSettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm_setting_ring, container, false)
        return mViewDataBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bitmap = BlurImage.with(context).load(R.drawable.bg_alarm_setting).intensity(20f).imageBlur
        view?.background = BitmapDrawable(resources, bitmap)

        (mViewDataBinding.rcvRingList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mViewDataBinding.rcvRingList.layoutManager = LinearLayoutManager(context)
        mViewDataBinding.rcvRingList.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL, 2, ContextCompat.getColor(context, android.R.color.white), false))
        mViewDataBinding.rcvRingList.adapter = alarmSettingAdapter

        alarmSettingAdapter.onItemClickListener = this

        mViewDataBinding.toolbarBack.setOnClickListener { activity.onBackPressed() }
    }

    override fun onStart() {
        super.onStart()
        val bleLampFragment = parentFragment.parentFragment as BleLampFragment
        bleLampFragment.hideBottomNavigationView()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        alarmSettingAdapter.setItemSelected(position, !alarmSettingAdapter.isItemSelected(position))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        alarmViewModel = ViewModelProviders.of(parentFragment).get(AlarmViewModel::class.java)
        alarmViewModel?.getAlarm()?.observe(this, Observer<Alarm> {
            if (it?.ringType!! > 0) alarmSettingAdapter.setItemSelected(it.ringType - 1, true)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        for ((k, v) in alarmSettingAdapter.selectedItems) {
            alarmViewModel?.setRingType(k + 1)
        }
    }

}