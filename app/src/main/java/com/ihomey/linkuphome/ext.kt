package com.ihomey.linkuphome

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.databinding.BindingAdapter
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.csr.mesh.DataModelApi
import com.ihomey.linkuphome.controller.BedController.Companion.CODE_LIGHT_SYNC_TIME_BASE
import com.ihomey.linkuphome.controller.BedController.Companion.UUID_CHARACTERISTIC_WRITE
import com.ihomey.linkuphome.controller.BedController.Companion.UUID_SERVICE
import com.ihomey.linkuphome.device.DeviceType
import com.ihomey.linkuphome.listener.FragmentBackHandler
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/9.
 */

fun BottomNavigationView.disableShiftMode(iconScaleRatio: Float) {
    val menuView = this.getChildAt(0) as BottomNavigationMenuView
    try {
        val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
        shiftingMode.isAccessible = true
        shiftingMode.setBoolean(menuView, false)
        shiftingMode.isAccessible = false
        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i) as BottomNavigationItemView
            val icon = item.findViewById<ImageView>(android.support.design.R.id.icon)
            val largeLabel = item.findViewById<TextView>(android.support.design.R.id.largeLabel)
            val smallLabel = item.findViewById<TextView>(android.support.design.R.id.smallLabel)
            largeLabel.setSingleLine(false)
            smallLabel.setSingleLine(false)
            largeLabel.setLineSpacing(0f, 0.8f)
            smallLabel.setLineSpacing(0f, 0.8f)
            largeLabel.gravity = Gravity.CENTER_HORIZONTAL
            smallLabel.gravity = Gravity.CENTER_HORIZONTAL
            icon.scaleX = iconScaleRatio
            icon.scaleY = iconScaleRatio
            val baselineLayout = largeLabel.parent as android.support.design.internal.BaselineLayout
            baselineLayout.setPadding(0, 0, 0, 0)
            val layoutParams = baselineLayout.layoutParams as FrameLayout.LayoutParams
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL
            layoutParams.topMargin = this.context.dip2px(42f)
            baselineLayout.layoutParams = layoutParams
            item.setShiftingMode(false)
            item.setChecked(item.itemData.isChecked)
        }
    } catch (e: NoSuchFieldException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    }
}


fun Activity.setTranslucentStatus() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }
}


fun BleManager.write(mac: String, service: String, character: String, value: String) {
    Log.d("aa", "--" + isConnected(mac))
    if (isConnected(mac)) {
        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(mac)
        val bleDevice = BleDevice(bluetoothDevice, 0, null, 0)
        write(bleDevice, service, character, HexUtil.hexStringToBytes(value), object : BleWriteCallback() {
            override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray?) {

            }

            override fun onWriteFailure(exception: BleException?) {
                Log.d("aa", exception.toString())
            }

        })
    }
}


fun BleManager.disconnect(mac: String) {
    Log.d("aa", "--" + isConnected(mac))
    if (isConnected(mac)) {
        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(mac)
        val bleDevice = BleDevice(bluetoothDevice, 0, null, 0)
        disconnect(bleDevice)
    }
}

