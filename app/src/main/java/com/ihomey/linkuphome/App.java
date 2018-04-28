package com.ihomey.linkuphome;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by WeiBinbin on 2017-11-15.
 */

public class App extends Application {

    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MultiDex.install(this);
        MobclickAgent.openActivityDurationTrack(true);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
//        Stetho.initialize(Stetho.newInitializerBuilder(this).enableDumpapp(Stetho.defaultDumperPluginsProvider(this)).enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this)).build());
    }
}
