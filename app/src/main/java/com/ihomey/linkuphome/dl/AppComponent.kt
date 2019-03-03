package com.ihomey.linkuphome.dl

import com.ihomey.linkuphome.device1.ConnectDeviceViewModel
import com.ihomey.linkuphome.device1.ConnectedDevicesViewModel
import com.ihomey.linkuphome.device1.DevicesViewModel
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.home.HomeViewModel
import com.ihomey.linkuphome.room.ChooseZoneTypeViewModel
import com.ihomey.linkuphome.room.RoomViewModel
import com.ihomey.linkuphome.room.UnBindedDevicesViewModel
import com.ihomey.linkuphome.scan.ScanViewModel
import com.ihomey.linkuphome.setting.SettingViewModel
import com.ihomey.linkuphome.share.ShareViewModel
import com.ihomey.linkuphome.share1.ShareZoneViewModel
import com.ihomey.linkuphome.splash.SplashActivityViewModel
import com.ihomey.linkuphome.zone.CreateZoneViewModel
import com.ihomey.linkuphome.zone.ZoneSettingViewModel
import com.ihomey.linkuphome.zone.ZonesViewModel
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


/**
 * Created by dongcaizheng on 2018/1/11.
 */
@Singleton
@Component(modules = [(AndroidInjectionModule::class), (ApiModule::class)])
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun build(): AppComponent
    }

    fun inject(homeViewModel: HomeViewModel)

    fun inject(connectedDevicesViewModel: ConnectedDevicesViewModel)

    fun inject(zonesViewModel: ZonesViewModel)

    fun inject(zoneSettingViewModel: ZoneSettingViewModel)

    fun inject(chooseZoneTypeViewModel: ChooseZoneTypeViewModel)

    fun inject(settingViewModel: SettingViewModel)

    fun inject(shareZoneViewModel: ShareZoneViewModel)

    fun inject(connectDeviceViewModel: ConnectDeviceViewModel)

    fun inject(devicesViewModel: DevicesViewModel)

    fun inject(unBondedDevicesViewModel: UnBindedDevicesViewModel)

    fun inject(roomViewModel: RoomViewModel)

    fun inject(homeActivityViewModel: HomeActivityViewModel)


    fun inject(splashActivityViewModel: SplashActivityViewModel)

    fun inject(createZoneViewModel: CreateZoneViewModel)




    fun inject(scanViewModel: ScanViewModel)
    fun inject(shareViewModel: ShareViewModel)
}