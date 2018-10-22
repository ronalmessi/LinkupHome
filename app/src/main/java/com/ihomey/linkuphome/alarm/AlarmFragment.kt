package com.ihomey.linkuphome.alarm

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.handleBackPress
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.IFragmentStackHolder

class AlarmFragment : BaseFragment(), IFragmentStackHolder, FragmentBackHandler {

    fun newInstance(): AlarmFragment {
        return AlarmFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_control_parent, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showFragment(AlarmListFragment().newInstance())
    }

    private fun showFragment(frag: BaseFragment) {
        val transaction = childFragmentManager.beginTransaction()
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        transaction.replace(R.id.inner_frag_control_container, frag, frag.javaClass.simpleName)
        transaction.addToBackStack(frag.javaClass.simpleName)
        transaction.commit()
    }

    override fun replaceFragment(containerId: Int, frag: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.push_right_in, R.anim.hold, R.anim.hold, R.anim.push_left_out)
        transaction.replace(containerId, frag, frag.javaClass.simpleName)
        transaction.addToBackStack(frag.javaClass.simpleName)
        transaction.commit()
    }

    override fun onBackPressed(): Boolean {
        return if (childFragmentManager.backStackEntryCount == 1) {
            false
        } else {
            handleBackPress(this)
        }
    }
}