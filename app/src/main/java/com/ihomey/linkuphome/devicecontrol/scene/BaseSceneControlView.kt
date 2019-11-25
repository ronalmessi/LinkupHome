package com.ihomey.linkuphome.devicecontrol.scene

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.ihomey.linkuphome.data.entity.LocalState

abstract class BaseSceneControlView(context: Context, layoutId:Int) {

    private var controlView= LayoutInflater.from(context).inflate(layoutId,null)

    fun getControlView():View{
        return controlView
    }

    abstract fun bindTo(localState: LocalState?)

    private var listener:OnSceneChangedListener?=null

    fun setOnSceneChangListener(listener:OnSceneChangedListener?){
        this.listener=listener
    }

    fun getOnSceneChangListener():OnSceneChangedListener?{
        return listener
    }

}


