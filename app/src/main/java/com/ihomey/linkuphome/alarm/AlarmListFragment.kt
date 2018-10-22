package com.ihomey.linkuphome.alarm

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.AlarmListAdapter
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.databinding.FragmentAlarmListBinding
import com.ihomey.linkuphome.dip2px
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.main.BleLampFragment
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.widget.VerticalSpaceItemDecoration
import com.yanzhenjie.loading.Utils
import com.yanzhenjie.recyclerview.swipe.*


/**
 * Created by dongcaizheng on 2017/12/25.
 */
class AlarmListFragment : BaseFragment(), SwipeItemClickListener, SwipeMenuItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    private lateinit var mViewDataBinding: FragmentAlarmListBinding

    private var mViewModel: MainViewModel? = null
    private var alarmViewModel: AlarmViewModel? = null

    private lateinit var adapter: AlarmListAdapter

    fun newInstance(): AlarmListFragment {
        return AlarmListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm_list, container, false)
        return mViewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        alarmViewModel = ViewModelProviders.of(parentFragment).get(AlarmViewModel::class.java)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = AlarmListAdapter(arrayListOf())
        adapter.onItemChildClickListener = this
        mViewDataBinding.rcvAlarmList.layoutManager = LinearLayoutManager(context)
        mViewDataBinding.rcvAlarmList.addItemDecoration(VerticalSpaceItemDecoration(Utils.dip2px(context, 6f).toInt()))
        mViewDataBinding.rcvAlarmList.adapter = adapter
        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            if (swipeRightMenu.viewType == 1) {
                val width = context.dip2px(96f)
                val height = ViewGroup.LayoutParams.MATCH_PARENT
                val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width.toInt()).setHeight(height).setText(R.string.delete).setTextColor(Color.WHITE).setTextSize(14)
                swipeRightMenu.addMenuItem(deleteItem)
            }
        }
        mViewDataBinding.rcvAlarmList.setSwipeItemClickListener(this)
        mViewDataBinding.rcvAlarmList.setSwipeMenuCreator(swipeMenuCreator)
        mViewDataBinding.rcvAlarmList.setSwipeMenuItemClickListener(this)

        mViewDataBinding.toolbarBack.setOnClickListener { activity.onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val lastUsedDeviceId by PreferenceHelper("lastUsedDeviceId_5", -1)
        mViewModel?.getAlarmResults(lastUsedDeviceId)?.observe(this, Observer<Resource<List<Alarm>>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                val newData = arrayListOf<Alarm>()
                newData.addAll(it.data)
                if (it.data.size < 2) newData.add(Alarm(-1, 0, 0, 0, 0, 0, 0, 0))
                adapter.setNewData(newData)
            }
        })
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge?) {
        val alarm = adapter.getItem(menuBridge?.adapterPosition!!) as Alarm
        alarmViewModel?.deleteAlarm(alarm)
        loadData()
        menuBridge.closeMenu()
    }

    override fun onItemClick(itemView: View?, position: Int) {
        if (parentFragment is IFragmentStackHolder) {
            val alarm = adapter.getItem(position) as Alarm
            alarmViewModel?.setAlarm(alarm)
            val newFrag = AlarmSettingFragment().newInstance()
            val fsh = parentFragment as IFragmentStackHolder
            fsh.replaceFragment(R.id.inner_frag_control_container, newFrag)
        }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {

    }

    override fun onStart() {
        super.onStart()
        val bleLampFragment = parentFragment.parentFragment as BleLampFragment
        bleLampFragment.showBottomNavigationView()
    }

}