package com.ihomey.linkuphome.room

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.RoomTypeListAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.CreateSubZoneListener
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import com.ihomey.linkuphome.zone.ZoneNavHostFragment
import kotlinx.android.synthetic.main.choose_zone_type_fragment.*

class ChooseRoomTypeFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener, CreateSubZoneListener {

    companion object {
        fun newInstance() = ChooseRoomTypeFragment()
    }

    private lateinit var adapter: RoomTypeListAdapter
    private lateinit var mViewModel: HomeActivityViewModel
    private var currentZone: Zone? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.choose_zone_type_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(HomeActivityViewModel::class.java)

        mViewModel.mCurrentZone.observe(this, Observer<Resource<Zone>> { it ->
            if (it?.status == Status.SUCCESS) {
                currentZone = it.data
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = RoomTypeListAdapter(R.layout.item_zone_type_list)
        adapter.onItemClickListener = this
        rcv_zone_type_list.layoutManager = GridLayoutManager(context, 3)
        val dm = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getRealMetrics(dm)
        val verticalMargin=(dm.heightPixels- context?.resources?.getDimensionPixelSize(R.dimen._64sdp)!!*5)/8
        rcv_zone_type_list.addItemDecoration( SpaceItemDecoration(0, 0, verticalMargin, 0))
        rcv_zone_type_list.adapter = adapter
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        parentFragment?.parentFragment?.let { (it as ZoneNavHostFragment).showBottomNavigationBar(false) }
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
        context?.getIMEI()?.let { it1 -> mViewModel.saveRoom(it1, currentZone?.id!!,type+1,name).observe(viewLifecycleOwner, Observer<Resource<Room>> {
            if (it?.status == Status.SUCCESS) {
                mViewModel.setCurrentZoneId(it.data?.zoneId)
                Navigation.findNavController(iv_back).popBackStack()
            }else  if (it?.status == Status.ERROR) {
                it.message?.let { it2 -> activity?.toast(it2) }
            }
        })}
    }

}
