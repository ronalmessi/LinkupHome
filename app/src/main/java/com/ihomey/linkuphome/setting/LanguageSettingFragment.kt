package com.ihomey.linkuphome.setting

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.Language1ListAdapter
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.OnLanguageListener

import com.ihomey.linkuphome.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.language_settingg_fragment.*


class LanguageSettingFragment : Fragment(), BaseQuickAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = LanguageSettingFragment()
    }

    private lateinit var adapter: Language1ListAdapter
    private lateinit var listener: OnLanguageListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.language_settingg_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnLanguageListener
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (parentFragment?.parentFragment as HomeFragment).showBottomNavigationBar(false)
        adapter = Language1ListAdapter(R.layout.item_language_list, view.context.resources.getStringArray(R.array.language_array).toList())
        adapter.onItemClickListener = this
        rcv_language_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._1sdp)?.toInt()?.let { DividerItemDecoration(context, LinearLayoutManager.VERTICAL, it, Color.parseColor("#EFEFF0"), true) }?.let { rcv_language_list.addItemDecoration(it) }
        rcv_language_list.adapter = adapter
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        (parentFragment?.parentFragment as HomeFragment).showBottomNavigationBar(false)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val desLanguage = AppConfig.LANGUAGE[position]
        val currentLanguage = LocaleHelper.getLanguage(context)
        if (!TextUtils.equals(currentLanguage, desLanguage)) {
            listener.onLanguageChange(position)
        }
    }

}