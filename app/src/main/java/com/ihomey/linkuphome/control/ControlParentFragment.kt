package com.ihomey.linkuphome.control

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.handleBackPress
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.IFragmentStackHolder

/**
 * Created by dongcaizheng on 2018/4/17.
 */
class ControlParentFragment : BaseFragment(), IFragmentStackHolder, FragmentBackHandler {

    fun newInstance(lampCategoryType: Int): ControlParentFragment {
        val fragment = ControlParentFragment()
        val bundle = Bundle()
        bundle.putInt("lampCategoryType", lampCategoryType)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_control_parent, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lampCategoryType = arguments?.getInt("lampCategoryType", -1)
        when (lampCategoryType) {
            1 -> setFragment(RgbControlFragment().newInstance())
            2 -> setFragment(WarmColdControlFragment().newInstance())
            3 -> setFragment(LedControlFragment().newInstance())
            4 -> setFragment(OutdoorControlFragment().newInstance())
            5 -> setFragment(S1ControlFragment().newInstance())
            6 -> setFragment(S2ControlFragment().newInstance())
        }
    }

    private fun setFragment(frag: BaseFragment) {
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