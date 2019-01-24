package com.ihomey.linkuphome.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.ihomey.library.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.LanguageListAdapter
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.databinding.DialogLanguageSelectionBinding
import com.ihomey.linkuphome.databinding.FragmentWelcomeBinding
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.IFragmentStackHolder
import com.ihomey.linkuphome.listener.OnLanguageListener
import com.ihomey.linkuphome.ui.CenterActivity
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.widget.DividerDecoration

class WelcomeFragment : BaseFragment(), FragmentBackHandler {

    lateinit var mViewDataBinding: FragmentWelcomeBinding
    private lateinit var mViewModel: MainViewModel
    private lateinit var listener: OnLanguageListener
    var dialog: BottomSheetDialog? = null
    private var addedProductCount = 0

    fun newInstance(): WelcomeFragment {
        return WelcomeFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false)
        mViewDataBinding.handlers = EventHandler()
        mViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        mViewModel.getCategoryResults()?.observe(this, Observer<Resource<List<LampCategory>>> {
            if (it?.status == Status.SUCCESS && it.data != null) {
                addedProductCount = it.data.filter { it.added == 1 }.size
            }
        })
        val currentLanguage = LocaleHelper.getLanguage(context)
        if(TextUtils.equals("nl",currentLanguage)){
            mViewDataBinding.welcomeTvOccurrence.visibility=View.INVISIBLE
        }else{
            mViewDataBinding.welcomeTvOccurrence.visibility=View.VISIBLE
        }
        return mViewDataBinding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnLanguageListener
    }


    inner class EventHandler : LanguageListAdapter.OnItemClickListener {

        fun onClick(view: View) {
            when (view.id) {
                R.id.toolbar_center -> view.context.startActivity(Intent(view.context, CenterActivity::class.java))
                R.id.toolbar_language -> showLanguageSelectionDialog(view)
                R.id.welcome_btn_open -> (activity as IFragmentStackHolder).replaceFragment(R.id.container, if(addedProductCount==0) AddProductFragment().newInstance(false) else ProductListFragment().newInstance())
                R.id.language_selection_btn_cancel ->dialog?.dismiss()
            }
        }

        private fun showLanguageSelectionDialog(view: View) {
            if (dialog == null) {
                dialog = BottomSheetDialog(view.context)
                val adapter = LanguageListAdapter(view.context.resources.getStringArray(R.array.language_array))
                adapter.setOnItemClickListener(this)
                val binding = DataBindingUtil.inflate<DialogLanguageSelectionBinding>(LayoutInflater.from(view.context), R.layout.dialog_language_selection, mViewDataBinding.welcomeClContent, false)
                binding.handlers = EventHandler()
                binding.languageSelectionRcvList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(view.context)
                binding.languageSelectionRcvList.adapter = adapter
                binding.languageSelectionRcvList.addItemDecoration(DividerDecoration(view.context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, true))
                dialog?.setContentView(binding.root)
                dialog?.setOnShowListener { mViewDataBinding.welcomeBtnOpen.visibility = View.GONE }
                dialog?.setOnDismissListener { mViewDataBinding.welcomeBtnOpen.visibility = View.VISIBLE }
                dialog?.window?.findViewById<FrameLayout>(R.id.design_bottom_sheet)?.setBackgroundResource(android.R.color.transparent)
            }
            dialog?.show()
        }

        override fun onItemClick(position: Int) {
            dialog?.dismiss()
            listener.onLanguageChange(position)
        }
    }


    override fun onBackPressed(): Boolean {
        return  false
    }
}