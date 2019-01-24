package com.ihomey.linkuphome.group

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.iclass.soocsecretary.util.PreferenceHelper
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.GroupListAdapter
import com.ihomey.linkuphome.data.vo.*
import com.ihomey.linkuphome.databinding.FragmentGroupListBinding
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import com.yanzhenjie.recyclerview.swipe.*


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class GroupListFragment : BaseFragment(), SwipeItemClickListener, SwipeMenuItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    private var lampCategoryType: Int = -1
    private lateinit var mViewDataBinding: FragmentGroupListBinding
    private lateinit var adapter: GroupListAdapter
    private var mViewModel: MainViewModel? = null


    private var setting: LampCategory? = null
    private var settingForType: LampCategory? = null

    fun newInstance(lampCategoryType: Int): GroupListFragment {
        val fragment = GroupListFragment()
        val bundle = Bundle()
        bundle.putInt("lampCategoryType", lampCategoryType)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_list, container, false)
        lampCategoryType = arguments?.getInt("lampCategoryType", -1)!!
        return mViewDataBinding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    mViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        mViewModel?.getGlobalSetting()?.observe(this, Observer<Resource<LampCategory>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                setting = it.data
            }
        })
        mViewModel?.getLocalSetting()?.observe(this, Observer<Resource<LampCategory>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                settingForType = it.data
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GroupListAdapter(lampCategoryType, arrayListOf())
        adapter.onItemChildClickListener = this

        mViewDataBinding.lampGroupRcvList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
//        mViewDataBinding.lampGroupRcvList.addItemDecoration(SpaceItemDecoration(dip2px(context, 2f).toInt()))
        mViewDataBinding.lampGroupRcvList.adapter = adapter

        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
//            if (swipeRightMenu.viewType == 1) {
////                val width = dip2px(context, 48f)
////                val height = ViewGroup.LayoutParams.MATCH_PARENT
////                val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width.toInt()).setHeight(height).setText(R.string.delete).setTextColor(Color.WHITE).setTextSize(14)
////                swipeRightMenu.addMenuItem(deleteItem)
//            }
        }
        mViewDataBinding.lampGroupRcvList.setSwipeItemClickListener(this)
        val isShare by PreferenceHelper("share_$lampCategoryType", false)
        if (!isShare) {
            mViewDataBinding.lampGroupRcvList.setSwipeMenuCreator(swipeMenuCreator)
            mViewDataBinding.lampGroupRcvList.setSwipeMenuItemClickListener(this)
        } else {
            mViewDataBinding.lampGroupRcvList.setSwipeMenuCreator(null)
            mViewDataBinding.lampGroupRcvList.setSwipeMenuItemClickListener(null)
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel?.getGroupResults(lampCategoryType)?.observe(this, Observer<Resource<List<GroupDevice>>> {
            if (it?.status == Status.SUCCESS) {
                val newData = arrayListOf<GroupDevice>()
                newData.addAll(it.data!!)
                var isShare by PreferenceHelper("share_$lampCategoryType", false)
                if (!isShare) {
                    newData.add(GroupDevice(-1, Device("", -1), null))
                }
                adapter.setNewData(newData)
            }
        })
    }

    override fun onItemClick(itemView: View?, position: Int) {
        val groupDevice = adapter.getItem(position)
        if (groupDevice?.id == -1) {
            mViewModel?.addGroup(setting!!, settingForType!!, getString(R.string.group), lampCategoryType)
        } else {
            mViewModel?.setCurrentControlDeviceInfo(DeviceInfo(groupDevice?.device?.type!!, groupDevice.id))
        }
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge,position: Int) {
        val lampGroup = adapter.getItem(position) as GroupDevice
        mViewModel?.deleteGroup(lampGroup)
        menuBridge.closeMenu()
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        if (parentFragment is IFragmentStackHolder) {
            val groupDevice = adapter?.getItem(position) as GroupDevice
            val newFrag = GroupSettingFragment().newInstance(groupDevice.id, groupDevice.device?.name, lampCategoryType)
            val fsh = parentFragment as IFragmentStackHolder
            fsh.replaceFragment(R.id.inner_frag_group_container, newFrag)
        }
    }

}