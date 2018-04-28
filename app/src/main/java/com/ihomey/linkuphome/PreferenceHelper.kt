package com.iclass.soocsecretary.util

import android.content.Context
import com.ihomey.linkuphome.App
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by dongcaizheng on 2018/1/16.
 */
class PreferenceHelper<T>(val name: String, private val defaultValue: T) : ReadWriteProperty<Any?, T> {

    private val mSharedPreferences by lazy { App.instance.getSharedPreferences("LinkupHome", Context.MODE_PRIVATE) }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(name, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    private fun <T> findPreference(name: String, default: T): T = with(mSharedPreferences) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }
        res as T
    }

    private fun <T> putPreference(name: String, value: T) = with(mSharedPreferences.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }.apply()
    }

    fun clearPreference() {
        mSharedPreferences.edit().clear().commit()
    }

    fun deletePreference(key: String) {
        mSharedPreferences.edit().remove(key).commit()
    }
}