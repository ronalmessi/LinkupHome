package com.ihomey.linkuphome.data.api

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.data.vo.RegisterVO
import com.ihomey.linkuphome.data.vo.ZoneVO
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiService {

    //用户注册
    @POST("/api/client/register")
    fun register(@Body registerVO: RegisterVO): LiveData<ApiResult<String>>


    //创建空间
    @POST("/api/space/save")
    fun createZone(@Body zoneVO: ZoneVO): LiveData<ApiResult<Boolean>>


    //创建空间
//    @FormUrlEncoded
//    @POST("/api/space/save")
//    fun createZone(@Field("guid")guid:String,@Field("name")name:String,@Field("signature")signature:String,@Field("timestamp")timestamp:Long): LiveData<ApiResult<Boolean>>




}