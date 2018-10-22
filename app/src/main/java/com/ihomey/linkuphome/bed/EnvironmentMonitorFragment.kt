package com.ihomey.linkuphome.bed

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.FragmentEnvironmentMonitorBinding
import com.ihomey.linkuphome.listener.OnDrawerMenuItemClickListener
import com.ihomey.linkuphome.main.BleLampFragment

class EnvironmentMonitorFragment : BaseFragment(), OnDrawerMenuItemClickListener {

    private lateinit var mViewDataBinding: FragmentEnvironmentMonitorBinding

    fun newInstance(): EnvironmentMonitorFragment {
        return EnvironmentMonitorFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_environment_monitor, container, false)
        val bleLampFragment = parentFragment as BleLampFragment
        bleLampFragment.setOnDrawerMenuItemClickListener(this)
        return mViewDataBinding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showFragment(ThermometerFragment().newInstance(1))
    }

    override fun onMenuItemClick(viewId: Int, position: Int) {

    }

    fun showFragment(fragment: BaseFragment?) {
        if (fragment != null && !fragment.isAdded) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.inner_frag_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}