fun Context.dip2px(dpValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

fun Context.sp2px(spValue: Float): Int {
    val fontScale = this.resources.displayMetrics.scaledDensity;
    return (spValue * fontScale + 0.5f).toInt()
}

fun Context.getScreenW(): Int {
    return this.resources.displayMetrics.widthPixels
}

fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Context.hideInput(view: View) {
    val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun Context.saveImageToGallery(bmp: Bitmap?, dir: String) {
    if (bmp == null) return
    val appDir = File(Environment.getExternalStorageDirectory(), dir)
    if (!appDir.exists()) {
        appDir.mkdirs()
    }
    val fileName = System.currentTimeMillis().toString() + ".jpg"
    val file = File(appDir, fileName)
    try {
        val fos = FileOutputStream(file)
        bmp.compress(CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
        toast(this.resources.getString(R.string.share_img_save_success))
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        toast(this.resources.getString(R.string.share_img_save_fail))
    } catch (e: IOException) {
        e.printStackTrace()
        toast(this.resources.getString(R.string.share_img_save_fail))
    }

    // 其次把文件插入到系统图库
    try {
        MediaStore.Images.Media.insertImage(this.contentResolver, file.absolutePath, fileName, null)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }

    // 最后通知图库更新
    this.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
}

/**
 * 从控件所在位置移动到控件的底部
 *
 * @return
 */
fun moveToViewBottomAnimation(): TranslateAnimation {
    val mHiddenAction = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, 1.0f)
    mHiddenAction.duration = 150
    return mHiddenAction
}

/**
 * 从控件的底部移动到控件所在位置
 *
 * @return
 */
fun moveToViewLocationAnimation(): TranslateAnimation {
    val mShowAction = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
    mShowAction.duration = 150
    return mShowAction
}

fun handleBackPress(fragmentActivity: FragmentActivity): Boolean {
    return handleBackPress(fragmentActivity.supportFragmentManager)
}

fun handleBackPress(fragment: Fragment): Boolean {
    return handleBackPress(fragment.childFragmentManager)
}

fun handleBackPress(fragmentManager: FragmentManager): Boolean {
    val fragments = fragmentManager.fragments ?: return false
    for (i in fragments.indices.reversed()) {
        val child = fragments[i]
        if (isFragmentBackHandled(child)) {
            return true
        }
    }
    if (fragmentManager.backStackEntryCount > 0) {
        fragmentManager.popBackStack()
        return true
    }
    return false
}

fun isFragmentBackHandled(fragment: Fragment?): Boolean {
    return (fragment != null && fragment.isVisible && fragment.userVisibleHint //for ViewPager
            && fragment is FragmentBackHandler && (fragment as FragmentBackHandler).onBackPressed())
}

@BindingAdapter("android:textSize")
fun setTextSize(view: TextView, textSize: Float) {
    view.textSize = textSize
}

fun decodeHex(data: CharArray): ByteArray {

    val len = data.size

    if (len and 0x01 != 0) {
        throw RuntimeException("Odd number of characters.")
    }

    val out = ByteArray(len shr 1)

    var i = 0
    var j = 0
    while (j < len) {
        var f = toDigit(data[j], j) shl 4
        j++
        f = f or toDigit(data[j], j)
        j++
        out[i] = (f and 0xFF).toByte()
        i++
    }

    return out
}


/**
 * Convert char to byte
 * @param c char
 * *
 * @return byte
 */
private fun charToByte(c: Char): Byte {

    return "0123456789ABCDEF".indexOf(c).toByte()
}


fun toDigit(ch: Char, index: Int): Int {
    val digit = Character.digit(ch, 16)
    if (digit == -1) {
        throw RuntimeException("Illegal hexadecimal character " + ch
                + " at index " + index)
    }
    return digit
}


/**
 * 用于建立十六进制字符的输出的小写字符数组
 */
private val DIGITS_LOWER = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

/**
 * 用于建立十六进制字符的输出的大写字符数组
 */
private val DIGITS_UPPER = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')


fun encodeHexStr(data: ByteArray): String {
    return encodeHexStr(data, true)
}


fun encodeHexStr(data: ByteArray, toLowerCase: Boolean): String {
    return encodeHexStr(data, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
}


fun encodeHexStr(data: ByteArray, toDigits: CharArray): String {
    return String(encodeHex(data, toDigits))
}

fun encodeHex(data: ByteArray): CharArray {
    return encodeHex(data, true)
}

fun encodeHex(data: ByteArray, toLowerCase: Boolean): CharArray {
    return encodeHex(data, if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER)
}


fun encodeHex(data: ByteArray, toDigits: CharArray): CharArray {
    val l = data.size
    val out = CharArray(l shl 1)
    // two characters form the hex value.
    var i = 0
    var j = 0
    while (i < l) {
        out[j++] = toDigits[(0xF0 and data[i].toInt()).ushr(4)]
        out[j++] = toDigits[0x0F and data[i].toInt()]
        i++
    }
    return out
}

fun inStringArray(s: String, array: Array<String>): Boolean {
    for (x in array) {
        if (x == s) {
            return true
        }
    }
    return false
}


fun syncTime(deviceId: Int) {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val code_lawn_time_prefix = "C201F304F2" + (if (hour >= 10) "" + hour else "0$hour") + (if (minute >= 10) "" + minute else "0$minute") + (if (second >= 10) "" + second else "0$second")
    val code_check = Integer.toHexString(Integer.parseInt(code_lawn_time_prefix.substring(6, 8), 16) + Integer.parseInt(code_lawn_time_prefix.substring(8, 10), 16) + Integer.parseInt(code_lawn_time_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_time_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_time_prefix.substring(14, 16), 16))
    val code_lawn_time = code_lawn_time_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
    DataModelApi.sendData(deviceId, decodeHex(code_lawn_time.toCharArray()), false)
}


fun syncTime(mac: String) {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    val year = calendar.get(Calendar.YEAR) - 2000
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
    var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    if (dayOfWeek == 1) dayOfWeek = 7 else {
        dayOfWeek -= 1
    }
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val code_lawn_time_prefix = CODE_LIGHT_SYNC_TIME_BASE + (if (year >= 10) "" + year else "0$year") + (if (month >= 10) "" + month else "0$month") + (if (dayOfMonth >= 10) "" + dayOfMonth else "0$dayOfMonth") + (if (dayOfWeek >= 10) "" + dayOfWeek else "0$dayOfWeek") + (if (hour >= 10) "" + hour else "0$hour") + (if (minute >= 10) "" + minute else "0$minute") + (if (second >= 10) "" + second else "0$second")
    val code_check = Integer.toHexString(Integer.parseInt(code_lawn_time_prefix.substring(10, 12), 16) + Integer.parseInt(code_lawn_time_prefix.substring(12, 14), 16) + Integer.parseInt(code_lawn_time_prefix.substring(14, 16), 16) + Integer.parseInt(code_lawn_time_prefix.substring(16, 18), 16) + Integer.parseInt(code_lawn_time_prefix.substring(18, 20), 16) + Integer.parseInt(code_lawn_time_prefix.substring(20, 22), 16) + Integer.parseInt(code_lawn_time_prefix.substring(22, 24), 16) + Integer.parseInt(code_lawn_time_prefix.substring(24, 26), 16) + Integer.parseInt(code_lawn_time_prefix.substring(26, 28), 16) + Integer.parseInt(code_lawn_time_prefix.substring(28, 30), 16))
    val code_lawn_time = code_lawn_time_prefix + (if (code_check.length > 2) code_check.substring(1, code_check.length) else code_check) + "16"
    BleManager.getInstance().write(mac, UUID_SERVICE, UUID_CHARACTERISTIC_WRITE, code_lawn_time)
    Log.d("aa", "--" + code_lawn_time)
}

fun getShortName(type: DeviceType) =
        when (type) {
            DeviceType.C3 -> "iHomey C3"
            DeviceType.R2 -> "iHomey R2"
            DeviceType.A2 -> "iHomey A2"
            DeviceType.N1 -> "iHomey N1"
            DeviceType.V1 -> "iHomey V1"
        }


fun getIcon(type: Int) =
        when (type) {
            0 -> R.mipmap.lamp_icon_lawn_unadded
            1 -> R.mipmap.lamp_icon_rgb_unadded
            2 -> R.mipmap.lamp_icon_warm_cold_unadded
            3 -> R.mipmap.lamp_icon_led_unadded
            4 -> R.mipmap.lamp_icon_outdoor_unadded
            else -> R.mipmap.lamp_icon_bed_unadded
        }

//七牛后台的key
var ACCESS_KEY = "MOeoLYAGZgMIe98ZTDo_Uk4c7rYrLAD2AVFVIwC5"
//七牛后台的secret
var SECRET_KEY = "0N370nRJKrEvnkdZKzjExgYFZo1p195x_y5uUlN0"
var BUCKET_NAME = "linkuphome"
var DOMAIN = "http://oopawrxkj.bkt.clouddn.com/"
var UPDATE_URL = DOMAIN + "androidVersionInfo_"


// json keys.
val NETWORK_KEY = "networkKey"
val CURRENT_ID = "currentId"
val DEVICE_TYPE = "deviceType"
val NEXT_DEVICE_INDEX_KEY = "nextDeviceIndex"
val NEXT_GROUP_INDEX_KEY = "nextGroupIndex"
val DEVICES_KEY = "devices"
val GROUPS_KEY = "groups"
val DEVICE_ID_KEY = "deviceID"
val DEVICE_NAME_KEY = "name"
val DEVICE_HASH_KEY = "hash"
val DEVICE_MODELS_KEY = "models"
val MODEL_TYPE_KEY = "type"
val MODEL_GROUP_INSTANCES_KEY = "groupInstances"
val MODEL_GROUP_X_KEY = "groups[x]"
val GROUP_ID_KEY = "groupID"
val GROUP_NAME_KEY = "name"


val PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 100
val REQUEST_CODE_Main = 103
val REQUEST_CODE_SCAN = 101
val REQUEST_BT_RESULT_CODE = 102

val batteryIcons = intArrayOf(R.mipmap.ic_battery0, R.mipmap.ic_battery1, R.mipmap.ic_battery2, R.mipmap.ic_battery3, R.mipmap.ic_battery4, R.mipmap.ic_battery5, R.mipmap.ic_battery6)
val bgRes = arrayListOf(R.mipmap.fragment_lawn_bg, R.mipmap.fragment_rgb_bg, R.mipmap.fragment_warm_cold_bg, R.mipmap.fragment_led_bg, R.mipmap.fragment_led_bg)
val CODE_LIGHT_COLORS = arrayOf("13", "12", "14", "15", "17", "16", "01", "00", "02", "03", "05", "04", "07", "06", "08", "09", "0B", "0A", "0D", "0C", "0E", "0F", "11", "10")
val dayOfWeek = listOf("每周日", "每周一", "每周二", "每周三", "每周四", "每周五", "每周六").toMutableList()
val ringTypeNames = listOf("海浪海鸥音效", "细流声", "晨鸟之歌").toMutableList()

/**
 * LiveData that propagates only distinct emissions.
 */
fun <T> LiveData<T>.getDistinct(): LiveData<T> {
    val distinctLiveData = MediatorLiveData<T>()
    distinctLiveData.addSource(this, object : Observer<T> {
        private var initialized = false
        private var lastObj: T? = null

        override fun onChanged(obj: T?) {
            if (!initialized) {
                initialized = true
                lastObj = obj
                distinctLiveData.postValue(lastObj)
            } else if ((obj == null && lastObj != null) || obj != lastObj) {
                lastObj = obj
                distinctLiveData.postValue(lastObj)
            }
        }
    })

    return distinctLiveData
}