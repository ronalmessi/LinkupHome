package com.ihomey.linkuphome;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;


public class App extends Application {

    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        Stetho.initializeWithDefaults(this);
        MobclickAgent.openActivityDurationTrack(true);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }
}
