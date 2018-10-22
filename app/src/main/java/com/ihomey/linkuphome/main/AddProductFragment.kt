package com.ihomey.linkuphome.main

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.UnAddedProductListAdapter
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.databinding.FragmentProductAddBinding
import com.ihomey.linkuphome.dip2px
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.widget.SpaceItemDecoration


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class AddProductFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener {

    private lateinit var mViewModel: MainViewModel
    private lateinit var mViewDataBinding: FragmentProductAddBinding

    private val addedLampCategoryAdapter: UnAddedProductListAdapter = UnAddedProductListAdapter(R.layout.item_product_add)

    fun newInstance(isGoBack: Boolean): AddProductFragment {
        val addProductFragment = AddProductFragment()
        val bundle = Bundle()
        bundle.putBoolean("isGoBack", isGoBack)
        addProductFragment.arguments = bundle
        return addProductFragment
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_add, container, false)
        mViewDataBinding.handlers = EventHandler()

        mViewDataBinding.lampCategoryAddedRcvList.layoutManager = GridLayoutManager(context, 2)
        mViewDataBinding.lampCategoryAddedRcvList.addItemDecoration(SpaceItemDecoration(context.dip2px(8f)))
        mViewDataBinding.lampCategoryAddedRcvList.adapter = addedLampCategoryAdapter

        mViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)
        mViewModel.getCategoryResults()?.observe(this, Observer<Resource<List<LampCategory>>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                addedLampCategoryAdapter.setNewData(it.data)
            }
        })
        addedLampCategoryAdapter.onItemClickListener = this
        return mViewDataBinding.root
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View?, position: Int) {
        val lampCategory = adapter.getItem(position) as LampCategory
        lampCategory.added = 1
        mViewModel.updateCategory(lampCategory)
        if (arguments.getBoolean("isGoBack", false)) {
            activity.onBackPressed()
        } else {
            activity.onBackPressed()
            (activity as IFragmentStackHolder).replaceFragment(R.id.container, ProductListFragment().newInstance())
        }
    }


    inner class EventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.toolbar_back -> (view.context as Activity).onBackPressed()
            }
        }
    }
}