package com.ihomey.linkuphome.setting

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.navigation.Navigation
import com.ihomey.linkuphome.base.BaseFragment

import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.home.HomeFragment
import com.ihomey.linkuphome.listener.BottomNavigationVisibilityListener
import kotlinx.android.synthetic.main.instructions_fragment.*

class InstructionsFragment : BaseFragment() {

    companion object {
        fun newInstance() = InstructionsFragment()
        const val INSTRUCTIONS_URL = "http://ihomey.cc/guide/guide.html"
    }

    private lateinit var listener: BottomNavigationVisibilityListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.instructions_fragment, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as BottomNavigationVisibilityListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener.showBottomNavigationBar(false)
        iv_back.setOnClickListener { Navigation.findNavController(it).popBackStack() }
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
