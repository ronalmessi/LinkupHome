package com.ihomey.linkuphome.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.umeng.analytics.MobclickAgent

/**
 * Created by Administrator on 2017/6/16.
 */
abstract class BaseFragment : Fragment() {


    private var loadingFragment: LoadingFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BaseFragment", javaClass.simpleName + "-----onCreate")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.d("BaseFragment", javaClass.simpleName + "-----onAttach")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("BaseFragment", javaClass.simpleName + "-----onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("BaseFragment", javaClass.simpleName + "-----onActivityCreated")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("BaseFragment", javaClass.simpleName + "-----onDetach")
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this.context)
        MobclickAgent.onPageEnd(this.javaClass.simpleName)
        Log.d("BaseFragment", javaClass.simpleName + "-----onPause")
    }

    override fun onStart() {
        super.onStart()
        Log.d("BaseFragment", javaClass.simpleName + "-----onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("BaseFragment", javaClass.simpleName + "-----onResume")
        MobclickAgent.onResume(this.context)
        MobclickAgent.onPageStart(this.javaClass.simpleName)
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        Log.d("BaseFragment", javaClass.simpleName + "-----userVisibleHint----" + userVisibleHint)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("BaseFragment", javaClass.simpleName + "-----onDestroyView")
    }

    override fun onStop() {
        super.onStop()
        Log.d("BaseFragment", javaClass.simpleName + "-----onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BaseFragment", javaClass.simpleName + "-----onDestroy")
    }

    fun showLoadingView() {
        if (loadingFragment == null) {
            loadingFragment = LoadingFragment()
        }
        loadingFragment?.isCancelable = false
        if (!loadingFragment?.isAdded!!) loadingFragment?.show(childFragmentManager, "LoadingFragment")
    }

    fun hideLoadingView() {
        if (loadingFragment != null) {
            loadingFragment?.dismiss()
        }
    }
}