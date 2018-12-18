package com.ihomey.linkuphome.main

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.FragmentPrivacyStatementBinding


/**
 * Created by dongcaizheng on 2018/4/10.
 */
class PrivacyStatementFragment : BaseFragment() {

    private lateinit var mViewDataBinding: FragmentPrivacyStatementBinding

    fun newInstance(type:Int): PrivacyStatementFragment {
        val fragment=PrivacyStatementFragment()
        val bundle=Bundle()
        bundle.putInt("type",type)
        fragment.arguments=bundle
        return fragment
}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_statement, container, false)
        mViewDataBinding.handlers = EventHandler()
        if(arguments.getInt("type")==0){
            mViewDataBinding.toolbarTitle.text=getString(R.string.license_agreement)
            mViewDataBinding.tvLicenseAgreementTitle.text="The End user License Agreement"
            mViewDataBinding.tvLicenseAgreementContent.text=getString(R.string.license_agreement_content)
        }else{
            mViewDataBinding.toolbarTitle.text=getString(R.string.privacy_statement)
            mViewDataBinding.tvLicenseAgreementTitle.text="The MeshLight Privacy Statement"
            mViewDataBinding.tvLicenseAgreementContent.text=getString(R.string.privacy_statement_content)
        }
        return mViewDataBinding.root
    }


    inner class EventHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.toolbar_close -> (view.context as Activity).onBackPressed()
            }
        }
    }
}