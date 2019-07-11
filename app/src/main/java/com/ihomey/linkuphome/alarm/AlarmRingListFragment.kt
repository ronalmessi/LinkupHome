package com.ihomey.linkuphome.alarm

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.RingListAdapter
import com.ihomey.linkuphome.data.entity.Alarm
import com.ihomey.linkuphome.dip2px
import com.ihomey.linkuphome.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.alarm_ring_list_fragment.*
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import android.util.SparseIntArray


class AlarmRingListFragment : BaseFragment(), BaseQuickAdapter.OnItemClickListener {


    private var ringListAdapter: RingListAdapter = RingListAdapter(false, R.layout.item_day_of_week_alarm)

    private lateinit var soundPool:SoundPool

    private val soundIdArray=SparseIntArray()

    private lateinit var mViewModel: AlarmViewModel

    private var ringType: Int=0

    private lateinit var mAlarm: Alarm

    fun newInstance(): AlarmRingListFragment {
        return AlarmRingListFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.alarm_ring_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(activity!!).get(AlarmViewModel::class.java)
        mViewModel.mAlarm.observe(viewLifecycleOwner, Observer<Alarm> {
            mAlarm=it
            ringType=mAlarm.ringType
            updateViews(it)
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSoundPool()
        (rcv_rings.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        rcv_rings.layoutManager = LinearLayoutManager(context)
        rcv_rings.addItemDecoration(DividerItemDecoration(LinearLayoutManager.HORIZONTAL, 0, context?.dip2px(0.5f)!!, Color.WHITE, false))
        rcv_rings.adapter = ringListAdapter
        ringListAdapter.onItemClickListener = this
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack()}
    }

    private fun initSoundPool() {
        soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        soundIdArray.put(1,soundPool.load(context, R.raw.a0, 1))
        soundIdArray.put(2,soundPool.load(context, R.raw.a1, 1))
        soundIdArray.put(3,soundPool.load(context, R.raw.a2, 1))
    }

    private fun updateViews(alarm: Alarm?) {
         alarm?.let {
             ringListAdapter.setItemSelected(it.ringType,true)
         }
    }


    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        if(position>0){
            soundPool.play(soundIdArray.get(position), 1f, 1f, 0, -1, 1f)
        }else{
            soundPool.stop(soundIdArray.get(ringListAdapter.getSelectedPosition()))
        }
        ringListAdapter.setItemSelected(position, !ringListAdapter.isItemSelected(position))
        mAlarm.ringType=ringListAdapter.getSelectedPosition()
        if(ringType!=ringListAdapter.getSelectedPosition()) mAlarm.editMode=2
    }

    override fun onDestroyView() {
        super.onDestroyView()
        soundPool.release()
    }
}