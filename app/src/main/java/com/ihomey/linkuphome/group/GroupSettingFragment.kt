package com.ihomey.linkuphome.group

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.Point
import android.graphics.PointF
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.adapter.BondedDeviceAdapter
import com.ihomey.linkuphome.adapter.UnBondedDeviceAdapter
import com.ihomey.linkuphome.data.entity.Model
import com.ihomey.linkuphome.data.entity.SingleDevice
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.databinding.FragmentGroupSettingBinding
import com.ihomey.linkuphome.listener.GroupUpdateListener
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.widget.DividerItemDecoration
import com.ihomey.linkuphome.widget.DragShadowBuilder
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener


/**
 * Created by dongcaizheng on 2018/4/17.
 */
class GroupSettingFragment : BaseFragment(), BaseQuickAdapter.OnItemChildClickListener, GroupUpdateListener, SwipeMenuItemClickListener, TextView.OnEditorActionListener {

    private var lampCategoryType: Int = -1
    private var mGroupId: Int = -1
    private var mGroupName: String = ""

    private lateinit var listener: ModelUpdateListener
    private var bondedDeviceAdapter: BondedDeviceAdapter? = null
    private var unBondedDeviceAdapter: UnBondedDeviceAdapter? = null
    private lateinit var mViewDataBinding: FragmentGroupSettingBinding
    private lateinit var swipeMenuCreator: SwipeMenuCreator
    private var mViewModel: MainViewModel? = null
    private var mDialog: GroupUpdateFragment? = null
    private var mSingleDevice: SingleDevice? = null

    private val dragTouchPointAdded: PointF? = PointF()
    private val dragTouchPointUnAdded: PointF? = PointF()


    private var isAdd = true
    private var isValidDrag = false

    fun newInstance(groupId: Int, groupName: String?, groupType: Int): GroupSettingFragment {
        val fragment = GroupSettingFragment()
        val bundle = Bundle()
        bundle.putInt("groupId", groupId)
        bundle.putString("groupName", groupName)
        bundle.putInt("groupType", groupType)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_setting, container, false)
        lampCategoryType = arguments?.getInt("groupType", -1)!!
        mGroupId = arguments?.getInt("groupId", -1)!!
        mGroupName = arguments?.getString("groupName", "")!!
        if (lampCategoryType != -1) {
            mViewDataBinding.groupSettingIvType.setImageResource(getIcon(lampCategoryType))
            mViewDataBinding.groupSettingEtName.setText(mGroupName)
            mViewDataBinding.groupSettingEtName.setSelection(mGroupName.length)
            mViewDataBinding.groupSettingEtName.requestFocus()
            mViewDataBinding.groupSettingEtName.setOnEditorActionListener(this)
            mViewDataBinding.root.setBackgroundResource(if (lampCategoryType != 3) R.drawable.round_black_bg else R.drawable.round_shallow_white_bg)
        }
        return mViewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        unBondedDeviceAdapter = UnBondedDeviceAdapter(R.layout.group_setting_device_unbonded, arrayListOf())
        unBondedDeviceAdapter?.onItemChildClickListener = this
        mViewDataBinding.groupSettingRcvUnBondedDevices.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        mViewDataBinding.groupSettingRcvUnBondedDevices.addItemDecoration(DividerItemDecoration(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, 2, ContextCompat.getColor(context!!, android.R.color.white), false))
        mViewDataBinding.groupSettingRcvUnBondedDevices.adapter = unBondedDeviceAdapter
        unBondedDeviceAdapter?.onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
            val singleDevice = unBondedDeviceAdapter?.getItem(position)
            val item = ClipData.Item(singleDevice?.name)
            val dragData = ClipData(singleDevice?.name as CharSequence, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
            val point = Point(((dragTouchPointUnAdded?.x?.minus(view.x))?.toInt()!!), ((dragTouchPointUnAdded.y.minus(view.y)).toInt()))
            val myShadow = DragShadowBuilder(view, point)
            mViewDataBinding.groupSettingRcvUnBondedDevices.startDrag(dragData, myShadow, singleDevice, 0);
            true
        }
//        mViewDataBinding.groupSettingRcvUnBondedDevices.addOnItemTouchListener(object : androidx.recyclerview.widget.RecyclerView.OnItemTouchListener {
//            override fun onTouchEvent(rv: androidx.recyclerview.widget.RecyclerView?, e: MotionEvent?) {
//
//            }
//
//            override fun onInterceptTouchEvent(rv: androidx.recyclerview.widget.RecyclerView?, e: MotionEvent): Boolean {
//                isAdd = false;
//                dragTouchPointUnAdded?.set(e.x, e.y)
//                return false
//            }
//
//            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//
//            }
//
//        })

