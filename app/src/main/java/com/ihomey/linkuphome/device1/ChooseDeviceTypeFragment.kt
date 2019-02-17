package com.ihomey.linkuphome.device1

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.library.base.BaseFragment

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceTypeListAdapter
import com.ihomey.linkuphome.data.vo.LampCategory
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.choose_device_type_fragment.*

class ChooseDeviceTypeFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = ChooseDeviceTypeFragment()
    }

    private lateinit var viewModel: ChooseDeviceTypeViewModel
    private lateinit var adapter: DeviceTypeListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.choose_device_type_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ChooseDeviceTypeViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DeviceTypeListAdapter(R.layout.item_device_type_list)
        adapter.onItemClickListener = this
        rcv_device_type_list.layoutManager = GridLayoutManager(context, 2)
        context?.resources?.getDimensionPixelSize(R.dimen._10sdp)?.let { SpaceItemDecoration(it / 2, it / 2, it / 2, it / 2) }?.let { rcv_device_type_list.addItemDecoration(it) }
        rcv_device_type_list.adapter = adapter
        adapter.setNewData(listOf(LampCategory(0, 3, 0, "", 0, 0), LampCategory(0, 4, 0, "", 0, 0)))
//        adapter.setNewData(listOf(LampCategory(0, 0, 0, "", 0, 0), LampCategory(0, 1, 0, "", 0, 0), LampCategory(0, 2, 0, "", 0, 0), LampCategory(0, 3, 0, "", 0, 0), LampCategory(0, 4, 0, "", 0, 0), LampCategory(0, 5, 0, "", 0, 0), LampCategory(0, 6, 0, "", 0, 0), LampCategory(0, 7, 0, "", 0, 0)))
        iv_back.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).popBackStack() }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val lampCategory = adapter.getItem(position)
        if (lampCategory != null) {
            val bundle = Bundle()
            bundle.putInt("deviceType", lampCategory.type)
            Navigation.findNavController(activity!!, R.id.nav_host).navigate(R.id.action_chooseDeviceTypeFragment_to_searchDeviceHintFragment, bundle)
        }
    }
}
