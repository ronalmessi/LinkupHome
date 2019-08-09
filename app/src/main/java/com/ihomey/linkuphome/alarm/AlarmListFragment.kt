package com.ihomey.linkuphome.alarm

import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.adapter.AlarmListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.M1Controller
import com.ihomey.linkuphome.data.entity.Alarm
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.spp.BluetoothSPP
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.alarm_list_fragment.*
import kotlinx.android.synthetic.main.alarm_list_fragment.iv_back
import org.spongycastle.util.encoders.Hex
import java.util.*


open class AlarmListFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener, AlarmListAdapter.AlarmStateListener, BaseQuickAdapter.OnItemChildClickListener {

    companion object {
        fun newInstance() = AlarmListFragment()
    }

    protected lateinit var mViewModel: HomeActivityViewModel

    protected lateinit var viewModel: AlarmViewModel

    private lateinit var adapter: AlarmListAdapter

    private var mDevice: Device?=null

    private val controller: M1Controller = M1Controller()

    private var isDelete:Boolean=false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.alarm_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel = ViewModelProviders.of(activity!!).get(AlarmViewModel::class.java)
        mViewModel.getCurrentControlDevice().observe(viewLifecycleOwner, Observer<Device> {
            mDevice=it
            viewModel.setDeviceId(it.id)
        })
        viewModel.mAlarmsResult.observe(viewLifecycleOwner, Observer<Resource<List<Alarm>>> {
            if (it.status == Status.SUCCESS) {
                it.data?.let {
                    adapter.setNewData(it)
                    if(it.isNotEmpty()&&it.size<2) iv_add.visibility=View.VISIBLE else iv_add.visibility=View.GONE
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        iv_add.setOnClickListener {
            if(adapter.itemCount==1){
                adapter.getItem(0)?.let {
                    mDevice?.let {it1->
                        val calendar = Calendar.getInstance()
                        viewModel.setCurrentAlarm(Alarm(if(it.id==1) 2 else 1,it1.id,0,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),1,0,0))
                        Navigation.findNavController(iv_add).navigate(R.id.action_alarmListFragment_to_alarmSettingFragment)
                    }
                }
            }
        }
        adapter = AlarmListAdapter(R.layout.item_alarm_list)
        adapter.setAlarmStateListener(this)
        rcv_alarm_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._18sdp)?.toInt()?.let { SpaceItemDecoration(0, 0, 0, it) }?.let { rcv_alarm_list.addItemDecoration(it) }
        adapter.setEmptyView(R.layout.view_alarm_list_empty,rcv_alarm_list)
        adapter.onItemClickListener = this
        adapter.onItemChildClickListener = this
        adapter.bindToRecyclerView(rcv_alarm_list)
        adapter.emptyView.setOnClickListener {it1->
            mDevice?.let {
                val calendar = Calendar.getInstance()
                viewModel.setCurrentAlarm(Alarm(1,it.id,0,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),1,0,0))
                Navigation.findNavController(it1).navigate(R.id.action_alarmListFragment_to_alarmSettingFragment)
            }
        }
        BluetoothSPP.getInstance()?.addOnDataReceivedListener(mOnDataReceivedListener)
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        adapter.getItem(position)?.let {
            it.editMode=1
            viewModel.setCurrentAlarm(it)
            Navigation.findNavController(iv_back).navigate(R.id.action_alarmListFragment_to_alarmSettingFragment)
        }
    }

    override fun onItemChildClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        adapter.getItem(position)?.let {
            when(view?.id){
                R.id.btn_delete-> {
                    isDelete=true
                    controller.cancelAlarm(mDevice?.id,it.id)
                    rcv_alarm_list.postDelayed({controller.stopAlarmRing(mDevice?.id)},150)
                    viewModel.deleteAlarm(it)
                }
                R.id.swipeLayout-> {
                    it.editMode=1
                    viewModel.setCurrentAlarm(it)
                    Navigation.findNavController(iv_back).navigate(R.id.action_alarmListFragment_to_alarmSettingFragment)
                }
            }
        }
    }

    override fun onStateChanged(isOn: Boolean, item: Alarm) {
        if (isOn) {
            item.isOn = 1
            controller.setAlarm(mDevice?.id,item)
        } else {
            item.isOn = 0
            controller.cancelAlarm(mDevice?.id,item.id)
            rcv_alarm_list.postDelayed({controller.stopAlarmRing(mDevice?.id)},150)
        }
        viewModel.saveAlarm(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isDelete=false
        BluetoothSPP.getInstance()?.removeOnDataReceivedListener(mOnDataReceivedListener)
    }

    private val mOnDataReceivedListener= BluetoothSPP.OnDataReceivedListener { data, message ->
        val receiveDataStr = Hex.toHexString(data).toUpperCase()
        if(receiveDataStr.startsWith("FE01D101DA0003C4")&&!isDelete){
            val isOn=TextUtils.equals("01",receiveDataStr.substring(16, 18))
            val alarmId = Integer.parseInt(receiveDataStr.substring(18, 20), 16)
            showCustomToast("闹钟" + alarmId + if(isOn) "已开启" else "已关闭")
        }
        isDelete=false
    }


    private fun showCustomToast(name: String) {
        context?.let {
            val toast = Toast(it)
            toast.setGravity(Gravity.BOTTOM, 0,it.dip2px(64f))
            toast.duration = Toast.LENGTH_SHORT
            val textView = TextView(it)
            textView.gravity = Gravity.CENTER
            textView.setPadding(it.dip2px(24f), it.dip2px(10f), it.dip2px(24f), it.dip2px(10f))
            textView.setTextColor(resources.getColor(android.R.color.black))
            textView.setBackgroundResource(R.drawable.bg_custom_toast)
            textView.text = name
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen._18ssp))
            toast.view = textView
            toast.show()
        }
    }

}
