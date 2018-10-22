package com.ihomey.linkuphome.group

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.bgRes
import com.ihomey.linkuphome.handleBackPress
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.ihomey.linkuphome.listener.IFragmentStackHolder

/**
 * Created by dongcaizheng on 2018/4/17.
 */
class GroupFragment : BaseFragment(), IFragmentStackHolder, FragmentBackHandler {

    fun newInstance(lampCategoryType: Int): GroupFragment {
        val fragment = GroupFragment()
        val bundle = Bundle()
        bundle.putInt("lampCategoryType", lampCategoryType)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater!!.inflate(R.layout.fragment_group, null)
        view.findViewById<ImageButton>(R.id.toolbar_back).setOnClickListener {
            activity.onBackPressed()
        }
        val lampCategoryType = arguments.getInt("lampCategoryType", -1)
        if (lampCategoryType != -1) {
            view.setBackgroundResource(bgRes[lampCategoryType])
        }
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragment(GroupListFragment().newInstance(arguments.getInt("lampCategoryType")))
    }

    private fun setFragment(frag: BaseFragment) {
        val transaction = childFragmentManager.beginTransaction()
        childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        transaction.replace(R.id.inner_frag_group_container, frag, frag.javaClass.simpleName)
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