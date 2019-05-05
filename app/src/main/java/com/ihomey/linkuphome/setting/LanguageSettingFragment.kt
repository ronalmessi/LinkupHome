package com.ihomey.linkuphome.setting

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ihomey.linkuphome.AppConfig
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.adapter.LanguageListAdapter
import com.ihomey.linkuphome.base.LocaleHelper
import com.ihomey.linkuphome.listener.OnLanguageListener
import com.ihomey.linkuphome.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.language_setting_fragment.*


class LanguageSettingFragment : Fragment(), BaseQuickAdapter.OnItemClickListener {

    companion object {
        fun newInstance() = LanguageSettingFragment()
    }

    private lateinit var adapter: LanguageListAdapter
    private lateinit var onLanguageListener: OnLanguageListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.language_setting_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onLanguageListener = context as OnLanguageListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment?.parentFragment?.let { (it as SettingNavHostFragment).showBottomNavigationBar(false) }
        adapter = LanguageListAdapter(R.layout.item_language_list, view.context.resources.getStringArray(R.array.language_array).toList())
        adapter.onItemClickListener = this
        rcv_language_list.layoutManager = LinearLayoutManager(context)
        context?.resources?.getDimension(R.dimen._1sdp)?.toInt()?.let { DividerItemDecoration(LinearLayoutManager.HORIZONTAL, context?.resources?.getDimension(R.dimen._27sdp)?.toInt()!!,it, Color.parseColor("#EFEFF0"), true) }?.let { rcv_language_list.addItemDecoration(it) }
        rcv_language_list.adapter = adapter
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
        val desLanguage = AppConfig.LANGUAGE[position]
        val currentLanguage = LocaleHelper.getLanguage(context)
        if (!TextUtils.equals(currentLanguage, desLanguage)) {
            onLanguageListener.onLanguageChange(position)
        }
    }
}
