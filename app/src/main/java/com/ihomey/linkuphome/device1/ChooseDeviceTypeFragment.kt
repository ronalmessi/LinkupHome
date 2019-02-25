package com.ihomey.linkuphome.device1

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.base.BaseFragment

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceTypeListAdapter
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.BottomNavigationVisibilityListener
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.choose_device_type_fragment.*

class ChooseDeviceTypeFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = ChooseDeviceTypeFragment()
    }

    private lateinit var listener: BottomNavigationVisibilityListener
    private lateinit var viewModel: ChooseDeviceTypeViewModel
    private lateinit var adapter: DeviceTypeListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.choose_device_type_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChooseDeviceTypeViewModel::class.java)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as BottomNavigationVisibilityListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener.showBottomNavigationBar(false)
        adapter = DeviceTypeListAdapter(R.layout.item_device_type_list)
        adapter.onItemClickListener = this
        rcv_device_type_list.layoutManager = GridLayoutManager(context, 2)
        context?.resources?.getDimensionPixelSize(R.dimen._10sdp)?.let { SpaceItemDecoration(it / 2, it / 2, it / 2, it / 2) }?.let { rcv_device_type_list.addItemDecoration(it) }
        rcv_device_type_list.adapter = adapter
        adapter.setNewData(listOf(3, 4))
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        val deviceType = adapter.getItem(position)
        val bundle = Bundle()
        deviceType?.let { bundle.putInt("deviceType", it) }
        arguments?.getInt("zoneId")?.let { bundle.putInt("zoneId", it) }
        Navigation.findNavController(view).navigate(R.id.action_chooseDeviceTypeFragment_to_searchDeviceHintFragment2, bundle)
    }
}

