package com.ihomey.linkuphome.device

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceListAdapter
import com.ihomey.linkuphome.category.DeviceType
import com.ihomey.linkuphome.control.MeshControlViewModel
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.databinding.FragmentDeviceMeshListBinding
import com.ihomey.linkuphome.getName
import com.ihomey.linkuphome.getShortName
import com.ihomey.linkuphome.listener.EventHandler
import com.ihomey.linkuphome.listeners.DeviceAssociateListener
import com.ihomey.linkuphome.listeners.DeviceRemoveListener
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import com.yanzhenjie.loading.Utils
import com.yanzhenjie.recyclerview.swipe.*
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class MeshDeviceListFragment : BaseFragment(), SwipeItemClickListener, SwipeMenuItemClickListener, DeviceAssociateListener, DeviceRemoveListener {

    private var lampCategoryType: Int = -1
    private var isDeviceRemoving = false
    private val uuidHashArray: SparseArray<String> = SparseArray()
    private lateinit var mViewDataBinding: FragmentDeviceMeshListBinding
    private var adapter: DeviceListAdapter? = null
    private lateinit var listener: DevicesStateListener
    private var mViewModel: MeshControlViewModel? = null
    private var setting: LampCategory? = null
    private val deviceAssociateFragment = DeviceAssociateFragment()
    private val deviceRemoveFragment = DeviceRemoveFragment()
    private val bgRes = arrayListOf(R.mipmap.fragment_lawn_bg, R.mipmap.fragment_rgb_bg, R.mipmap.fragment_warm_cold_bg, R.mipmap.fragment_led_bg, R.mipmap.lamp_icon_bed)


    fun newInstance(lampCategoryType: Int): MeshDeviceListFragment {
        val fragment = MeshDeviceListFragment()
        val bundle = Bundle()
        bundle.putInt("lampCategoryType", lampCategoryType)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_mesh_list, container, false)
        mViewDataBinding.handlers = EventHandler()
        lampCategoryType = arguments.getInt("lampCategoryType", -1)
        if (lampCategoryType != -1) mViewDataBinding.root.setBackgroundResource(bgRes[lampCategoryType])
        return mViewDataBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DeviceListAdapter(R.layout.lamp_device_mesh_list_item)

        mViewDataBinding.lampDeviceMeshRcvList.layoutManager = LinearLayoutManager(context)
        mViewDataBinding.lampDeviceMeshRcvList.addItemDecoration(SpaceItemDecoration(Utils.dip2px(context, 2f).toInt()))
        mViewDataBinding.lampDeviceMeshRcvList.adapter = adapter

        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            val width = Utils.dip2px(context, 48f)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width.toInt()).setHeight(height).setText(R.string.delete).setTextColor(Color.WHITE).setTextSize(14)
            swipeRightMenu.addMenuItem(deleteItem)
        }

        mViewDataBinding.lampDeviceMeshRcvList.setSwipeItemClickListener(this)
        var isShare by PreferenceHelper("share_$lampCategoryType", false)
        if (!isShare) {
            mViewDataBinding.lampDeviceMeshRcvList.setSwipeMenuCreator(swipeMenuCreator)
            mViewDataBinding.lampDeviceMeshRcvList.setSwipeMenuItemClickListener(this)
        } else {
            mViewDataBinding.lampDeviceMeshRcvList.setSwipeMenuCreator(null)
            mViewDataBinding.lampDeviceMeshRcvList.setSwipeMenuItemClickListener(null)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity).get(MeshControlViewModel::class.java)
        mViewModel?.getDeviceResults()?.observe(this, Observer<Resource<List<SingleDevice>>> {
            if (it?.status == Status.SUCCESS) {
                if (adapter?.itemCount == 0) {
                    adapter?.setNewData(it.data)
                    uuidHashArray.clear()
                }
            }
        })
        mViewModel?.getDiscoverDevicesState()?.observe(this, Observer<Boolean> {
            listener.discoverDevices(it!!, this)
        })
        mViewModel?.getAssociateDeviceState()?.observe(this, Observer<Int> {
            listener.associateDevice(it!!, null)
        })

        mViewModel?.getRemoveDeviceState()?.observe(this, Observer<SingleDevice> {
            listener.removeDevice(it!!, this)
        })

        mViewModel?.getSettingResults()?.observe(this, Observer<Resource<List<LampCategory>>> { it ->
            if (it?.status == Status.SUCCESS && it.data?.size == 2) {
                setting = it.data[0]
            }
        })

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as DevicesStateListener
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        val isShare by PreferenceHelper("share_$lampCategoryType", false)
        if (!isShare) {
            mViewModel?.setDeviceDiscoveryFilterEnabled(userVisibleHint)
        }
        if (!userVisibleHint) {
            uuidHashArray.clear()
            adapter?.setNewData(null)
        } else {
            mViewModel?.loadDevices(lampCategoryType)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val isShare by PreferenceHelper("share_$lampCategoryType", false)
        if (!isShare) {
            mViewModel?.setDeviceDiscoveryFilterEnabled(false)
        }
        uuidHashArray.clear()
        adapter?.setNewData(null)
    }

    override fun onItemClick(itemView: View?, position: Int) {
        val singleDevice = adapter?.getItem(position)
        var isShare by PreferenceHelper("share_$lampCategoryType", false)
        if (!isShare && singleDevice?.id == 0) {
            deviceAssociateFragment.isCancelable = false
            deviceAssociateFragment.show(activity.fragmentManager, "DeviceAssociateFragment")
            mViewModel?.associateDevice(singleDevice.hash)
        } else {
            mViewModel?.setCurrentControlDeviceInfo(DeviceInfo(singleDevice?.device?.type!!, singleDevice.id))
        }
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge) {
        val singleDevice = adapter?.getItem(menuBridge.adapterPosition)
        if (singleDevice?.id != 0) {
            isDeviceRemoving = true
            showDeviceRemoveAlertDialog(singleDevice!!)
        }
        menuBridge.closeMenu()
    }

    override fun newUuid(uuid: UUID, uuidHash: Int, rssi: Int, ttl: Int) {

    }

    override fun newAppearance(uuidHash: Int, appearance: ByteArray, shortName: String) {
        Log.d("aa", "" + uuidHash + "---" + shortName + "---" + uuidHashArray.indexOfKey(uuidHash) + "---" + isDeviceRemoving)
        if (!isDeviceRemoving && uuidHashArray.indexOfKey(uuidHash) < 0) {
            uuidHashArray.put(uuidHash, shortName)
            val deviceType = DeviceType.values()[lampCategoryType]
            val deviceShortName = getShortName(deviceType)
            if (TextUtils.equals(deviceShortName, shortName)) adapter?.addData(SingleDevice(0, Device(getName(deviceType), lampCategoryType), uuidHash, 0, 0, 0, null))
        }
    }

    override fun associationProgress(progress: Int) {
        if (progress in 0..99) {
            deviceAssociateFragment.onAssociateProgressChanged(progress)
        }
    }

    override fun deviceAssociated(deviceId: Int, uuidHash: Int, bitmap: Long) {
        val deviceType = DeviceType.values()[lampCategoryType]
        val device = SingleDevice(deviceId, Device(getName(deviceType), lampCategoryType), uuidHash, 0, bitmap, 0, ControlState())
        val position = adapter?.data?.indexOf(device) ?: -1
        if (position != -1) {
            adapter?.getItem(position)?.id = deviceId
            adapter?.notifyItemChanged(position)
            mViewModel?.addDevice(setting!!, device)
        }
        deviceAssociateFragment.dismiss()
    }

    override fun deviceAssociated(deviceId: Int, message: String) {
        deviceAssociateFragment.onAssociateProgressChanged(0)
        deviceAssociateFragment.dismiss()
        activity.toast(message)
    }

    override fun onDeviceRemoved(deviceId: Int, uuidHash: Int, success: Boolean) {
        deviceRemoveFragment.dismiss()
        isDeviceRemoving = false
        val deviceType = DeviceType.values()[lampCategoryType]
        val device = SingleDevice(deviceId, Device(getName(deviceType), lampCategoryType), uuidHash, 0, 0, 0, ControlState())
        val position = adapter?.data?.indexOf(device) ?: -1
        if (position != -1) {
            uuidHashArray.remove(uuidHash)
            adapter?.remove(position)
            mViewModel?.removeDevice(lampCategoryType, deviceId)
        }
    }

    private fun showDeviceRemoveAlertDialog(singleDevice: SingleDevice) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(getString(R.string.delete) + " " + singleDevice.device.name + "?")
        builder.setPositiveButton(R.string.confirm) { _, _ ->
            deviceRemoveFragment.isCancelable = false
            deviceRemoveFragment.show(activity.fragmentManager, "DeviceRemoveFragment")
            mViewModel?.removeDevice(singleDevice)
        }
        builder.setNegativeButton(R.string.cancel, null)
        builder.setCancelable(false)
        builder.create().show()
    }

    interface DevicesStateListener {
        fun discoverDevices(enabled: Boolean, listener: DeviceAssociateListener)
        fun removeDevice(device: SingleDevice, listener: DeviceRemoveListener)
        fun associateDevice(uuidHash: Int, shortCode: String?)
    }
}