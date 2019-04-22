package com.ihomey.linkuphome;

import android.app.Application;
import android.content.Context;
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

    @Override
    protected void attachBaseContext(Context base) {
        String currentLanguage = Locale.getDefault().getDisplayLanguage().toLowerCase();
        switch (currentLanguage) {
            case "español":
                super.attachBaseContext(LocaleHelper.onAttach(base, "es"));
                break;
            case "deutsch":
                super.attachBaseContext(LocaleHelper.onAttach(base, "de"));
                break;
            case "français":
                super.attachBaseContext(LocaleHelper.onAttach(base, "fr"));
                break;
            case "nederlands":
                super.attachBaseContext(LocaleHelper.onAttach(base, "nl"));
                break;
            case "中文":
                super.attachBaseContext(LocaleHelper.onAttach(base, "zh"));
                break;
            default:
                super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
                break;
        }
    }
}
