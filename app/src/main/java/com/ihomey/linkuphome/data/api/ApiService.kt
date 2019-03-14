package com.ihomey.linkuphome.data.api

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.*
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {

    //用户注册
    @POST("/api/client/register")
    fun register(@Body registerVO: RegisterVO): LiveData<ApiResult<String>>

    //创建空间
    @POST("/api/space/save")
    fun createZone(@Body createZoneVO: CreateZoneVO): LiveData<ApiResult<Zone>>

    //修改空间名称
    @POST("/api/space/save")
    fun changeZoneName(@Body changeZoneNameVO: ChangeZoneNameVO): LiveData<ApiResult<Zone>>

    //删除空间
    @POST("/api/space/delete")
    fun deleteZone(@Body deleteZoneVO: DeleteZoneVO): LiveData<ApiResult<Boolean>>

    //分享空间
    @POST("/api/space/share")
    fun shareZone(@Body deleteZoneVO: DeleteZoneVO): LiveData<ApiResult<String>>

    //加入空间
    @POST("/api/space/join")
    fun joinZone(@Body joinZoneVO: JoinZoneVO): LiveData<ApiResult<Zone>>

    //获取当前空间的详细信息
    @POST("/api/space")
    fun getCurrentZone(@Body baseVO: BaseVO): LiveData<ApiResult<Zone>>

    //获取空间列表
    @POST("/api/spaces")
    fun getZones(@Body baseVO: BaseVO): LiveData<ApiResult<List<Zone>>>


    //创建空间
//    @FormUrlEncoded
//    @POST("/api/space/save")
//    fun createZone(@Field("guid")guid:String,@Field("name")name:String,@Field("signature")signature:String,@Field("timestamp")timestamp:Long): LiveData<ApiResult<Boolean>>




}