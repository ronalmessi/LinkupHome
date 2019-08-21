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
import com.ihomey.linkuphome.adapter.ImageViewAdapter
import com.ihomey.linkuphome.base.LocaleHelper
import kotlinx.android.synthetic.main.instruction_detail_fragment.*


class InstructionDetailFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        BigImageViewer.initialize(GlideImageLoader.with(App.instance))
        return inflater.inflate(R.layout.instruction_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title.text=if(arguments?.getInt("type")==0)"智能床头灯操作指南" else "蓝牙MESH灯具操作指南"
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        var currentLanguage = LocaleHelper.getLanguage(context)
        currentLanguage = when {
            TextUtils.equals("zh-rCN",currentLanguage) -> "zh"
            TextUtils.equals("zh-rTW",currentLanguage) -> "zh-Hant"
            TextUtils.equals("de",currentLanguage) -> "de"
            TextUtils.equals("fr",currentLanguage) -> "fr"
            else -> "en"
        }
        val imageUrlList= mutableListOf<String>()
        for(i in 1..18){
            imageUrlList.add(AppConfig.INSTRUCTIONS_BASE_URL+currentLanguage+"/"+(if(i<10) "0"+i else ""+i)+".jpg")
        }
        val adapter = ImageViewAdapter(imageUrlList, context)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(0, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        BigImageViewer.imageLoader().cancelAll()
    }
}
