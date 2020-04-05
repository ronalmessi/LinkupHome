package com.ihomey.linkuphome.zone.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.getDeviceId

import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.toast
import kotlinx.android.synthetic.main.create_zone_fragment.*


class CreateZoneFragment : BaseFragment() {

    companion object {
        fun newInstance() = CreateZoneFragment()
    }

    private lateinit var createZoneViewModel: CreateZoneViewModel
    private lateinit var homeActivityViewModel: HomeActivityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.create_zone_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        createZoneViewModel = ViewModelProviders.of(this).get(CreateZoneViewModel::class.java)
        homeActivityViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        et_zone_name.requestFocus()
        et_zone_name.setSelection(et_zone_name.text.toString().trim().length)
        val isDefault = arguments?.getBoolean("isDefault") ?: true
        if (!isDefault) iv_back.visibility = View.VISIBLE
        btn_save.setOnClickListener { it0 ->
            context?.getDeviceId()?.let { it1 ->
                createZoneViewModel.createZone(it1, et_zone_name.text.toString().trim()).observe(viewLifecycleOwner, Observer<Resource<Zone>> {
                    if (it?.status == Status.SUCCESS) {
                        hideLoadingView()
                        homeActivityViewModel.setCurrentZoneId(it.data?.id)
                        if (isDefault) {
                            Navigation.findNavController(it0).navigate(R.id.action_createZoneFragment_to_homeFragment)
                        } else {
                            Navigation.findNavController(it0).popBackStack()
                        }
                    } else if (it?.status == Status.ERROR) {
                        hideLoadingView()
                        it.message?.let { it2 -> activity?.toast(it2) }
                    } else if (it?.status == Status.LOADING) {
                        showLoadingView()
                    }
                })
            }
        }
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
    }
}
