package com.ihomey.linkuphome.control

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.BedControlPageAdapter
import com.ihomey.linkuphome.data.vo.ControlDevice
import com.ihomey.linkuphome.databinding.FragmentControlBedBinding
import com.ihomey.linkuphome.main.BleLampFragment

class BedControlFragment : BaseControlFragment(), RadioGroup.OnCheckedChangeListener {

    private lateinit var mViewDataBinding: FragmentControlBedBinding

    fun newInstance(): BedControlFragment {
        return BedControlFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_control_bed, container, false)
        initController(5)
        mViewDataBinding.rgControlSwitch.setOnCheckedChangeListener(this)
        mViewDataBinding.bleControlVp.offscreenPageLimit = 2
        mViewDataBinding.bleControlVp.adapter = BedControlPageAdapter(5, childFragmentManager)
        mViewDataBinding.handlers = ToolBarEventHandler()
        return mViewDataBinding.root
    }


    override fun updateViewData(controlDevice: ControlDevice?) {

    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
       if(checkedId==R.id.rb_control_rgb){
           mViewDataBinding.bleControlVp.currentItem=0
       }else if(checkedId==R.id.rb_control_warm_cold){
           mViewDataBinding.bleControlVp.currentItem=1
       }
    }

    override fun onStart() {
        super.onStart()
        val bleLampFragment=parentFragment.parentFragment as BleLampFragment
        bleLampFragment.showBottomNavigationView()
    }
}