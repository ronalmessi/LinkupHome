package com.ihomey.linkuphome.m1

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.ihomey.linkuphome.App
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import kotlinx.android.synthetic.main.instruction_fragment.*
import com.ihomey.linkuphome.adapter.ImageViewAdapter
import com.ihomey.linkuphome.adapter.M1InstructionsPageAdapter
import com.ihomey.linkuphome.base.LocaleHelper
import kotlinx.android.synthetic.main.instruction_fragment.viewPager
import kotlinx.android.synthetic.main.m1_instruction_fragment.*


class InstructionFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.m1_instruction_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter =M1InstructionsPageAdapter(childFragmentManager)
        viewPager.adapter = adapter
        dotsView.noOfPages = 3
        viewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                dotsView.onPageChange(position)
                btn_close.visibility= if(position==2) View.VISIBLE else  View.GONE
            }
        })
        btn_close.setOnClickListener { Navigation.findNavController(it).popBackStack() }
    }

}
