package com.ihomey.linkuphome.zone.create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.encodeBase64
import com.ihomey.linkuphome.getDeviceId

import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.protocol.sigmesh.SigMeshServiceManager
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.worker.CreateMeshNetWorker
import kotlinx.android.synthetic.main.create_zone_fragment.*


class CreateZoneFragment : BaseFragment() {

    companion object {
        fun newInstance() = CreateZoneFragment()
    }

    var isDefault =false

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
         isDefault = arguments?.getBoolean("isDefault") ?: true
        if (!isDefault) iv_back.visibility = View.VISIBLE
        btn_save.setOnClickListener {
            createMeshNet()
        }
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
    }


    private fun createMeshNet() {
        val createMeshNetWorker = OneTimeWorkRequest.Builder(CreateMeshNetWorker::class.java).build()
        WorkManager.getInstance().getWorkInfoByIdLiveData(createMeshNetWorker.id).observe(this, Observer { workStatus ->
            if (workStatus.state == WorkInfo.State.SUCCEEDED) {
                val result = workStatus.outputData.getString("result")
                result?.encodeBase64()?.let {
                    saveSpace(it)
                }
            }
        })
        WorkManager.getInstance().beginUniqueWork("CreateMeshNetWorker", ExistingWorkPolicy.REPLACE, createMeshNetWorker).enqueue()
    }


    private fun saveSpace(meshInfo:String){
        context?.getDeviceId()?.let { it1 ->
            createZoneViewModel.createZone(it1, meshInfo,et_zone_name.text.toString().trim()).observe(viewLifecycleOwner, Observer<Resource<Zone>> {
                if (it?.status == Status.SUCCESS) {
                    hideLoadingView()
                    SigMeshServiceManager.getInstance().isInited=false
                    homeActivityViewModel.setCurrentZoneId(it.data?.id)
                    if (isDefault) {
                        Navigation.findNavController(btn_save).navigate(R.id.action_createZoneFragment_to_homeFragment)
                    } else {
                        Navigation.findNavController(btn_save).popBackStack()
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
}
