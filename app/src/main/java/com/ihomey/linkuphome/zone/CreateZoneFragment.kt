package com.ihomey.linkuphome.zone

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.home.HomeActivity
import kotlinx.android.synthetic.main.create_zone_fragment.*
import kotlinx.android.synthetic.main.zones_fragment.*

class CreateZoneFragment : BaseFragment() {

    companion object {
        fun newInstance(isDefault: Boolean): CreateZoneFragment {
            val fragment = CreateZoneFragment()
            val bundle = Bundle()
            bundle.putBoolean("isDefault", isDefault)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var viewModel: CreateZoneViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.create_zone_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CreateZoneViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        et_zone_name.setSelection(et_zone_name.text.toString().trim().length)
        val isCurrent = arguments?.getBoolean("isDefault") ?: true
        if (!isCurrent) iv_back.visibility = View.VISIBLE
        btn_save.setOnClickListener {
            context?.getIMEI()?.let { it1 -> viewModel.createZone(it1,et_zone_name.text.toString().trim(), isCurrent).observe(this, Observer<Resource<Boolean>> {
                if (it?.status == Status.SUCCESS) {
                      Log.d("aa","---111")
                }else  if (it?.status == Status.ERROR) {
                    Log.d("aa","---111"+it.message)
                }
            }) }
//            if (isCurrent) {
//                startActivity(Intent(activity, HomeActivity::class.java))
//                activity?.finish()
//            } else Navigation.findNavController(it).popBackStack()
        }

        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
    }
}
