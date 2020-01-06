package com.ihomey.linkuphome

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.ihomey.linkuphome.listener.FragmentBackHandler
import com.pairlink.sigmesh.lib.MeshNetInfo
import com.pairlink.sigmesh.lib.PlSigMeshService
import com.pairlink.sigmesh.lib.Util
import de.keyboardsurfer.android.widget.crouton.Crouton
import org.spongycastle.crypto.digests.SHA256Digest
import org.spongycastle.util.encoders.Hex
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

fun Activity.showCrouton(message: String, bgColorRes: Int) {
    val textView = TextView(this)
    textView.width = getScreenW()
    textView.setPadding(0, dip2px(36f), 0, dip2px(18f))
    textView.gravity = Gravity.CENTER
    textView.setTextColor(resources.getColor(android.R.color.white))
    textView.setBackgroundResource(bgColorRes)
    textView.text = message
    Crouton.make(this, textView).show()
}


fun Context.toast(message: Int, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Context.toast(errorCode: String) {
    val message: String
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
        "10000" -> message = this.getString(R.string.hint_no_network)
        "10001" -> message = this.getString(R.string.hint_bad_network)
        else -> message=errorCode
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
    val fragments = fragmentManager.fragments
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


fun String.encodeBase64(): String {
    return Base64.encodeToString(toByteArray(), Base64.DEFAULT)
}

fun String.decodeBase64(): String {
    return String(Base64.decode(toByteArray(), Base64.DEFAULT))
}

fun <T> beanToJson(t: T): String {
    val mapper = ObjectMapper()
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    return AppConfig.APP_SECRET + mapper.writeValueAsString(t).replace(":", "").replace("{", "").replace("}", "").replace("[", "").replace("]", "").replace(",", "").replace("_", "").replace("\\", "").replace(",\"", "").replace("\"", "")
}


fun getHCHOLevel(hchoValue: Int): Int {
    return when {
        hchoValue <= 30 -> R.string.title_level_good
        hchoValue in 31..80 -> R.string.title_level_normal
        hchoValue in 81..160 -> R.string.title_level_pollution_lightly
        hchoValue in 161..200 -> R.string.title_level_pollution_moderately
        else -> R.string.title_level_pollution_heavily
    }
}


fun getVOCLevel(vocValue: Int): Int {
    return when (vocValue) {
        1 -> R.string.title_level_voc_good
        2 -> R.string.title_level_pollution_lightly
        3 -> R.string.title_level_pollution_moderately
        4 -> R.string.title_level_pollution_heavily
        else -> R.string.title_level_normal
    }
}

fun getPM25Level(pm25Value: Int): Int {
    return when {
        pm25Value <= 35 -> R.string.title_level_good
        pm25Value in 36..75 -> R.string.title_level_moderate
        pm25Value in 76..115 -> R.string.title_level_pollution_lightly
        pm25Value in 116..150 -> R.string.title_level_pollution_moderately
        pm25Value in 151..250 -> R.string.title_level_pollution_heavily
        else -> R.string.title_level_pollution_severely
    }
}

fun checkSum(hexData:String):String{
    var sum = 0
    var num = 0
    while (num < hexData.length) {
        val s = hexData.substring(num, num + 2)
        sum += Integer.parseInt(s, 16)
        num += 2
    }
    return Integer.toHexString(sum).takeLast(2)
}


fun getPeriodMinute(selectHour: Int, selectMinute: Int): Int {
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE)
    return if (selectHour > currentHour || selectHour == currentHour && selectMinute >= currentMinute) {
        (selectHour - currentHour) * 60 + selectMinute - currentMinute
    } else {
        (selectHour - currentHour + 24) * 60 + selectMinute - currentMinute
    }
}

fun Context.checkGPSIsOpen(): Boolean {
    val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}






