package com.ihomey.linkuphome.alarm

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DayOfWeekListAdapter
import com.ihomey.linkuphome.data.entity.Alarm
import com.ihomey.linkuphome.dip2px
import com.ihomey.linkuphome.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.alarm_day_list_fragment.*

class AlarmDayListFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener {


    private var dayOfWeekListAdapter: DayOfWeekListAdapter = DayOfWeekListAdapter(false, R.layout.item_day_of_week_alarm)

    private lateinit var mViewModel: AlarmViewModel

    private var mAlarm: Alarm? = null

    fun newInstance(): AlarmDayListFragment {
        return AlarmDayListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.alarm_day_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (rcv_daysOfWeek.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        rcv_daysOfWeek.layoutManager = LinearLayoutManager(context)
        rcv_daysOfWeek.addItemDecoration(DividerItemDecoration(LinearLayoutManager.HORIZONTAL, 0, context?.dip2px(0.5f)!!, Color.WHITE, false))
        rcv_daysOfWeek.adapter = dayOfWeekListAdapter
        dayOfWeekListAdapter.onItemClickListener = this
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack()}
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(AlarmViewModel::class.java)
        mViewModel.mAlarm.observe(viewLifecycleOwner, Observer<Alarm> {
            mAlarm=it
            updateViews(it)
        })
    }

    private fun updateViews(alarm: Alarm?) {
        alarm?.let {
            val dayOfWeekHexStr=Integer.toBinaryString(it.dayOfWeek)
            for (i in 0 until dayOfWeekHexStr.length) {
                if (TextUtils.equals("1", dayOfWeekHexStr[i].toString())) {
                    dayOfWeekListAdapter.setItemSelected(7-dayOfWeekHexStr.length+i,true)
                }
            }
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        dayOfWeekListAdapter.setItemSelected(position, !dayOfWeekListAdapter.isItemSelected(position))
        mAlarm?.dayOfWeek=dayOfWeekListAdapter.getDayOfWeekValue()
    }

}