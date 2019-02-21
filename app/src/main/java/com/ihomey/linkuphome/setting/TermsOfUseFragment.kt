package com.ihomey.linkuphome.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.R
import kotlinx.android.synthetic.main.terms_of_use_fragment.*


class TermsOfUseFragment : BaseFragment() {

    companion object {
        fun newInstance() = TermsOfUseFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.terms_of_use_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_close.setOnClickListener {  Navigation.findNavController(it).popBackStack() }
    }

}
