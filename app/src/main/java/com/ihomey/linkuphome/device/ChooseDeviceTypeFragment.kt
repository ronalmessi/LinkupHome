package com.ihomey.linkuphome.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.DeviceTypeListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.choose_device_type_fragment.*


class ChooseDeviceTypeFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = ChooseDeviceTypeFragment()
    }

    private lateinit var adapter: DeviceTypeListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.choose_device_type_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment?.parentFragment?.let { (it as DeviceNavHostFragment).showBottomNavigationBar(false) }
        adapter = DeviceTypeListAdapter(R.layout.item_device_type_list)
        adapter.onItemClickListener = this
        rcv_device_type_list.layoutManager = GridLayoutManager(context, 2)
        context?.resources?.getDimensionPixelSize(R.dimen._10sdp)?.let { SpaceItemDecoration(it / 2, it / 2, it / 2, it / 2) }?.let { rcv_device_type_list.addItemDecoration(it) }
        rcv_device_type_list.adapter = adapter
        adapter.setNewData(listOf(0, 1, 3, 2, 6, 10, 7, 8, 4, 9))
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View, position: Int) {
        adapter.getItem(position)?.let {
            val bundle = Bundle()
            arguments?.getInt("zoneId")?.let { bundle.putInt("zoneId", it) }
            if (it == 0) {
                Navigation.findNavController(view).navigate(R.id.action_chooseDeviceTypeFragment_to_connectM1DeviceFragment, bundle)
            } else {
                bundle.putInt("deviceType", it)
                Navigation.findNavController(view).navigate(R.id.action_chooseDeviceTypeFragment_to_searchDeviceHintFragment2, bundle)
            }
        }
    }
}

