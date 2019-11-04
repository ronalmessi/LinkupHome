package com.ihomey.linkuphome.listener


interface SppStateListener {
    fun newAppearance(shortName: String, macAddress: String)
}