package com.ihomey.linkuphome;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.clj.fastble.BleManager;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.facebook.stetho.Stetho;
import com.ihomey.linkuphome.base.LocaleHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.Locale;


public class App extends Application {

    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MultiDex.install(this);
        MobclickAgent.openActivityDurationTrack(true);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        Stetho.initialize(Stetho.newInitializerBuilder(this).enableDumpapp(Stetho.defaultDumperPluginsProvider(this)).enableWebKitInspector(
                Stetho.defaultInspectorModulesProvider(this)).build());

        BleManager.getInstance().init(this);
        BleManager.getInstance().enableLog(BuildConfig.DEBUG).setReConnectCount(1, 5000).setOperateTimeout(5000);
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder().setDeviceName(true, new String[]{"Linkuphome C3", "Linkuphome R2", "Linkuphome A2", "Linkuphome N1", "Linkuphome M1"}).setScanTimeOut(10000).build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
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
            case "中文":
                super.attachBaseContext(LocaleHelper.onAttach(base, "zh"));
                break;
            default:
                super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
                break;
        }
    }
}
