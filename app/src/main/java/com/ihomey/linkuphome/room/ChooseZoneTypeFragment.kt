package com.ihomey.linkuphome.room

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.ZoneTypeListAdapter
import com.ihomey.linkuphome.data.entity.Setting
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.entity.ZoneSetting
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.CreateSubZoneListener
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.choose_zone_type_fragment.*

class ChooseZoneTypeFragment : Fragment(), BaseQuickAdapter.OnItemClickListener, CreateSubZoneListener {

    companion object {
        fun newInstance() = ChooseZoneTypeFragment()
    }

    private lateinit var adapter: ZoneTypeListAdapter
    private lateinit var mViewModel: HomeActivityViewModel
    private var currentSetting: Setting? = null
    private var currentZone: Zone? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.choose_zone_type_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)
        mViewModel.getGlobalSetting().observe(this, Observer<Resource<Setting>> { it ->
            if (it?.status == Status.SUCCESS) {
                currentSetting = it.data
            }
        })
        mViewModel.mCurrentZone.observe(this, Observer<Resource<Zone>> { it ->
            if (it?.status == Status.SUCCESS) {
                currentZone = it.data
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (parentFragment?.parentFragment as HomeFragment).showBottomNavigationBar(false)
        adapter = ZoneTypeListAdapter(R.layout.item_zone_type_list)
        adapter.onItemClickListener = this
        rcv_zone_type_list.layoutManager = GridLayoutManager(context, 3)
        context?.resources?.getDimensionPixelSize(R.dimen._21sdp)?.let { SpaceItemDecoration(it, it, it, it) }?.let { rcv_zone_type_list.addItemDecoration(it) }
        rcv_zone_type_list.adapter = adapter
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val dialog = CreateRoomFragment()
        val bundle = Bundle()
        adapter.getItem(position)?.let { bundle.putInt("zoneTYpe", it) }
        dialog.arguments = bundle
        dialog.setCreateSubZoneListener(this)
        dialog.show(fragmentManager, "CreateRoomFragment")
    }


    override fun createSubZone(type: Int, name: String) {
        currentSetting?.let { currentZone?.let { it1 -> mViewModel.addRoom(it, it1, type, name) } }
        Navigation.findNavController(iv_back).popBackStack()
    }

}
