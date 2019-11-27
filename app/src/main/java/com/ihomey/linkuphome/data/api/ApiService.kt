package com.ihomey.linkuphome.data.api

import androidx.lifecycle.LiveData
import com.ihomey.linkuphome.data.entity.Device
import com.ihomey.linkuphome.data.entity.Room
import com.ihomey.linkuphome.data.entity.Zone
import com.ihomey.linkuphome.data.vo.*
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {

    //创建空间
    @POST("/api/space/save")
    fun createZone(@Body createZoneVO: CreateZoneVO): LiveData<ApiResult<Zone>>

    //更新meshInfo
    @POST("/api/space/save")
    fun uploadMeshInfo(@Body uploadMeshInfoVO: UploadMeshInfoVO): LiveData<ApiResult<Zone>>

    //修改空间名称
    @POST("/api/space/save")
    fun changeZoneName(@Body changeZoneNameVO: ChangeZoneNameVO): LiveData<ApiResult<Zone>>

    //删除空间
    @POST("/api/space/delete")
    fun deleteZone(@Body deleteVO: DeleteVO): LiveData<ApiResult<Int>>

    //分享空间
    @POST("/api/space/share")
    fun shareZone(@Body deleteVO: DeleteVO): LiveData<ApiResult<String>>

    //切换空间
    @POST("/api/space/change")
    fun switchZone(@Body deleteVO: DeleteVO): LiveData<ApiResult<ZoneDetail>>

    //加入空间
    @POST("/api/space/join")
    fun joinZone(@Body joinZoneVO: JoinZoneVO): LiveData<ApiResult<ZoneDetail>>

    //获取当前空间的详细信息
    @POST("/api/space")
    fun getCurrentZone(@Body baseVO: BaseVO): LiveData<ApiResult<ZoneDetail>>

    //获取当前空间的详细信息
    @POST("/api/space")
    fun getZone(@Body deleteVO: DeleteVO): LiveData<ApiResult<ZoneDetail>>

    //获取空间列表
    @POST("/api/spaces")
    fun getZones(@Body baseVO: BaseVO): LiveData<ApiResult<List<Zone>>>


    //添加设备
    @POST("/api/device/save")
    fun saveDevice(@Body saveDeviceVO: SaveDeviceVO): LiveData<ApiResult<Device>>

    //删除设备
    @POST("/api/device/delete")
    fun deleteDevice(@Body deleteVO: DeleteDeviceVO): LiveData<ApiResult<Boolean>>

    //修改设备名称
    @POST("/api/device/save")
    fun changeDeviceName(@Body saveDeviceVO: SaveDeviceVO): LiveData<ApiResult<Device>>

    //修改设备状态
    @POST("/api/device/handling")
    fun changeDeviceState(@Body changeDeviceStateVO: ChangeDeviceStateVO): LiveData<ApiResult<Device>>


    //添加房间
    @POST("/api/group/save")
    fun saveRoom(@Body saveRoomVO: SaveRoomVO): LiveData<ApiResult<Room>>

    //删除房间
    @POST("/api/group/delete")
    fun deleteRoom(@Body deleteVO: DeleteVO): LiveData<ApiResult<Boolean>>

    //修改房间名称
    @POST("/api/group/save")
    fun changeRoomName(@Body changeDeviceNameVO: ChangeDeviceNameVO): LiveData<ApiResult<Room>>

    //添加设备到房间
    @POST("/api/group/device")
    fun bindDevice(@Body bindDeviceVO: BindDeviceVO): LiveData<ApiResult<Room>>

    //修改设备状态
    @POST("/api/group/handling")
    fun changeRoomState(@Body changeDeviceStateVO: ChangeDeviceStateVO): LiveData<ApiResult<Room>>

}