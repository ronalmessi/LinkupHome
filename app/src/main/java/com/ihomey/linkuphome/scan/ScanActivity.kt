package com.ihomey.linkuphome.scan

import android.app.Activity
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Bundle
import android.os.Vibrator
import androidx.appcompat.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import com.ihomey.linkuphome.base.BaseActivity
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.databinding.ActivityScanBinding
import com.ihomey.linkuphome.scan.core.QRCodeView
import com.ihomey.linkuphome.share.ShareActivity
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder


/**
 * Created by dongcaizheng on 2017/12/21.
 */
class ScanActivity : BaseActivity(), QRCodeView.Delegate {

    private lateinit var mViewDataBinding: ActivityScanBinding
    private var lampCategoryType = -1
    private lateinit var mViewModel: ScanViewModel
    private lateinit var pullShareInfoDialog: PullShareInfoFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan)
        mViewModel = ViewModelProviders.of(this).get(ScanViewModel::class.java)
        mViewDataBinding.handlers = ScanHandler()

        mViewDataBinding.capturePreview.setDelegate(this)
//        mViewDataBinding.capturePreview.setScanBox(mViewDataBinding.captureCropLayout)

        val mAnimation = TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f)
        mAnimation.duration = 1500
        mAnimation.repeatCount = -1
        mAnimation.repeatMode = Animation.REVERSE
        mAnimation.interpolator = LinearInterpolator()
        mViewDataBinding.captureScanLine.animation = mAnimation
    }


    override fun onStart() {
        super.onStart()
        lampCategoryType = intent.getIntExtra("lampCategoryType", -1)
        mViewDataBinding.capturePreview.startCamera()
        mViewDataBinding.capturePreview.startSpot()
    }

    override fun onStop() {
        super.onStop()
        mViewDataBinding.capturePreview.stopSpot()
        mViewDataBinding.capturePreview.stopCamera()// 关闭摄像头预览，并且隐藏扫描框
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewDataBinding.capturePreview.onDestroy()// 销毁二维码扫描控件
    }

    override fun onScanQRCodeSuccess(result: String) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200L)
        if (!result.startsWith(DOMAIN)) {
            toast(getString(R.string.import_error))
            mViewDataBinding.capturePreview.startSpot()
        } else if (!isNetworkAvailable()) {
            toast(getString(R.string.network_error))
            mViewDataBinding.capturePreview.startSpot()
        } else {
            getRemoteJsonData(result)
        }
    }


    override fun onScanQRCodeOpenCameraError() {

    }


    private fun getRemoteJsonData(dataUrl: String) {
        pullShareInfoDialog = PullShareInfoFragment()
        pullShareInfoDialog.isCancelable = false
        pullShareInfoDialog.show(fragmentManager, "PullShareInfoFragment")
        HttpUrlGetTask(object : IHttpRespond {
            override fun onSuccess(t: String) {
                pullShareInfoDialog.dismiss()
                confirmReplacingDatabase(t)
            }

            override fun onFailure() {
                pullShareInfoDialog.dismiss()
                toast(getString(R.string.network_error))
                mViewDataBinding.capturePreview.startSpot()
            }

        }).execute(dataUrl)
    }


    private fun confirmReplacingDatabase(shareInfo: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.device_clear)
        builder.setPositiveButton(R.string.confirm) { _, _ ->
            val configuration = URLDecoder.decode(shareInfo, "UTF-8")
            val jsonObj = JSONObject(configuration)
            if (lampCategoryType != jsonObj.getInt(DEVICE_TYPE) - 1) {
                toast(getString(R.string.import_error))
                mViewDataBinding.capturePreview.startSpot()
            } else {
                mViewModel.udateData(jsonObj)
                val intent = Intent()
                intent.putExtra("categoryType", lampCategoryType)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        builder.setNegativeButton(R.string.cancel) { _, _ -> mViewDataBinding.capturePreview.startSpot() }
        builder.setCancelable(false)
        builder.create().show()
    }


    interface IHttpRespond {
        fun onSuccess(t: String)
        fun onFailure()
    }

    private class HttpUrlGetTask(val callback: IHttpRespond) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {
            var input: InputStream? = null
            var reader: BufferedReader? = null
            var respond: StringBuilder? = null
            try {
                val url = URL(params[0])
                val conn = url
                        .openConnection() as HttpURLConnection
                conn.readTimeout = 5000
                conn.connectTimeout = 5000
                conn.requestMethod = "GET"
                input = conn.inputStream
                reader = BufferedReader(InputStreamReader(
                        input))
                val buf = CharArray(1024)
                respond = StringBuilder()
                var i: Int
                do {
                    i = reader.read(buf)
                    if (i == -1) break
                    respond.append(buf, 0, i)
                } while (true)
                conn.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            } finally {
                if (input != null && reader != null) {
                    closeIO(input, reader)
                }
            }
            return respond.toString()
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            if (TextUtils.isEmpty(result)) {
                callback.onFailure()
            } else {
                callback.onSuccess(result)
            }
        }

        /**
         * 关闭流
         *
         * @param closeables
         */
        fun closeIO(vararg closeables: Closeable) {
            if (closeables.isEmpty()) {
                return
            }
            for (cb in closeables) {
                try {
                    if (null == cb) {
                        continue
                    }
                    cb.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }


    inner class ScanHandler {
        fun onClick(view: View) {
            when (view.id) {
                R.id.toolbar_back -> this@ScanActivity.finish()
                R.id.tv_myQRCodeView -> {
                    val intent = Intent(this@ScanActivity, ShareActivity::class.java)
                    intent.putExtra("lampCategoryType", lampCategoryType)
                    startActivity(intent)
                }
            }
        }
    }
}