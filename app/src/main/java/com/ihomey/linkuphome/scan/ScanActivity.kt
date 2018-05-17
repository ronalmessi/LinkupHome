package com.ihomey.linkuphome.scan

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import com.ihomey.library.base.BaseActivity
import com.ihomey.linkuphome.*
import com.ihomey.linkuphome.databinding.ActivityScanBinding
import com.ihomey.linkuphome.scan.camera.CameraManager
import com.ihomey.linkuphome.scan.decode.CaptureActivityHandler
import com.ihomey.linkuphome.scan.decode.InactivityTimer
import com.ihomey.linkuphome.share.ShareActivity
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder


/**
 * Created by dongcaizheng on 2017/12/21.
 */
class ScanActivity : BaseActivity(), SurfaceHolder.Callback {

    private lateinit var mViewDataBinding: ActivityScanBinding
    var handler: CaptureActivityHandler? = null
    private var hasSurface: Boolean = false
    private var inactivityTimer: InactivityTimer? = null
    private var lampCategoryType = -1

    private lateinit var mViewModel: ScanViewModel
    private lateinit var pullShareInfoDialog: PullShareInfoFragment

    var x = 0
    var y = 0
    var cropWidth = 0
    var cropHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_scan)
        mViewModel = ViewModelProviders.of(this).get(ScanViewModel::class.java)
        mViewDataBinding.handlers = ScanHandler()

        // 初始化 CameraManager
        CameraManager.init(application)
        hasSurface = false
        inactivityTimer = InactivityTimer(this)

        val mAnimation = TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f)
        mAnimation.duration = 1500
        mAnimation.repeatCount = -1
        mAnimation.repeatMode = Animation.REVERSE
        mAnimation.interpolator = LinearInterpolator()
        mViewDataBinding.captureScanLine.animation = mAnimation
    }


    override fun onResume() {
        super.onResume()
        lampCategoryType = intent.getIntExtra("lampCategoryType", -1)
        val surfaceHolder = mViewDataBinding.capturePreview.holder
        if (hasSurface) {
            initCamera(surfaceHolder)
        } else {
            surfaceHolder.addCallback(this)
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }

    override fun onPause() {
        super.onPause()
        if (handler != null) {
            handler?.quitSynchronously()
            handler = null
        }
        CameraManager.get().closeDriver()
    }

    override fun onDestroy() {
        inactivityTimer?.shutdown()
        super.onDestroy()
    }

    private fun initCamera(surfaceHolder: SurfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder)

            val point = CameraManager.get().cameraResolution
            val width = point.y
            val height = point.x

            x = mViewDataBinding.captureCropLayout.left * width / mViewDataBinding.captureContainter.width
            y = mViewDataBinding.captureCropLayout.top * height / mViewDataBinding.captureContainter.height
            cropWidth = mViewDataBinding.captureCropLayout.width * width / mViewDataBinding.captureContainter.width
            cropHeight = mViewDataBinding.captureCropLayout.height * height / mViewDataBinding.captureContainter.height


        } catch (ioe: IOException) {
            return
        } catch (e: RuntimeException) {
            return
        }

        if (handler == null) {
            handler = CaptureActivityHandler(this)
        }
    }


    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        hasSurface = false
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!hasSurface) {
            hasSurface = true
            initCamera(holder)
        }
    }

    fun handleDecode(result: String) {
        inactivityTimer?.onActivity()
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200L)
        if (!result.startsWith(DOMAIN)) {
            toast(getString(R.string.import_config_error))
        } else if (!isNetworkAvailable()) {
            toast(getString(R.string.network_error))
        } else {
            getRemoteJsonData(result)
        }
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
            }

        }).execute(dataUrl)
    }


    private fun confirmReplacingDatabase(shareInfo: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.devices_clear)
        builder.setPositiveButton(R.string.confirm) { _, _ ->
            val configuration = URLDecoder.decode(shareInfo, "UTF-8")
            val jsonObj = JSONObject(configuration)
            if (lampCategoryType != jsonObj.getInt(DEVICE_TYPE) - 1) {
                toast(getString(R.string.import_config_error))
                handler?.sendEmptyMessage(R.id.restart_preview)
            } else {
                mViewModel.udateData(jsonObj)
                val intent = Intent()
                intent.putExtra("categoryType", lampCategoryType)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        builder.setNegativeButton(R.string.cancel, null)
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
                R.id.tv_myQRCodeView -> view.context.startActivity(Intent(this@ScanActivity, ShareActivity::class.java))
            }
        }
    }
}