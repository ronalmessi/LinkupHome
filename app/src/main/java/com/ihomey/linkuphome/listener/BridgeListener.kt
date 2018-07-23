package com.ihomey.linkuphome.listener

interface BridgeListener {
    fun isBridgeConnected(): Boolean
    fun connectBridge()
    fun clear()
}