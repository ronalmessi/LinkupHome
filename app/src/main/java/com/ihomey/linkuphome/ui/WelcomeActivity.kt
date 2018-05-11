package com.ihomey.linkuphome.ui

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.csr.mesh.MeshService
import com.ihomey.library.base.BaseActivity
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.REQUEST_CODE_Main
import com.ihomey.linkuphome.adapter.LanguageListAdapter
import com.ihomey.linkuphome.databinding.ActivityWelcomeBinding
import com.ihomey.linkuphome.databinding.DialogLanguageSelectionBinding
import com.ihomey.linkuphome.main.MainActivity
import com.ihomey.linkuphome.widget.DividerDecoration


/**
 * Created by dongcaizheng on 2018/4/3.
 */
class WelcomeActivity : BaseActivity() {

    val languageArray: Array<String> = arrayOf("en", "zh", "fr", "de", "es")
    lateinit var mViewDataBinding: ActivityWelcomeBinding
    var dialog: BottomSheetDialog? = null
    private var mService: MeshService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        mViewDataBinding.handlers = EventHandler()
        bindService(Intent(this, MeshService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("aa", "onActivityResult==" + requestCode + "---" + resultCode)
        if (requestCode == REQUEST_CODE_Main && resultCode == Activity.RESULT_OK) {
            releaseMService()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            unbindService(mServiceConnection)
        } catch (e: Exception) {
            Log.d("LinkupHome", "oh,some error happen!")
        }
    }

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, rawBinder: IBinder) {
            mService = (rawBinder as MeshService.LocalBinder).service
            releaseMService()
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            mService = null
        }
    }

    private fun releaseMService() {
        try {
            mService?.setDeviceDiscoveryFilterEnabled(false)
            mService?.disconnectBridge()
            mService?.setHandler(null)
            mService?.setLeScanCallback(null)
        } catch (e: Exception) {

        }
    }

    inner class EventHandler : LanguageListAdapter.OnItemClickListener {

        fun onClick(view: View) {
            when (view.id) {
                R.id.welcome_iv_center -> view.context.startActivity(Intent(view.context, CenterActivity::class.java))
                R.id.welcome_tv_language -> showLanguageSelectionDialog(view)
                R.id.welcome_btn_open -> this@WelcomeActivity.startActivityForResult(Intent(this@WelcomeActivity, MainActivity::class.java), REQUEST_CODE_Main)
                R.id.language_selection_btn_cancel -> dialog?.dismiss()
            }
        }

        private fun showLanguageSelectionDialog(view: View) {
            if (dialog == null) {
                dialog = BottomSheetDialog(view.context)
                val adapter = LanguageListAdapter(view.context.resources.getStringArray(R.array.language_array))
                adapter.setOnItemClickListener(this)
                val binding = DataBindingUtil.inflate<DialogLanguageSelectionBinding>(LayoutInflater.from(view.context), R.layout.dialog_language_selection, mViewDataBinding.welcomeClContent, false)
                binding.handlers = EventHandler()
                binding.languageSelectionRcvList.layoutManager = LinearLayoutManager(view.context)
                binding.languageSelectionRcvList.adapter = adapter
                binding.languageSelectionRcvList.addItemDecoration(DividerDecoration(view.context, LinearLayoutManager.VERTICAL, true))
                dialog?.setContentView(binding.root)
                dialog?.setOnShowListener { mViewDataBinding.welcomeBtnOpen.visibility = View.GONE }
                dialog?.setOnDismissListener { mViewDataBinding.welcomeBtnOpen.visibility = View.VISIBLE }
                dialog?.window?.findViewById<FrameLayout>(R.id.design_bottom_sheet)?.setBackgroundResource(android.R.color.transparent)
            }
            dialog?.show()
        }

        override fun onItemClick(position: Int) {
            language = languageArray[position - 1]
            dialog?.dismiss()
        }
    }

}