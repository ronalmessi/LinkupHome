package com.ihomey.linkuphome.listener


interface EmtValueListener {
    fun onEmtValueChanged(pm25Value:Int,hchoValue: Int,vocValue: Int)
}