        mViewDataBinding.groupSettingLlUnBondedDevices.setOnDragListener { v, event ->
            val action = event.action
            val singleDevice = event.localState as SingleDevice
            if (isAdd) {
                when (action) {
                    DragEvent.ACTION_DRAG_ENTERED -> isValidDrag = true
                    DragEvent.ACTION_DRAG_EXITED -> isValidDrag = false
                    DragEvent.ACTION_DRAG_ENDED -> {
                        if (isValidDrag) {
                            isValidDrag = false
                            unBindDevice(singleDevice)
                        }
                    }
                }
            }
            true
        }

        bondedDeviceAdapter = BondedDeviceAdapter(R.layout.group_setting_device_bonded, arrayListOf())
        mViewDataBinding.groupSettingRcvBondedDevices.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        mViewDataBinding.groupSettingRcvBondedDevices.addItemDecoration(DividerItemDecoration(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, 2, ContextCompat.getColor(context!!, android.R.color.white), true))
        bondedDeviceAdapter?.bindToRecyclerView(mViewDataBinding.groupSettingRcvBondedDevices)
        bondedDeviceAdapter?.setEmptyView(R.layout.empty_bonded_device_view)
        bondedDeviceAdapter?.onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { adapter, view, position ->
            val singleDevice = bondedDeviceAdapter?.getItem(position)
            val item = ClipData.Item(singleDevice?.name)
            val dragData = ClipData(singleDevice?.name as CharSequence, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
            val point = Point(((dragTouchPointAdded?.x?.minus(view.x))?.toInt()!!), ((dragTouchPointAdded.y.minus(view.y)).toInt()))
            val myShadow = DragShadowBuilder(view, point)
            mViewDataBinding.groupSettingRcvBondedDevices.startDrag(dragData, myShadow, singleDevice, 0);
            true
        }
//        mViewDataBinding.groupSettingRcvBondedDevices.addOnItemTouchListener(object : androidx.recyclerview.widget.RecyclerView.OnItemTouchListener {
//            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onTouchEvent(rv: androidx.recyclerview.widget.RecyclerView?, e: MotionEvent?) {
//
//            }
//
//            override fun onInterceptTouchEvent(rv: androidx.recyclerview.widget.RecyclerView?, e: MotionEvent): Boolean {
//                isAdd = true
//                dragTouchPointAdded?.set(e.x, e.y)
//                return false
//            }
//
//            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//
//            }

//        })
        mViewDataBinding.groupSettingRcvBondedDevices.setOnDragListener { v, event ->
            val action = event.action
            val singleDevice = event.localState as SingleDevice
            if (!isAdd) {
                when (action) {
                    DragEvent.ACTION_DRAG_ENTERED -> isValidDrag = true
                    DragEvent.ACTION_DRAG_EXITED -> isValidDrag = false
                    DragEvent.ACTION_DRAG_ENDED -> {
                        if (isValidDrag) {
                            isValidDrag = false
                            bindDevice(singleDevice)
                        }
                    }
                }
            }
            true
        }

        swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            val width = context?.dip2px(48f)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width!!.toInt()).setHeight(height).setText(R.string.delete).setTextColor(Color.WHITE).setTextSize(14)
            swipeRightMenu.addMenuItem(deleteItem)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as ModelUpdateListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        mViewModel?.getBondedDeviceResults()?.observe(this, Observer<Resource<List<SingleDevice>>> {
            if (it?.status == Status.SUCCESS) {
                bondedDeviceAdapter?.setNewData(it.data)
                if (bondedDeviceAdapter?.itemCount!! > 0) {
                    mViewDataBinding.groupSettingRcvBondedDevices.setSwipeMenuCreator(swipeMenuCreator)
                    mViewDataBinding.groupSettingRcvBondedDevices.setSwipeMenuItemClickListener(this)
                } else {
                    mViewDataBinding.groupSettingRcvBondedDevices.setSwipeMenuCreator(null)
                    mViewDataBinding.groupSettingRcvBondedDevices.setSwipeMenuItemClickListener(null)
                }
                updateBondedDevicesRcvHeight()
            }
        })
        mViewModel?.getUnBondedDeviceResults()?.observe(this, Observer<Resource<List<SingleDevice>>> {
            if (it?.status == Status.SUCCESS) {
                unBondedDeviceAdapter?.setNewData(it.data)
            }
        })
        mViewModel?.getModelResults()?.observe(this, Observer<Resource<List<Model>>> {
            if (it?.status == Status.SUCCESS) {
                if (mDialog != null && mDialog!!.isVisible) {
                    listener.updateModel(mSingleDevice?.id!!, mGroupId, it.data, this)
                }
            }
        })
        mViewModel?.loadBoundedDevices(DeviceInfo(lampCategoryType, mGroupId))
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val singleDevice = adapter?.getItem(position) as SingleDevice
        bindDevice(singleDevice)
    }

    override fun groupsUpdated(deviceId: Int, groupId: Int, groupIndex: Int, success: Boolean, msg: String?) {
        mDialog?.dismiss()
        if (groupId == 0) {
            mViewModel?.deleteModel(deviceId, mGroupId, groupIndex)
            if (lampCategoryType == 4) syncTime(deviceId)
        } else if (groupId != -1) {
//            mViewModel?.addModel(Model(0, deviceId, groupId, groupIndex, lampCategoryType,1))
        }
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge?,position:Int) {
        val singleDevice = bondedDeviceAdapter?.getItem(position) as SingleDevice
        unBindDevice(singleDevice)
        menuBridge?.closeMenu()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        context?.hideInput(mViewDataBinding.groupSettingEtName)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || (event != null && KeyEvent.KEYCODE_ENTER == event.keyCode && KeyEvent.ACTION_DOWN == event.action)) {
            if (mViewDataBinding.groupSettingEtName.text != null && !TextUtils.isEmpty(mViewDataBinding.groupSettingEtName.text.toString())) {
                mViewModel?.updateDeviceName(lampCategoryType, mGroupId, mViewDataBinding.groupSettingEtName.text.toString())
                mViewDataBinding.groupSettingEtName.isCursorVisible = false
            }
            context?.hideInput(mViewDataBinding.groupSettingEtName)
        }
        return false
    }

    private fun updateBondedDevicesRcvHeight() {
        mViewDataBinding.groupSettingRcvBondedDevices.post {
            val linearParams = mViewDataBinding.groupSettingRcvUnBondedDevices.layoutParams as LinearLayout.LayoutParams
            if (mViewDataBinding.groupSettingRcvUnBondedDevices.height > (mViewDataBinding.groupSettingCl.height - mViewDataBinding.groupSettingLlHeader.height)*2 / 3) {
                linearParams.height = (mViewDataBinding.groupSettingCl.height - mViewDataBinding.groupSettingLlHeader.height)*2 / 3
            }
            mViewDataBinding.groupSettingRcvUnBondedDevices.layoutParams = linearParams
        }
    }

    private fun bindDevice(singleDevice: SingleDevice) {
        mSingleDevice = singleDevice
        mDialog = GroupUpdateFragment()
        val bundle = Bundle()
        bundle.putInt("updateType", 0)
//        mDialog?.arguments = bundle
//        mDialog?.isCancelable = false
//        mDialog?.show(activity?.fragmentManager, "GroupUpdateFragment")
//        mViewModel?.loadModels(singleDevice.id)
    }

    private fun unBindDevice(singleDevice: SingleDevice) {
        mSingleDevice = singleDevice
        mDialog = GroupUpdateFragment()
        val bundle = Bundle()
        bundle.putInt("updateType", 1)
//        mDialog?.arguments = bundle
//        mDialog?.isCancelable = false
//        mDialog?.show(activity?.fragmentManager, "GroupUpdateFragment")
//        mViewModel?.loadModels(singleDevice.id)
    }

    interface ModelUpdateListener {
        fun updateModel(deviceId: Int, groupId: Int, models: List<Model>?, groupUpdateListener: GroupUpdateListener)
    }
}