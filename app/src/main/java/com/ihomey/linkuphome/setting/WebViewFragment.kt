package com.ihomey.linkuphome.setting

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseFragment
import kotlinx.android.synthetic.main.webview_fragment.*

class WebViewFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.webview_fragment, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        parentFragment?.parentFragment?.let { if (it is SettingNavHostFragment) it.showBottomNavigationBar(false) }
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.webChromeClient = WebChromeClient(webView_pb)

        arguments?.getString("sourceUrl").let { webView.loadUrl(it) }
        arguments?.getString("title").let { tv_title.text = it }

    }

    class WebChromeClient(private val progressBar: ProgressBar) : android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if (newProgress == 100) {
                progressBar.visibility = View.GONE
            } else {
                progressBar.visibility = View.VISIBLE
                progressBar.progress = newProgress
            }
        }
    }
}
