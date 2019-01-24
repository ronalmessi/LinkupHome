package com.ihomey.linkuphome.zone

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ihomey.linkuphome.R

class ZonesFragment : Fragment() {

    companion object {
        fun newInstance() = ZonesFragment()
    }

    private lateinit var viewModel: ZonesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.zones_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ZonesViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
