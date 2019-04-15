package com.ihomey.linkuphome.dl

import com.ihomey.linkuphome.device1.ConnectDeviceViewModel
import com.ihomey.linkuphome.device1.DevicesViewModel
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.inform.InformViewModel
import com.ihomey.linkuphome.room.RoomViewModel
import com.ihomey.linkuphome.room.UnBondDevicesViewModel
import com.ihomey.linkuphome.scene.SceneSettingViewModel
import com.ihomey.linkuphome.setting.SettingViewModel
import com.ihomey.linkuphome.share1.JoinZoneViewModel
import com.ihomey.linkuphome.share1.ShareZoneListViewModel
import com.ihomey.linkuphome.splash.SplashViewModel
import com.ihomey.linkuphome.zone.CreateZoneViewModel
import com.ihomey.linkuphome.zone.ZoneSettingViewModel
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

    fun inject(shareZoneListViewModel: ShareZoneListViewModel)

    fun inject(connectDeviceViewModel: ConnectDeviceViewModel)

    fun inject(devicesViewModel: DevicesViewModel)

    fun inject(unBondedDevicesViewModel: UnBondDevicesViewModel)

    fun inject(roomViewModel: RoomViewModel)

    fun inject(homeActivityViewModel: HomeActivityViewModel)

    fun inject(splashViewModel: SplashViewModel)

    fun inject(createZoneViewModel: CreateZoneViewModel)

    fun inject(joinZoneViewModel: JoinZoneViewModel)

}