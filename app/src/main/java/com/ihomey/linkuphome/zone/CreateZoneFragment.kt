package com.ihomey.linkuphome.zone

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.ihomey.library.base.BaseFragment

import com.ihomey.linkuphome.R
import kotlinx.android.synthetic.main.create_zone_fragment.*

class CreateZoneFragment : BaseFragment() {

    companion object {
        fun newInstance() = CreateZoneFragment()
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
        val isCurrent=arguments?.getBoolean("isCurrent")?:true
        if(!isCurrent) iv_back.visibility=View.VISIBLE
        btn_save.setOnClickListener {
            viewModel.createZone(et_zone_name.text.toString().trim(),isCurrent)
            if(isCurrent)NavHostFragment.findNavController(this@CreateZoneFragment).navigate(R.id.action_createZoneFragment_to_homeFragment) else Navigation.findNavController(activity!!, R.id.nav_host).popBackStack()
        }
        iv_back.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).popBackStack() }
    }
}
