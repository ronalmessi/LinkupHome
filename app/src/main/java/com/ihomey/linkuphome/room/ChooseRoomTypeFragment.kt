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
import com.ihomey.linkuphome.base.BaseNavHostFragment
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.Resource
import com.ihomey.linkuphome.data.vo.Status
import com.ihomey.linkuphome.dialog.InputDialogFragment
import com.ihomey.linkuphome.getIMEI
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.listener.InputDialogInterface
import com.ihomey.linkuphome.toast
import com.ihomey.linkuphome.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.choose_room_type_fragment.*

class ChooseRoomTypeFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener,  InputDialogInterface {

    companion object {
        fun newInstance() = ChooseRoomTypeFragment()
    }

    private lateinit var adapter: RoomTypeListAdapter
    private lateinit var mViewModel: HomeActivityViewModel
    private var currentZone: Zone? = null
    private var selectedRoomType :Int?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.choose_room_type_fragment, container, false)
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
        val verticalMargin = (dm.heightPixels - context?.resources?.getDimensionPixelSize(R.dimen._64sdp)!! * 5) / 8
        rcv_zone_type_list.addItemDecoration(SpaceItemDecoration(0, 0, verticalMargin, 0))
        rcv_zone_type_list.adapter = adapter
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        parentFragment?.parentFragment?.let { (it as BaseNavHostFragment).showBottomNavigationBar(false) }
    }

    override fun onItemClick(adapter1: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        adapter.getItem(position)?.let {
            selectedRoomType=it
            val dialog = InputDialogFragment()
            val bundle = Bundle()
            bundle.putString("title", getString(R.string.title_rename_room))
            dialog.arguments = bundle
            dialog.setInputDialogInterface(this)
            dialog.show(fragmentManager, "InputDialogFragment")
        }
    }

    override fun onInput(text: String) {
      selectedRoomType?.let { it0->
          currentZone?.let { it1->
              context?.getIMEI()?.let { it2 ->
                  mViewModel.saveRoom(it2, it1.id, it0 + 1, text).observe(viewLifecycleOwner, Observer<Resource<Room>> {
                      when {
                          it?.status == Status.SUCCESS -> {
                              hideLoadingView()
                              Navigation.findNavController(iv_back).popBackStack()
                          }
                          it?.status == Status.ERROR -> {
                              hideLoadingView()
                              it.message?.let { it2 -> activity?.toast(it2) }
                          }
                          it?.status == Status.LOADING -> showLoadingView()
                      }
                  })
              }
          }
      }
    }

}
