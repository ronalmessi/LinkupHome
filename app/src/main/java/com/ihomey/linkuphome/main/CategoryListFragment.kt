package com.ihomey.linkuphome.main

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.AddedLampCategoryAdapter
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.databinding.DialogLampCategoryUnaddedBinding
import com.ihomey.linkuphome.databinding.FragmentCategoryListBinding
import com.ihomey.linkuphome.dip2px
import com.ihomey.linkuphome.listener.BridgeListener
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import com.yanzhenjie.recyclerview.swipe.*


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class CategoryListFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener, SwipeMenuItemClickListener, SwipeItemClickListener {

    private lateinit var mViewModel: MainViewModel
    private lateinit var mViewDataBinding: FragmentCategoryListBinding
    private lateinit var mDialogBinding: DialogLampCategoryUnaddedBinding
//    private lateinit var dialog: BottomSheetDialog
    private lateinit var listener: BridgeListener

//    private val unAddedLampCategoryAdapter: UnAddedLampCategoryAdapter = UnAddedLampCategoryAdapter(R.layout.lamp_category_unadded_item)
    private val addedLampCategoryAdapter: AddedLampCategoryAdapter = AddedLampCategoryAdapter(R.layout.item_product_list)

    fun newInstance(): CategoryListFragment {
        return CategoryListFragment()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as BridgeListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_list, container, false)
        mViewDataBinding.handlers = EventHandler()

//        dialog = BottomSheetDialog(context)
//        mDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_lamp_category_unadded, mViewDataBinding.lampCategoryCl, false)
//        mDialogBinding.lampCategoryUnAddedRcvList.layoutManager = LinearLayoutManager(context)
//        mDialogBinding.lampCategoryUnAddedRcvList.addItemDecoration(SpaceItemDecoration(context.dip2px(4f)))
//        dialog.setContentView(mDialogBinding.root)
//        dialog.setOnShowListener { mViewDataBinding.lampCategoryBtnAdd.visibility = View.GONE }
//        dialog.setOnDismissListener { mViewDataBinding.lampCategoryBtnAdd.visibility = View.VISIBLE }
//        dialog.window?.findViewById<FrameLayout>(R.id.design_bottom_sheet)?.setBackgroundResource(android.R.color.transparent)

        mViewDataBinding.lampCategoryAddedRcvList.layoutManager = LinearLayoutManager(context)
        mViewDataBinding.lampCategoryAddedRcvList.addItemDecoration(SpaceItemDecoration(context.dip2px(4f)))
        mViewDataBinding.lampCategoryAddedRcvList.adapter = addedLampCategoryAdapter

        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            val width = context.dip2px(96f)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val deleteItem = SwipeMenuItem(context).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width).setHeight(height).setText(R.string.delete).setTextColor(Color.WHITE).setTextSize(14)
            swipeRightMenu.addMenuItem(deleteItem)
        }

        mViewDataBinding.lampCategoryAddedRcvList.setSwipeItemClickListener(this)
        mViewDataBinding.lampCategoryAddedRcvList.setSwipeMenuCreator(swipeMenuCreator)
        mViewDataBinding.lampCategoryAddedRcvList.setSwipeMenuItemClickListener(this)

        mViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        mViewModel.getCategoryResults()?.observe(this, Observer<Resource<List<LampCategory>>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                addedLampCategoryAdapter.setNewData(it.data.filter { it.added == 1 })
//                unAddedLampCategoryAdapter.setNewData(it.data.filter { it.added == 0 })
            }
        })

//        unAddedLampCategoryAdapter.onItemClickListener = this
        addedLampCategoryAdapter.onItemClickListener = this

        return mViewDataBinding.root
    }

    override fun onResume() {
        super.onResume()
        listener.connectBridge()
    }

//    private fun showAddProductDialog() {
//        mDialogBinding.lampCategoryUnAddedRcvList.adapter = unAddedLampCategoryAdapter
//        dialog.show()
//    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View?, position: Int) {
        val lampCategory = adapter.getItem(position) as LampCategory
        lampCategory.added = 1
        mViewModel.updateCategory(lampCategory)
//        dialog.dismiss()
    }

    override fun onItemClick(itemView: View?, position: Int) {
        val lampCategory = addedLampCategoryAdapter.getItem(position)
        val fsh = activity as IFragmentStackHolder
        if (lampCategory != null) {
            fsh.replaceFragment(R.id.container, LampFragment().newInstance(lampCategory.type))
            mViewModel.loadData(lampCategory.type)
        }
    }

    override fun onItemClick(menuBridge: SwipeMenuBridge) {
        val lampCategory = addedLampCategoryAdapter.getItem(menuBridge.adapterPosition) as LampCategory
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