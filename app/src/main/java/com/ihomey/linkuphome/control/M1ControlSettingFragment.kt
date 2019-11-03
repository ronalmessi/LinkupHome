package com.ihomey.linkuphome.control

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.SleepModeDialogFragment
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.controller.Controller
import com.ihomey.linkuphome.controller.ControllerFactory
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.decodeHex
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.spp.BluetoothSPP
import com.ihomey.linkuphome.toast
import kotlinx.android.synthetic.main.m1_control_setting_fragment.*
import org.spongycastle.util.encoders.Hex


/**
 * Created by dongcaizheng on 2018/4/15.
 */
class M1ControlSettingFragment : BaseFragment() {

    private lateinit var viewModel: HomeActivityViewModel

    private var controller: Controller? = null

    private var mDevice: Device?=null

    private var hasQuerySleepModeState: Boolean=false
    private var hasQueryGestureControlState: Boolean=false


    private val mOnDataReceivedListener= BluetoothSPP.OnDataReceivedListener { data, _, _ ->
        val receiveDataStr = Hex.toHexString(data).toUpperCase()
        if(receiveDataStr.startsWith("FE01D101DA0004C20103")){
            val isSleepModeOn=TextUtils.equals("01",receiveDataStr.substring(20,22))
            if(!hasQuerySleepModeState){
                hasQuerySleepModeState=true
                sb_sleep_mode.isChecked=isSleepModeOn
                sb_sleep_mode.postDelayed(sleepModeRunnable,500)
            }else{
                activity?.toast(if(isSleepModeOn) R.string.msg_sleep_mode_on else R.string.msg_sleep_mode_off,Toast.LENGTH_SHORT)
            }
        }else if(receiveDataStr.startsWith("FE01D101DA0004C70101")){
            val isGestureControlEnabled=TextUtils.equals("01",receiveDataStr.substring(20,22))
            if(!hasQueryGestureControlState){
                hasQueryGestureControlState=true
                sb_gesture_control.isChecked=isGestureControlEnabled
                sb_gesture_control.postDelayed(gestureControlRunnable,500)
            }else{
                activity?.toast(if(isGestureControlEnabled) R.string.msg_gesture_control_on else R.string.msg_gesture_control_off,Toast.LENGTH_SHORT)
            }
        }else if(receiveDataStr.startsWith("FE01D101DA000AC3012")){
            activity?.toast( R.string.msg_sync_time_success, Toast.LENGTH_SHORT)
        }
    }

    companion object {
        fun newInstance() = M1ControlSettingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.m1_control_setting_fragment,container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        viewModel.getCurrentControlDevice().observe(this, Observer<Device> {
            controller = ControllerFactory().createController(it.type,TextUtils.equals("LinkupHome V1",it.name))
            mDevice=it
            queryDeviceState(it.id)
        })
    }

    private val sleepModeRunnable= Runnable {
        sb_sleep_mode.setOnCheckedChangeListener { _, isChecked -> controller?.setSleepMode(mDevice?.id,if(isChecked) 1 else 0)}
    }

    private val gestureControlRunnable= Runnable {
        sb_gesture_control.setOnCheckedChangeListener { _, isChecked -> controller?.enableGestureControl(mDevice?.id,isChecked)}
    }

    private fun queryDeviceState(deviceId:String?){
        BluetoothSPP.getInstance().send(deviceId, decodeHex("BF01D101CD04C2090103D316".toUpperCase().toCharArray()), false)
        Handler().postDelayed({BluetoothSPP.getInstance().send(deviceId, decodeHex("BF01D101CD04C2070103D116".toUpperCase().toCharArray()), false)},150)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_sleep_mode.setOnClickListener {
            val dialogFragment = SleepModeDialogFragment()
            dialogFragment.show(fragmentManager, null)
        }

        infoTextLayout_setting_syncTime.setOnClickListener { controller?.syncTime(mDevice?.id)}
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack()}
        infoTextLayout_setting_timer.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_m1ControlSettingFragment_to_m1TimerSettingFragment) }
        BluetoothSPP.getInstance()?.addOnDataReceivedListener(mOnDataReceivedListener)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        sb_sleep_mode.removeCallbacks(sleepModeRunnable)
        sb_gesture_control.removeCallbacks(gestureControlRunnable)
        BluetoothSPP.getInstance()?.removeOnDataReceivedListener(mOnDataReceivedListener)
        sb_sleep_mode.setOnCheckedChangeListener(null)
        sb_gesture_control.setOnCheckedChangeListener(null)
        hasQuerySleepModeState=false
        hasQueryGestureControlState=false
    }
}