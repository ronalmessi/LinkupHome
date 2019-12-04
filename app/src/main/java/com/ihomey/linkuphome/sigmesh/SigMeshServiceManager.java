package com.ihomey.linkuphome.sigmesh;



import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.ihomey.linkuphome.data.entity.Zone;
import com.pairlink.sigmesh.lib.MeshNetInfo;
import com.pairlink.sigmesh.lib.PlSigMeshProvisionCallback;
import com.pairlink.sigmesh.lib.PlSigMeshProxyCallback;
import com.pairlink.sigmesh.lib.PlSigMeshService;
import com.pairlink.sigmesh.lib.Util;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class SigMeshServiceManager implements Connector {

    private static final SigMeshServiceManager ourInstance = new SigMeshServiceManager();

    public static SigMeshServiceManager getInstance() {
        return ourInstance;
    }

    private SigMeshServiceManager() {

    }

    private WeakReference<Activity> mActivity;
    private PlSigMeshService mPlSigMeshService;
    private MeshNetInfo mPlSigMeshNet;


    @Override
    public void bind(@NotNull Activity activity) {
        this.mActivity= new WeakReference<>(activity);
        activity.bindService(new Intent(activity,PlSigMeshService.class), mPlSigMeshServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void unBind(@NotNull Activity activity) {
        activity.unbindService(mPlSigMeshServiceConnection);
    }


    @Override
    public void connect() {

    }

    @Override
    public void initService(@NotNull Zone zone) {
        Log.d("aa", "222222");
        mPlSigMeshNet = PlSigMeshService.getInstance().chooseMeshNet(0);
        mPlSigMeshService.scanDevice(true, Util.SCAN_TYPE_PROXY);
        mPlSigMeshService.registerProxyCb(mSigMeshProxyCB);
        mPlSigMeshService.registerProvisionCb(mSigMeshProvisionCB);
        mPlSigMeshService.proxyJoin();
    }

    @Override
    public void startScan() {
        mPlSigMeshService.proxyExit();
        mPlSigMeshService.scanDevice(true, Util.SCAN_TYPE_PROVISION);
    }

    @Override
    public void stopScan() {
        mPlSigMeshService.scanDevice(true, Util.SCAN_TYPE_PROXY);
        mPlSigMeshService.proxyJoin();
    }

    private ServiceConnection mPlSigMeshServiceConnection=new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder rawBinder) {
            mPlSigMeshService = ((PlSigMeshService.LocalBinder)rawBinder).getService();
            mPlSigMeshService.init(mActivity.get(),Util.DBG_LEVEL_DBG, Util.DBG_LEVEL_DBG);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlSigMeshService = null;
        }
    };

    private PlSigMeshProxyCallback mSigMeshProxyCB =new PlSigMeshProxyCallback() {

        @Override
        public void onMeshStatus(int status, String addr) {
            super.onMeshStatus(status, addr);
            switch (status){
                case  Util.PL_MESH_READY :
                    Log.d("aa", "111");
                    break;
                case  Util.PL_MESH_JOIN_FAIL:
                    Log.d("aa", "222");
                    break;
                case  Util.PL_MESH_EXIT :
                    Log.d("aa", "333");
                    break;
            }
        }

        @Override
        public void onConfigComplete(int result, MeshNetInfo.MeshNodeInfo config_node, MeshNetInfo mesh_net) {
            super.onConfigComplete(result, config_node, mesh_net);
        }

        @Override
        public void onNodeResetStatus(short src) {
            super.onNodeResetStatus(src);
        }
    };

    private PlSigMeshProvisionCallback mSigMeshProvisionCB = new PlSigMeshProvisionCallback() {
        @Override
        public void onDeviceFoundUnprovisioned(BluetoothDevice device, int rssi, String uuid) {
            super.onDeviceFoundUnprovisioned(device, rssi, uuid);
            Log.d("aa",device.getName()+"---"+device.getAddress());
        }

        @Override
        public void onProvisionComplete(int result, MeshNetInfo.MeshNodeInfo provision_node, MeshNetInfo mesh_net) {
            super.onProvisionComplete(result, provision_node, mesh_net);
        }
    };

}
