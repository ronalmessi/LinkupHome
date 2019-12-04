package com.ihomey.linkuphome.sigmesh;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.csr.mesh.MeshService;
import com.ihomey.linkuphome.data.entity.Device;
import com.ihomey.linkuphome.data.entity.Zone;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CSRMeshServiceManager implements Connector{

    private static final CSRMeshServiceManager ourInstance = new CSRMeshServiceManager();

    public static CSRMeshServiceManager getInstance() {
        return ourInstance;
    }

    private CSRMeshServiceManager() {

    }

    private  MeshService mService;

    private Handler mMeshHandler;

    private  Boolean mConnected = false;

    private  MeshDeviceScanListener meshDeviceScanListener;

    public void setMeshDeviceScanListener(MeshDeviceScanListener meshDeviceScanListener) {
        this.meshDeviceScanListener = meshDeviceScanListener;
    }

    @Override
    public void bind(@NotNull Activity activity) {
        mMeshHandler=new MeshHandler();
        activity.bindService(new Intent(activity, MeshService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void unBind(@NotNull Activity activity) {
        activity.unbindService(mServiceConnection);
    }

    @Override
    public void initService(@NotNull Zone zone) {
       mService.setNextDeviceId(zone.getNextDeviceIndex());
       mService.setNetworkPassPhrase(zone.getNetWorkKey());
    }

    @Override
    public void startScan() {
        mService.setDeviceDiscoveryFilterEnabled(true);
    }

    @Override
    public void stopScan() {
        mService.setDeviceDiscoveryFilterEnabled(false);
    }

    @Override
    public void connect() {

    }

    @Override
    public boolean isConnected() {
        return mConnected;
    }


    private ServiceConnection mServiceConnection=new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder rawBinder) {
            mService = ((MeshService.LocalBinder)rawBinder).getService();
            mService.setHandler(mMeshHandler);
            mService.setLeScanCallback(mScanCallBack);
            mService.setMeshListeningMode(true, true);
            mService.autoConnect(1, 10000, 100, 0);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };


    private BluetoothAdapter.LeScanCallback mScanCallBack=new BluetoothAdapter.LeScanCallback(){

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            mService.processMeshAdvert(device, scanRecord, rssi);
        }
    };


    private int getDeviceTypeByShortName(String shortName){
        switch (shortName){
            case "iHomey C3":
                return 1;
            case "iHomey R2":
                return 2;
            case "iHomey A2":
                return 3;
            case "iHomey N1":
                return 4;
            case "iHomey V1":
                return 6;
            case "iHomey S1":
                return 7;
            case "iHomey S2":
                return 8;
            case "iHomey T1":
                return 9;
            case "iHomey V2":
                return 10;
        }
        return 1;
    }

    @SuppressLint("HandlerLeak")
    private class MeshHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MeshService.MESSAGE_LE_CONNECTED:
                    mConnected=true;
                    Log.d("aa",":11111");
                    break;
                case MeshService.MESSAGE_LE_DISCONNECTED:
                    mConnected=false;
                    Log.d("aa",":2222");
                    break;
                case MeshService.MESSAGE_DEVICE_APPEARANCE:
                    String shortName = msg.getData().getString(MeshService.EXTRA_SHORTNAME);
                    int uuidHash = msg.getData().getInt(MeshService.EXTRA_UUIDHASH_31);
                    if(!TextUtils.isEmpty(shortName)&&meshDeviceScanListener!=null&&shortName!=null){
                        Device device=new Device(getDeviceTypeByShortName(shortName),shortName.substring(shortName.length()-2));
                        device.setHash(uuidHash+"");
                        meshDeviceScanListener.onDeviceFound(device);
                    }
                    break;
            }
        }
    }
}
