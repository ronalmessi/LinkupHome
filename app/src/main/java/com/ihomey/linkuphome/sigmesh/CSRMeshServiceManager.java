package com.ihomey.linkuphome.sigmesh;

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
import android.util.Log;

import com.csr.mesh.MeshService;
import com.ihomey.linkuphome.data.entity.Zone;

import org.jetbrains.annotations.NotNull;

public class CSRMeshServiceManager implements Connector{

    private static final CSRMeshServiceManager ourInstance = new CSRMeshServiceManager();

    public static CSRMeshServiceManager getInstance() {
        return ourInstance;
    }

    private CSRMeshServiceManager() {

    }

    private static MeshService mService;

    private Handler mMeshHandler;


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



    private static class MeshHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MeshService.MESSAGE_LE_CONNECTED:
                    Log.d("aa",":11111");
                    break;
                case MeshService.MESSAGE_LE_DISCONNECTED:
                    Log.d("aa",":2222");
                    break;
                case MeshService.MESSAGE_DEVICE_APPEARANCE:
                    String address = msg.getData().getString(MeshService.EXTRA_DEVICE_ADDRESS);
                    String shortName = msg.getData().getString(MeshService.EXTRA_SHORTNAME);
                    int uuidHash = msg.getData().getInt(MeshService.EXTRA_UUIDHASH_31);
                    Log.d("aa",":333333---"+address+"----"+shortName+"----"+uuidHash);
                    break;
            }
        }
    }
}
