package com.ihomey.linkuphome;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.ihomey.linkuphome.base.LocaleHelper;
import com.umeng.analytics.MobclickAgent;
import java.util.Locale;


public class App extends Application {

    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MobclickAgent.openActivityDurationTrack(true);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }
}
