package com.ihomey.linkuphome.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.listener.FragmentBackHandler


class BaseNavHostFragment : BaseFragment(), FragmentBackHandler {

    //    private var listener: FragmentVisibleStateListener? = null
    private lateinit var finalHost: NavHostFragment

    var isVisibleToUser: Boolean = false

    fun newInstance(navGraphId: Int): BaseNavHostFragment {
        val baseNavHostFragment = BaseNavHostFragment()
        val bundle = Bundle()
        bundle.putInt("navGraphId", navGraphId)
        baseNavHostFragment.arguments = bundle
        return baseNavHostFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_navhost_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val navGraphId = arguments?.getInt("navGraphId")
            navGraphId?.let {
                finalHost = NavHostFragment.create(it)
                childFragmentManager.beginTransaction().replace(R.id.nav_host, finalHost).setPrimaryNavigationFragment(finalHost).commit()
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isVisibleToUser = isVisibleToUser
//        if (listener != null) listener?.onFragmentVisibleStateChanged(isVisibleToUser)
    }

    override fun onBackPressed(): Boolean {
        return true
    }

//    fun setFragmentVisibleStateListener(listener: FragmentVisibleStateListener) {
//        this.listener = listener
//    }
}
