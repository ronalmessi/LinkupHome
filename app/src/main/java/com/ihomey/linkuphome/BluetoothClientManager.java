package com.ihomey.linkuphome;

import android.app.Application;

import com.inuker.bluetooth.library.BluetoothClient;

public class BluetoothClientManager {

    private Application context;

    private BluetoothClient mClient;

    private static final BluetoothClientManager ourInstance = new BluetoothClientManager();

    public static BluetoothClientManager getInstance() {
        return ourInstance;
    }

    private BluetoothClientManager() {

    }

    public void init(Application app) {
        if (context == null && app != null) {
            context = app;
            mClient=new BluetoothClient(app);
        }
    }

    public BluetoothClient getClient() {
        return mClient;
    }
}
