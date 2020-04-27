package com.ihomey.linkuphome

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.base.BaseActivity
import com.ihomey.linkuphome.base.BaseFragment
import com.ihomey.linkuphome.base.BaseNavHostFragment
import com.ihomey.linkuphome.inform.InformViewModel
import kotlinx.android.synthetic.main.webview_fragment.*

class WebViewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_fragment)
        initViews()
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        iv_back.setOnClickListener {finish() }
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.webChromeClient = WebChromeClient(webView_pb)

        intent?.getStringExtra("sourceUrl").let { webView.loadUrl(it) }
        intent?.getStringExtra("title").let { tv_title.text = it }

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
