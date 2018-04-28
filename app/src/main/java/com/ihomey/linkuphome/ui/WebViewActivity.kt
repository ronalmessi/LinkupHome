package com.ihomey.linkuphome.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import com.ihomey.library.base.BaseActivity
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.databinding.ActivityWebviewBinding
import com.ihomey.linkuphome.listener.EventHandler

/**
 * Created by dongcaizheng on 2017/12/21.
 */
class WebViewActivity : BaseActivity() {

    companion object {
        const val INSTRUCTIONS_URL = "http://ihomey.cc/guide/guide.html"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityWebviewBinding>(this, R.layout.activity_webview)
        binding.handlers = EventHandler()
        binding.webView.settings.setSupportZoom(true)
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.displayZoomControls = false
        binding.webView.webChromeClient = WebChromeClient(binding.webViewPb)
        binding.webView.loadUrl(INSTRUCTIONS_URL)
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