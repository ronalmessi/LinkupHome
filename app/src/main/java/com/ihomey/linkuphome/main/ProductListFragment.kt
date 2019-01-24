package com.ihomey.linkuphome.main

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
import com.ihomey.linkuphome.adapter.AddedProductListAdapter
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.databinding.FragmentProductListBinding
import com.ihomey.linkuphome.device.DeviceConnectFragment
import com.ihomey.linkuphome.dip2px
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import com.yanzhenjie.recyclerview.swipe.*


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class ProductListFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener, SwipeMenuItemClickListener, SwipeItemClickListener {

    private lateinit var mViewModel: MainViewModel
    private lateinit var mViewDataBinding: FragmentProductListBinding
    private var isConnected = false

    private val addedLampCategoryAdapter: AddedProductListAdapter = AddedProductListAdapter(R.layout.item_product_list)

    fun newInstance(): ProductListFragment {
        return ProductListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_list, container, false)
        mViewDataBinding.handlers = EventHandler()

        mViewDataBinding.lampCategoryAddedRcvList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        mViewDataBinding.lampCategoryAddedRcvList.addItemDecoration(SpaceItemDecoration(context?.dip2px(4f)!!))
        mViewDataBinding.lampCategoryAddedRcvList.adapter = addedLampCategoryAdapter

        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            val width = context?.dip2px(96f)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width!!).setHeight(height).setText(R.string.delete).setTextColor(Color.WHITE).setTextSize(14)
            swipeRightMenu.addMenuItem(deleteItem)
        }

        mViewDataBinding.lampCategoryAddedRcvList.setSwipeItemClickListener(this)
        mViewDataBinding.lampCategoryAddedRcvList.setSwipeMenuCreator(swipeMenuCreator)
        mViewDataBinding.lampCategoryAddedRcvList.setSwipeMenuItemClickListener(this)

        addedLampCategoryAdapter.onItemClickListener = this
        return mViewDataBinding.root
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View?, position: Int) {
        val lampCategory = adapter.getItem(position) as LampCategory
        lampCategory.added = 1
        mViewModel.updateCategory(lampCategory)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel.getCategoryResults()?.observe(this, Observer<Resource<List<LampCategory>>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                addedLampCategoryAdapter.setNewData(it.data.filter { it.added == 1 })
                mViewDataBinding.lampCategoryBtnAdd.visibility = if (it.data.none { it -> it.added == 0 }) View.INVISIBLE else View.VISIBLE
            }
        })
        mViewModel.getBridgeState().observe(this, Observer<Boolean> {
            if (it != null) {
                isConnected = it
            }
        })
    }

    override fun onItemClick(itemView: View?, position: Int) {
        val lampCategory = addedLampCategoryAdapter.getItem(position)
        val fsh = activity as IFragmentStackHolder
        if (lampCategory != null) {
            if (isConnected) {
                fsh.replaceFragment(R.id.container, LampFragment().newInstance(lampCategory.type))
            } else {
                val hasConnected by PreferenceHelper("hasConnected" + lampCategory.type, false)
                fsh.replaceFragment(R.id.container, DeviceConnectFragment().newInstance(lampCategory.type, hasConnected, false))
            }
            mViewModel.loadData(lampCategory.type)
        }
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge,position: Int) {
        val lampCategory = addedLampCategoryAdapter.getItem(position) as LampCategory
        lampCategory.added = 0
        mViewModel.updateCategory(lampCategory)
        menuBridge.closeMenu()
    }

    inner class EventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.toolbar_back -> (view.context as Activity).onBackPressed()
                R.id.lamp_category_btn_add -> (activity as IFragmentStackHolder).replaceFragment(R.id.container, AddProductFragment().newInstance(true))
            }
        }
    }
}