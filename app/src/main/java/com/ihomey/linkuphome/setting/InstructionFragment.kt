package com.ihomey.linkuphome.setting

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.ihomey.linkuphome.App
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import kotlinx.android.synthetic.main.instruction_fragment.*
import com.ihomey.linkuphome.adapter.ImageViewAdapter
import com.ihomey.linkuphome.base.LocaleHelper


class InstructionFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.instruction_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment?.parentFragment?.let { (it as SettingNavHostFragment).showBottomNavigationBar(false) }
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        cardView_m1_instruction.setOnClickListener {
            val bundle=Bundle()
            bundle.putInt("type",0)
            Navigation.findNavController(view).navigate(R.id.action_instructionFragment_to_instructionDetailFragment,bundle)
        }
        cardView_mesh_instruction.setOnClickListener {
            val bundle=Bundle()
            bundle.putInt("type",1)
            Navigation.findNavController(view).navigate(R.id.action_instructionFragment_to_instructionDetailFragment,bundle)
        }
    }
}
