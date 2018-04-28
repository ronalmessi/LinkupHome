package com.ihomey.linkuphome.category

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.library.base.BaseActivity
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.AddedLampCategoryAdapter
import com.ihomey.linkuphome.adapter.UnAddedLampCategoryAdapter
import com.ihomey.linkuphome.control.MeshControlActivity
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.databinding.ActivityLampCategoryBinding
import com.ihomey.linkuphome.databinding.DialogLampCategoryUnaddedBinding
import com.ihomey.linkuphome.dip2px
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import com.yanzhenjie.recyclerview.swipe.*


/**
 * Created by dongcaizheng on 2017/12/21.
 */
class LampCategoryActivity : BaseActivity(), BaseQuickAdapter.OnItemClickListener, SwipeMenuItemClickListener, SwipeItemClickListener {

    private lateinit var mViewModel: LampCategoryViewModel
    private lateinit var mViewDataBinding: ActivityLampCategoryBinding
    private lateinit var mDialogBinding: DialogLampCategoryUnaddedBinding
    private lateinit var dialog: BottomSheetDialog
    private val unAddedLampCategoryAdapter: UnAddedLampCategoryAdapter = UnAddedLampCategoryAdapter(R.layout.lamp_category_unadded_item)
    private val addedLampCategoryAdapter: AddedLampCategoryAdapter = AddedLampCategoryAdapter(R.layout.lamp_category_added_item)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_lamp_category)
        mViewDataBinding.handlers = EventHandler()

        dialog = BottomSheetDialog(this)
        mDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_lamp_category_unadded, mViewDataBinding.lampCategoryCl, false)
        mDialogBinding.lampCategoryUnAddedRcvList.layoutManager = LinearLayoutManager(this)
        mDialogBinding.lampCategoryUnAddedRcvList.addItemDecoration(SpaceItemDecoration(dip2px(4f)))
        dialog.setContentView(mDialogBinding.root)
        dialog.setOnShowListener { mViewDataBinding.lampCategoryBtnAdd.visibility = View.GONE }
        dialog.setOnDismissListener { mViewDataBinding.lampCategoryBtnAdd.visibility = View.VISIBLE }
        dialog.window?.findViewById<FrameLayout>(R.id.design_bottom_sheet)?.setBackgroundResource(android.R.color.transparent)

        mViewDataBinding.lampCategoryAddedRcvList.layoutManager = LinearLayoutManager(this)
        mViewDataBinding.lampCategoryAddedRcvList.addItemDecoration(SpaceItemDecoration(dip2px(4f)))
        mViewDataBinding.lampCategoryAddedRcvList.adapter = addedLampCategoryAdapter

        val swipeMenuCreator = SwipeMenuCreator { _, swipeRightMenu, _ ->
            val width = dip2px(96f)
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val deleteItem = SwipeMenuItem(this).setBackground(R.drawable.selectable_lamp_category_delete_item_background).setWidth(width).setHeight(height).setText(R.string.delete).setTextColor(Color.WHITE).setTextSize(14)
            swipeRightMenu.addMenuItem(deleteItem)
        }

        mViewDataBinding.lampCategoryAddedRcvList.setSwipeItemClickListener(this)
        mViewDataBinding.lampCategoryAddedRcvList.setSwipeMenuCreator(swipeMenuCreator)
        mViewDataBinding.lampCategoryAddedRcvList.setSwipeMenuItemClickListener(this)

        mViewModel = ViewModelProviders.of(this).get(LampCategoryViewModel::class.java)

        mViewModel.getAddedResults().observe(this, Observer<Resource<List<LampCategory>>> {
            if (it?.status == Status.SUCCESS) addedLampCategoryAdapter.setNewData(it.data)
        })

        mViewModel.getUnAddedResults().observe(this, Observer<Resource<List<LampCategory>>> {
            if (it?.status == Status.SUCCESS) unAddedLampCategoryAdapter.setNewData(it.data)
        })

        unAddedLampCategoryAdapter.onItemClickListener = this
        addedLampCategoryAdapter.onItemClickListener = this

        mViewModel.loadCategories()
    }

    private fun showAddProductDialog() {
        mDialogBinding.lampCategoryUnAddedRcvList.adapter = unAddedLampCategoryAdapter
        dialog.show()
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View?, position: Int) {
        val lampCategory = adapter.getItem(position) as LampCategory
        lampCategory.added = 1
        mViewModel.updateCategory(lampCategory)
        dialog.dismiss()
    }

    override fun onItemClick(itemView: View?, position: Int) {
        val lampCategory = addedLampCategoryAdapter.getItem(position)
        val intent = Intent(this@LampCategoryActivity, MeshControlActivity::class.java)
        intent.putExtra("lampCategoryType", lampCategory?.type)
        startActivity(intent)
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
                R.id.toolbar_back -> (view.context as Activity).finish()
                R.id.lamp_category_btn_add -> showAddProductDialog()
            }
        }
    }

}