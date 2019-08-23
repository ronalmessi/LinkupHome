package com.ihomey.linkuphome

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.csr.mesh.DataModelApi
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.ihomey.linkuphome.data.entity.DeviceState
import com.ihomey.linkuphome.device.DeviceType
import com.ihomey.linkuphome.listener.FragmentBackHandler
import kotlinx.android.synthetic.main.environmental_indicators_fragment.*
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.util.encoders.Hex
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.util.*


/**
 * Created by dongcaizheng on 2018/4/9.
 */

fun Activity.setTranslucentStatus() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}

fun View.dip2px(dpValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
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

fun Context.toast(message: Int, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Context.toast(errorCode: String) {
    var message = ""
    when (errorCode) {
        "0001" -> message = "无效请求，缺少填必参数"
        "0002" -> message = "接口签名已过期"
        "0003" -> message = "接口签名错误"
        "0010" -> message = "未知客户端ID"
        "0011" -> message = "客户端唯一编号不能大于225个字符"
        "0012" -> message = "客户端类型错误"
        "0020" -> message = "未知空间ID"
        "0021" -> message = "空间名称不能大于225个字符"
        "0022" -> message = "空间类型不能大于64个字符"
        "0023" -> message = "空间邀请码已过期"
        "0024" -> message = "空间已存在"
        "0030" -> message = "未知分组ID"
        "0031" -> message = "分组名称不能大于225个字符"
        "0032" -> message = "分组类型不能大于64个字符"
        "10000" -> message = this.getString(R.string.error_network)
        "10001" -> message = "未知错误"
    }
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.hideInput(view: View) {
    val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

@SuppressLint("MissingPermission")
fun Context.getIMEI(): String {
    val telephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    return if (TextUtils.isEmpty(telephonyManager.deviceId)) "" else telephonyManager.deviceId

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


fun String.sha256(): String {
    val digest = SHA256Digest()
    digest.update(this.toByteArray(), 0, this.toByteArray().size)
    val sha256Bytes = ByteArray(digest.digestSize)
    digest.doFinal(sha256Bytes, 0)
    return Hex.toHexString(sha256Bytes)
}

fun String.md5(): String {
    val digest = MessageDigest.getInstance("MD5")
    val result = digest.digest(this.toByteArray())
    return Hex.toHexString(result)
}

fun <T> beanToJson(t: T): String {
    val mapper = ObjectMapper()
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    return AppConfig.APP_SECRET + mapper.writeValueAsString(t).replace(":", "").replace("{", "").replace("}", "").replace(",\"", "").replace("\"", "")
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


fun getShortName(type: DeviceType) =
        when (type) {
            DeviceType.C3 -> "iHomey C3"
            DeviceType.R2 -> "iHomey R2"
            DeviceType.A2 -> "iHomey A2"
            DeviceType.N1 -> "iHomey N1"
            DeviceType.V1 -> "iHomey V1"
            DeviceType.V2 -> "iHomey V2"
            DeviceType.S1 -> "iHomey S1"
            DeviceType.S2 -> "iHomey S2"
            DeviceType.M1 -> "iHomey M1"
            DeviceType.G1 -> "iHomey G1"
            DeviceType.T1 -> "iHomey T1"
        }

fun getHCHOLevel(hchoValue: Int): Int {
    return when {
        hchoValue<1000 -> R.string.title_level_normal
        hchoValue in 1000..1999 -> R.string.title_level_normal
        hchoValue in 2000..4999 -> R.string.title_level_normal
        else -> R.string.title_level_pollution_severely
    }
}


fun getVOCLevel(vocValue: Int): Int {
    return when (vocValue) {
        0 -> R.string.title_level_voc_good
        1 -> R.string.title_level_pollution_lightly
        2 -> R.string.title_level_pollution_moderately
        3 -> R.string.title_level_pollution_heavily
        else -> R.string.title_level_normal
    }
}

fun getPM25Level(pm25Value: Int): Int {
    return when {
        pm25Value<50 ->  R.string.title_level_good
        pm25Value in 50..99 -> R.string.title_level_moderate
        pm25Value in 100..149 -> R.string.title_level_pollution_lightly
        pm25Value in 150..199 -> R.string.title_level_pollution_moderately
        pm25Value in 200..299 ->  R.string.title_level_pollution_heavily
        else -> R.string.title_level_pollution_severely
    }
}

fun getMinuteList(): ArrayList<String> {
    val list = ArrayList<String>()
    for (i in 0..59) {
        if (i < 10) {
            list.add("0$i")
        } else {
            list.add("" + i)
        }
    }
    return list
}

 fun getHourList(): ArrayList<String> {
    val list = ArrayList<String>()
    for (i in 0..23) {
        if (i < 10) {
            list.add("0$i")
        } else {
            list.add("" + i)
        }
    }
    return list
}




