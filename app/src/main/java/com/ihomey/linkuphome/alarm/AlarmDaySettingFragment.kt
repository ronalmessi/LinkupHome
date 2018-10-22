package com.ihomey.linkuphome.alarm

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.AlarmSettingAdapter
import com.ihomey.linkuphome.data.vo.Alarm
import com.ihomey.linkuphome.databinding.FragmentAlarmSettingDayBinding
import com.ihomey.linkuphome.dayOfWeek
import com.ihomey.linkuphome.main.BleLampFragment
import com.ihomey.linkuphome.widget.DividerItemDecoration
import com.jackandphantom.blurimage.BlurImage


class AlarmDaySettingFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener {

    private var sb: StringBuilder? = null
    private lateinit var mViewDataBinding: FragmentAlarmSettingDayBinding
    private var alarmViewModel: AlarmViewModel? = null
    private var alarmSettingAdapter: AlarmSettingAdapter = AlarmSettingAdapter(false, R.layout.item_alarm_setting, dayOfWeek)

    fun newInstance(): AlarmDaySettingFragment {
        return AlarmDaySettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm_setting_day, container, false)
        return mViewDataBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bitmap = BlurImage.with(context).load(R.drawable.bg_alarm_setting).intensity(20f).imageBlur
        view?.background = BitmapDrawable(resources, bitmap)

        (mViewDataBinding.rcvDayList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mViewDataBinding.rcvDayList.layoutManager = LinearLayoutManager(context)
        mViewDataBinding.rcvDayList.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL, 2, ContextCompat.getColor(context, android.R.color.white), false))
        mViewDataBinding.rcvDayList.adapter = alarmSettingAdapter

        alarmSettingAdapter.onItemClickListener = this

        mViewDataBinding.toolbarBack.setOnClickListener { activity.onBackPressed() }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        alarmViewModel = ViewModelProviders.of(parentFragment).get(AlarmViewModel::class.java)
        alarmViewModel?.getAlarm()?.observe(this, Observer<Alarm> {
            val binaryStr = Integer.toBinaryString(it?.dayOfWeek!!)
            sb = StringBuilder()
            for (i in 0 until (8 - binaryStr.length)) {
                sb?.append("0")
            }
            sb?.append(binaryStr)

            updateView(sb?.toString()!!)
        })
    }

    private fun updateView(binaryStr: String) {
        for (i in 1 until binaryStr.length) {
            if (TextUtils.equals("1", binaryStr[i].toString())) {
                if (i == 1) alarmSettingAdapter.selectedItems[0] = 0 else alarmSettingAdapter.selectedItems[8 - i] = 8 - i
            }
        }
        alarmSettingAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        val bleLampFragment = parentFragment.parentFragment as BleLampFragment
        bleLampFragment.hideBottomNavigationView()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        updateAlarms(position, alarmSettingAdapter.isItemSelected(position))
        alarmSettingAdapter.setItemSelected(position, !alarmSettingAdapter.isItemSelected(position))
    }

    private fun updateAlarms(position: Int, itemSelected: Boolean) {
        if (position == 0) {
            sb?.replace(position + 1, position + 2, if (itemSelected) "0" else "1")
        } else {
            sb?.replace(8 - position, 9 - position, if (itemSelected) "0" else "1")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        alarmViewModel?.setDayOfWeek(Integer.parseInt(sb.toString(), 2))
    }
}