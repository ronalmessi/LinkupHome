package com.ihomey.linkuphome.devicecontrol.view

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import cn.iclass.guideview.Component
import cn.iclass.guideview.Guide
import cn.iclass.guideview.GuideBuilder
import com.ihomey.linkuphome.PreferenceHelper
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.base.BaseNavHostFragment
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.dialog.InputDialogFragment
import com.ihomey.linkuphome.getDeviceId
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.DeviceStateChangeListener
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.InputDialogInterface
import com.ihomey.linkuphome.toast
import kotlinx.android.synthetic.main.device_control_fragment.*


class DeviceControlFragment : BaseFragment(), InputDialogInterface,FragmentBackHandler, DeviceStateChangeListener {

    private lateinit var mViewModel: HomeActivityViewModel

    private var controlView: BaseControlView? = null

    private var guide: Guide? = null

    private lateinit var device: Device

    var hasShowRenameDeviceGuide by PreferenceHelper("hasShowRenameDeviceGuide", false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        hideBottomBar()
        return inflater.inflate(R.layout.device_control_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(view).popBackStack() }
        tv_title.setOnClickListener {
            hideGuideView()
            val dialog = InputDialogFragment()
            val bundle = Bundle()
            bundle.putString("title", getString(R.string.title_rename))
            bundle.putString("inputText", device.name)
            dialog.arguments = bundle
            dialog.setInputDialogInterface(this)
            dialog.show(fragmentManager, "InputDialogFragment")
        }
    }

    private fun hideBottomBar() {
        parentFragment?.parentFragment?.let { if (it is BaseNavHostFragment) it.showBottomNavigationBar(false) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            mViewModel = ViewModelProviders.of(it).get(HomeActivityViewModel::class.java)
            mViewModel.getCurrentControlDevice().observe(viewLifecycleOwner, Observer<Device> { it0 ->
                device = it0
                tv_title.text = it0.name
                context?.let { it1 ->
                    ControlViewFactory().createControlView(it0.type, it1, this)?.let {
                        controlView = it
                        it.setDeviceStateChangeListener(this)
                        rootView.addView(it.getControlView(), ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                    }
                }
                tv_title.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (!hasShowRenameDeviceGuide) showGuideView(tv_title)
                        tv_title.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    }
                })
            })
        }
    }

    override fun onDeviceStateChange(device: Device, key: String, value: String) {
        updateState(device, key, value)
        if(device.type!=0)context?.getDeviceId()?.let { mViewModel.changeDeviceState(it, device.id, key, value).observe(viewLifecycleOwner, Observer<Resource<Device>> {}) }
    }


    private fun updateState(device: Device, key: String, value: String) {
        if (TextUtils.equals("brightness", key)) {
            val deviceState = device.parameters
            deviceState?.let {
                it.brightness = value.toInt()
                mViewModel.updateDeviceState(device, it)
            }
        } else {
            val deviceState = device.parameters
            deviceState?.let {
                it.on = value.toInt()
                mViewModel.updateRoomAndDeviceState(device, it)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        for (f in childFragmentManager.fragments) {
            if (f is RgbControlView || f is WarmColdControlView) {
                childFragmentManager.beginTransaction().remove(f).commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        controlView?.bindTo(device)
    }

    override fun onBackPressed(): Boolean {
        return if (guide != null && guide?.isVisible!!) {
            hideGuideView()
            true
        } else {
            false
        }
    }

    override fun onInput(text: String) {
        if (device.type == 0) {
            updateDeviceName(text, device)
        } else {
            context?.getDeviceId()?.let { it1 ->
                mViewModel.changeDeviceName(it1, device.zoneId, device.id,device.pid,device.type, text).observe(viewLifecycleOwner, Observer<Resource<Device>> {
                    when {
                        it?.status == Status.SUCCESS -> {
                            hideLoadingView()
                            updateDeviceName(text, device)
                        }
                        it?.status == Status.ERROR -> {
                            hideLoadingView()
                            it.message?.let { it2 -> activity?.toast(it2) }
                        }
                        it?.status == Status.ERROR -> showLoadingView()
                    }
                })
            }
        }
    }


    private fun updateDeviceName(newName: String, device: Device) {
        tv_title.text = newName
        device.name = newName
        mViewModel.updateDeviceName(device, newName)
    }

    private fun showGuideView(view: View) {
        val builder = GuideBuilder()
        builder.setTargetView(view)
                .setAlpha(200)
                .setHighTargetCorner(context?.resources?.getDimension(R.dimen._24sdp)?.toInt()!!)
                .setHighTargetPaddingLeft(context?.resources?.getDimension(R.dimen._27sdp)?.toInt()!!)
                .setHighTargetPaddingRight(context?.resources?.getDimension(R.dimen._27sdp)?.toInt()!!)
                .setHighTargetPaddingBottom(context?.resources?.getDimension(R.dimen._5sdp)?.toInt()!!)
                .setHighTargetPaddingTop(context?.resources?.getDimension(R.dimen._5sdp)?.toInt()!!)
                .setHighTargetMarginTop(getMarginTop(view) + context?.resources?.getDimension(R.dimen._13sdp)?.toInt()!!)
                .setAutoDismiss(true)
                .setOverlayTarget(false)
                .setOutsideTouchable(true)
        builder.setOnVisibilityChangedListener(object : GuideBuilder.OnVisibilityChangedListener {
            override fun onShown() {
                hasShowRenameDeviceGuide = true
            }

            override fun onDismiss() {}
        })
        builder.addComponent(object : Component {
            override fun getView(inflater: LayoutInflater): View {
                return inflater.inflate(R.layout.view_guide_device_rename, null)
            }

            override fun getAnchor(): Int {
                return Component.ANCHOR_BOTTOM
            }

            override fun getFitPosition(): Int {
                return Component.FIT_CENTER
            }

            override fun getXOffset(): Int {
                return 0
            }

            override fun getYOffset(): Int {
                return context?.resources?.getDimension(R.dimen._8sdp)?.toInt()!!
            }

        })
        guide = builder.createGuide()
        guide?.setShouldCheckLocInWindow(true)
        guide?.show(activity)
    }

    private fun getMarginTop(view: View): Int {
        val loc = IntArray(2)
        view.getLocationOnScreen(loc)
        return loc[1]
    }

    private fun hideGuideView() {
        if (guide != null && guide?.isVisible!!) {
            guide?.dismiss()
        }
    }
}