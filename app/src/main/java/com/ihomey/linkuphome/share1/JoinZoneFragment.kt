package com.ihomey.linkuphome.share1

import android.os.Bundle
import android.text.TextUtils
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
import com.ihomey.linkuphome.toast
import kotlinx.android.synthetic.main.zone_join_fragment.*

class JoinZoneFragment : BaseFragment() {

    companion object {
        fun newInstance() = JoinZoneFragment()
    }

    private lateinit var mViewModel: JoinZoneViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zone_join_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(JoinZoneViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack()}
        btn_join_zone.setOnClickListener { joinZone() }
    }

    private fun joinZone() {
        val invitationCode=et_invitation_code.text.toString().trim()
        if(!TextUtils.isEmpty(invitationCode)){
            context?.getIMEI()?.let { it1 ->  mViewModel.joinZone(it1,invitationCode).observe(viewLifecycleOwner, Observer<Resource<Zone>> {
                if (it?.status == Status.SUCCESS) {

                }else if (it?.status == Status.ERROR) {
                    it.message?.let { it2 -> activity?.toast(it2) }

                }
            })}
        }else{

        }
    }
}
