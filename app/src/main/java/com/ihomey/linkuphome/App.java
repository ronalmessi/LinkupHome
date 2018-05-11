package com.ihomey.linkuphome;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.clj.fastble.BleManager;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.facebook.stetho.Stetho;
import com.umeng.analytics.MobclickAgent;


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
}
