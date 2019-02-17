package com.ihomey.linkuphome.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.navigation.Navigation
import com.ihomey.library.base.BaseFragment

import com.ihomey.linkuphome.R
import kotlinx.android.synthetic.main.instructions_fragment.*

class InstructionsFragment : BaseFragment() {

    companion object {
        fun newInstance() = InstructionsFragment()
        const val INSTRUCTIONS_URL = "http://ihomey.cc/guide/guide.html"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.instructions_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_back.setOnClickListener { Navigation.findNavController(activity!!, R.id.nav_host).popBackStack() }
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.webChromeClient = WebChromeClient(webView_pb)
        webView.loadUrl(INSTRUCTIONS_URL)
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
