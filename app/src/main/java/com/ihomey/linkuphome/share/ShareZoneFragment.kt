package com.ihomey.linkuphome.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import kotlinx.android.synthetic.main.zone_share_fragment.*

class ShareZoneFragment : BaseFragment() {

    companion object {
        fun newInstance() = ShareZoneFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zone_share_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_invitation_code.text=arguments?.getString("invitationCode")
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
    }

}
