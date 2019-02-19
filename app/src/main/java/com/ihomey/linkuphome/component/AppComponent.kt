package com.ihomey.linkuphome.component

import com.ihomey.linkuphome.device1.ConnectDeviceViewModel
import com.ihomey.linkuphome.device1.DevicesViewModel
import com.ihomey.linkuphome.device1.UnBindedDevicesViewModel
import com.ihomey.linkuphome.home.HomeActivityViewModel
import com.ihomey.linkuphome.home.HomeViewModel
import com.ihomey.linkuphome.module.ApiModule
import com.ihomey.linkuphome.scan.ScanViewModel
import com.ihomey.linkuphome.scene.SceneSettingViewModel
import com.ihomey.linkuphome.setting.SettingViewModel
import com.ihomey.linkuphome.share.ShareViewModel
import com.ihomey.linkuphome.share1.ShareZoneViewModel
import com.ihomey.linkuphome.splash.SplashActivityViewModel
import com.ihomey.linkuphome.viewmodel.MainViewModel
import com.ihomey.linkuphome.zone.*
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

    fun inject(zonesViewModel: ZonesViewModel)

    fun inject(zoneSettingViewModel: ZoneSettingViewModel)

    fun inject(chooseZoneTypeViewModel: ChooseZoneTypeViewModel)

    fun inject(settingViewModel: SettingViewModel)

    fun inject(shareZoneViewModel: ShareZoneViewModel)

    fun inject(connectDeviceViewModel: ConnectDeviceViewModel)

    fun inject(devicesViewModel: DevicesViewModel)

    fun inject(unBondedDevicesViewModel: UnBindedDevicesViewModel)

    fun inject(subZoneViewModel: SubZoneViewModel)

    fun inject(homeActivityViewModel: HomeActivityViewModel)

    fun inject(splashActivityViewModel: SplashActivityViewModel)

    fun inject(createZoneViewModel: CreateZoneViewModel)

    fun inject(mainViewModel: MainViewModel)


    fun inject(sceneSettingViewModel: SceneSettingViewModel)

    fun inject(scanViewModel: ScanViewModel)
    fun inject(shareViewModel: ShareViewModel)
}