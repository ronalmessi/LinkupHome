package com.ihomey.linkuphome.dl

import com.ihomey.linkuphome.alarm.AlarmViewModel
import com.ihomey.linkuphome.device.ConnectDeviceViewModel
import com.ihomey.linkuphome.devicecontrol.scene.SceneSettingViewModel
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.inform.InformViewModel
import com.ihomey.linkuphome.room.RoomViewModel
import com.ihomey.linkuphome.room.UnBondDevicesViewModel
import com.ihomey.linkuphome.setting.SettingViewModel
import com.ihomey.linkuphome.splash.SplashViewModel
import com.ihomey.linkuphome.zone.ZoneSettingViewModel
import com.ihomey.linkuphome.zone.create.CreateZoneViewModel
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


/**
 * Created by dongcaizheng on 2018/1/11.
 */
@Singleton
@Component(modules = [(AndroidInjectionModule::class), (AppModule::class)])
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun build(): AppComponent
    }


    fun inject(informViewModel: InformViewModel)


    fun inject(zoneSettingViewModel: ZoneSettingViewModel)


    fun inject(settingViewModel: SettingViewModel)


    fun inject(sceneSettingViewModel: SceneSettingViewModel)


    fun inject(connectDeviceViewModel: ConnectDeviceViewModel)


    fun inject(unBondedDevicesViewModel: UnBondDevicesViewModel)

    fun inject(roomViewModel: RoomViewModel)

    fun inject(homeActivityViewModel: HomeActivityViewModel)

    fun inject(splashViewModel: SplashViewModel)

    fun inject(createZoneViewModel: CreateZoneViewModel)

    fun inject(alarmViewModel: AlarmViewModel)

}