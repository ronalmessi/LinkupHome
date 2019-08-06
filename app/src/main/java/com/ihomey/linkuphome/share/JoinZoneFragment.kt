package com.ihomey.linkuphome.share

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.data.vo.ZoneDetail
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.BridgeListener
import com.ihomey.linkuphome.toast
import kotlinx.android.synthetic.main.zone_join_fragment.*

class JoinZoneFragment : BaseFragment() {

    companion object {
        fun newInstance() = JoinZoneFragment()
    }

    private lateinit var mViewModel: JoinZoneViewModel
    private lateinit var viewModel: HomeActivityViewModel
    private lateinit var bridgeListener: BridgeListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zone_join_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(JoinZoneViewModel::class.java)
        viewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        bridgeListener= context as BridgeListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack()}
        btn_join_zone.setOnClickListener { joinZone() }
    }

    private fun joinZone() {
        val invitationCode=et_invitation_code.text.toString().trim()
        if(!TextUtils.isEmpty(invitationCode)){
            context?.getIMEI()?.let { it1 ->  mViewModel.joinZone(it1,invitationCode).observe(viewLifecycleOwner, Observer<Resource<ZoneDetail>> {
                if (it?.status == Status.SUCCESS) {
                    hideLoadingView()
                    activity?.toast(getString(R.string.msg_join_zone_success), Toast.LENGTH_SHORT)
                    viewModel.setCurrentZoneId(it.data?.id)
                    bridgeListener.reConnectBridge()
                    Navigation.findNavController(et_invitation_code).popBackStack()
                }else if (it?.status == Status.ERROR) {
                    hideLoadingView()
                    activity?.toast(getString(R.string.msg_error_invitation_code), Toast.LENGTH_SHORT)
                }else if (it?.status == Status.LOADING) {
                    showLoadingView()
                }
            })}
        }
    }
}
