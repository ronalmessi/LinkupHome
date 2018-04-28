package com.iclass.soocsecretary.component

import com.iclass.soocsecretary.module.ApiModule
import com.ihomey.linkuphome.category.LampCategoryViewModel
import com.ihomey.linkuphome.control.MeshControlViewModel
import com.ihomey.linkuphome.scan.ScanViewModel
import com.ihomey.linkuphome.scene.SceneSettingViewModel
import com.ihomey.linkuphome.share.ShareViewModel
import com.ihomey.linkuphome.time.TimerSettingViewModel
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

    fun inject(meshControlViewModel: MeshControlViewModel)

    fun inject(lampCategoryViewModel: LampCategoryViewModel)

    fun inject(sceneSettingViewModel: SceneSettingViewModel)

    fun inject(timerSettingViewModel: TimerSettingViewModel)
    fun inject(scanViewModel: ScanViewModel)
    fun inject(shareViewModel: ShareViewModel)
}