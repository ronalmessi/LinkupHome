package com.ihomey.linkuphome.alarm

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.home.HomeActivityViewModel
import kotlinx.android.synthetic.main.environmental_indicators_fragment.*
import android.text.style.RelativeSizeSpan
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ihomey.linkuphome.controller.M1Controller
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.spp.BluetoothSPP
import kotlinx.android.synthetic.main.environmental_indicators_fragment.iv_back
import org.spongycastle.util.encoders.Hex


open class EnvironmentalIndicatorsFragment : BaseFragment() {

    companion object {
        fun newInstance() = EnvironmentalIndicatorsFragment()
    }

    protected lateinit var mViewModel: HomeActivityViewModel

    private var mDevice: Device? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.environmental_indicators_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        fl_refresh.setOnClickListener {
            startAnimation()
        }
        BluetoothSPP.getInstance()?.addOnDataReceivedListener(mOnDataReceivedListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.getCurrentControlDevice().observe(viewLifecycleOwner, Observer<Device> {
            mDevice = it
            startAnimation()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BluetoothSPP.getInstance()?.removeOnDataReceivedListener(mOnDataReceivedListener)
    }

    private fun startAnimation() {
        BluetoothSPP.getInstance().send(mDevice?.id, decodeHex("BF01D101CD04C10207EFBD16".toUpperCase().toCharArray()), false)
        btn_refresh.isActivated = true
        btn_home.isActivated = true
        val rotate = AnimationUtils.loadAnimation(context, R.anim.rotate)
        rotate.interpolator = LinearInterpolator()
        btn_refresh.startAnimation(rotate)
    }

    private fun stopAnimation() {
        btn_refresh.isActivated = false
        btn_home.isActivated = false
        btn_refresh.clearAnimation()
    }


    private val mOnDataReceivedListener= BluetoothSPP.OnDataReceivedListener { data, _, _ ->
        val receiveDataStr = Hex.toHexString(data).toUpperCase()
        if (receiveDataStr.startsWith("FE01D101DA000BC107")) {
            val pm25Value = Integer.parseInt(receiveDataStr.substring(18, 20), 16) * 256 + Integer.parseInt(receiveDataStr.substring(20, 22), 16)
            val hchoValue = Integer.parseInt(receiveDataStr.substring(22, 24), 16) * 256 + Integer.parseInt(receiveDataStr.substring(24, 26), 16)
            val vocValue = Integer.parseInt(receiveDataStr.substring(26, 28), 16)
            updateEnvironmentalIndicatorViews(pm25Value, hchoValue, vocValue)
        }else if (TextUtils.equals("FE01D101DA0004C2050101CD16", receiveDataStr)) {
            activity?.toast("床头灯正在播放音乐", Toast.LENGTH_SHORT)
        }
    }

    private fun updateEnvironmentalIndicatorViews(pm25Value: Int, hchoValue: Int, vocValue: Int) {
        stopAnimation()
        val relativeSizeSpan = RelativeSizeSpan(2.2f)
        val hchoSpannableString = SpannableString("$hchoValue ug/m³")
        hchoSpannableString.setSpan(relativeSizeSpan, 0, hchoSpannableString.length - 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_hcho_value.text = hchoSpannableString
        tv_hcho_value_level.text= getHCHOLevel(hchoValue)
        val pm25SpannableString = SpannableString("$pm25Value ug/m³")
        pm25SpannableString.setSpan(relativeSizeSpan, 0, hchoSpannableString.length - 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv_pm25_value.text = pm25SpannableString
        tv_pm25_value_level.text= getPM25Level(pm25Value)
        tv_voc_value.text = "" + vocValue
        tv_voc_value_level.text= getVOCLevel(vocValue)
    }
}
