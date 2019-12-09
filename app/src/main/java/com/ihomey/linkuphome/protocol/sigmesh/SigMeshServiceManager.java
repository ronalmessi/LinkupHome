package com.ihomey.linkuphome.protocol.sigmesh;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.ihomey.linkuphome.AppConfig;
import com.ihomey.linkuphome.data.entity.Device;
import com.ihomey.linkuphome.data.entity.Zone;
import com.pairlink.sigmesh.lib.MeshNetInfo;
import com.pairlink.sigmesh.lib.PlSigMeshProvisionCallback;
import com.pairlink.sigmesh.lib.PlSigMeshProxyCallback;
import com.pairlink.sigmesh.lib.PlSigMeshService;
import com.pairlink.sigmesh.lib.Util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

import static com.ihomey.linkuphome.ExtKt.createPlSigMeshNet;

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
    private Boolean mConnected = false;

    private Boolean isInited = false;

    private MeshDeviceScanListener meshDeviceScanListener;
    private MeshStateListener meshStateListener;
    private MeshDeviceAssociateListener meshDeviceAssociateListener;
    private MeshDeviceRemoveListener meshDeviceRemoveListener;
    private MeshInfoListener meshInfoListener;


    public Boolean isInited() {
        return isInited;
    }

    public PlSigMeshService getPlSigMeshService() {
        return mPlSigMeshService;
    }

    public void setMeshInfoListener(MeshInfoListener meshInfoListener) {
        this.meshInfoListener = meshInfoListener;
    }

    public void setMeshStateListener(MeshStateListener meshStateListener) {
        this.meshStateListener = meshStateListener;
    }

    public void setMeshDeviceScanListener(MeshDeviceScanListener meshDeviceScanListener) {
        this.meshDeviceScanListener = meshDeviceScanListener;
    }

    @Override
    public void bind(@NotNull Activity activity) {
        this.mActivity = new WeakReference<>(activity);
        activity.bindService(new Intent(activity, PlSigMeshService.class), mPlSigMeshServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void unBind(@NotNull Activity activity) {
        isInited=false;
        mPlSigMeshService.scanDevice(false, Util.SCAN_TYPE_PROXY);
        mPlSigMeshService.proxyExit();
        activity.unbindService(mPlSigMeshServiceConnection);
    }

    @Override
    public void initService(@NotNull Zone zone) {
        if(TextUtils.isEmpty(zone.getMeshInfo())) createPlSigMeshNet();
        isInited=true;
        mPlSigMeshNet = PlSigMeshService.getInstance().chooseMeshNet(0);
        mPlSigMeshService.scanDevice(true, Util.SCAN_TYPE_PROXY);
        mPlSigMeshService.registerProxyCb(mSigMeshProxyCB);
        mPlSigMeshService.registerProvisionCb(mSigMeshProvisionCB);
        Log.d("aa","proxyJoin----");
        mPlSigMeshService.proxyJoin();
    }

    public void release(){
        isInited=false;
        mPlSigMeshService.scanDevice(false, Util.SCAN_TYPE_PROXY);
        mPlSigMeshService.proxyExit();
        mPlSigMeshService.deleteMeshNet(0);
    }

    @Override
    public void startScan() {
//        mPlSigMeshService.proxyExit();
        mPlSigMeshService.scanDevice(true, Util.SCAN_TYPE_PROVISION);
    }

    @Override
    public void stopScan() {
        mPlSigMeshService.scanDevice(true, Util.SCAN_TYPE_PROXY);
//        mPlSigMeshService.proxyJoin();
    }

    @Override
    public boolean isConnected() {
        return mConnected;
    }

    private ServiceConnection mPlSigMeshServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder rawBinder) {
            Log.d("aa","onServiceConnected----mPlSigMeshService");
            mPlSigMeshService = ((PlSigMeshService.LocalBinder) rawBinder).getService();
            mPlSigMeshService.init(mActivity.get(), Util.DBG_LEVEL_DBG, Util.DBG_LEVEL_DBG);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlSigMeshService = null;
        }
    };

    private PlSigMeshProxyCallback mSigMeshProxyCB = new PlSigMeshProxyCallback() {

        @Override
        public void onMeshStatus(int status, String addr) {
            super.onMeshStatus(status, addr);
            Log.d("aa",status+"----");
            switch (status) {
                case Util.PL_MESH_READY:
                    mConnected = true;
                    mActivity.get().runOnUiThread(() -> {
                        if (meshStateListener != null)
                            meshStateListener.onDeviceStateChanged("SigMesh V1",true);
                    });

                    break;
                case Util.PL_MESH_JOIN_FAIL:
                case Util.PL_MESH_EXIT:
                    mConnected = false;
                    mActivity.get().runOnUiThread(() -> {
                        if (meshStateListener != null)
                            meshStateListener.onDeviceStateChanged("SigMesh V1",false);
                    });
                    break;
            }
        }

        @Override
        public void onConfigComplete(int result, MeshNetInfo.MeshNodeInfo config_node, MeshNetInfo mesh_net) {
            super.onConfigComplete(result, config_node, mesh_net);
            mPlSigMeshNet = mesh_net;
            mActivity.get().runOnUiThread(() -> {
                if(meshDeviceAssociateListener!=null){
                    meshDeviceAssociateListener.associationProgress(95);
                    meshDeviceAssociateListener.deviceAssociated(config_node.getPrimary_addr(),0,config_node.getUuid());
                }
            });
        }

        @Override
        public void onNodeResetStatus(short src) {
            super.onNodeResetStatus(src);
            mPlSigMeshService.delMeshNode(src);
            mActivity.get().runOnUiThread(() -> {
                if(meshInfoListener!=null){
                    meshInfoListener.onMeshInfoChanged();
                }
            });
        }
    };

    private PlSigMeshProvisionCallback mSigMeshProvisionCB = new PlSigMeshProvisionCallback() {
        @Override
        public void onDeviceFoundUnprovisioned(BluetoothDevice device, int rssi, String uuid) {
            super.onDeviceFoundUnprovisioned(device, rssi, uuid);
            Log.d("aa",device.getName());
            if (!TextUtils.isEmpty(device.getName()) && meshDeviceScanListener != null) {
                String deviceName = device.getName();
                Device singleDevice = new Device(6, deviceName.substring(deviceName.length() - 2));
                singleDevice.setHash(uuid);
                singleDevice.setMacAddress(device.getAddress());
                meshDeviceScanListener.onDeviceFound(singleDevice);
            }
        }

        @Override
        public void onProvisionComplete(int result, MeshNetInfo.MeshNodeInfo provision_node, MeshNetInfo mesh_net) {
            super.onProvisionComplete(result, provision_node, mesh_net);
            mPlSigMeshNet = mesh_net;
            if ((int)Util.CONFIG_MODE_PROVISION_CONFIG_ONE_BY_ONE==  mPlSigMeshService.get_config_mode() && result == 0) {
                mActivity.get().runOnUiThread(() -> {
                    if(meshDeviceAssociateListener!=null) meshDeviceAssociateListener.associationProgress(50);
                });
            }
        }
    };

    @Override
    public void associateDevice(@NotNull Device device, MeshDeviceAssociateListener listener) {
        this.meshDeviceAssociateListener = listener;
        mPlSigMeshService.scanDevice(false, Util.SCAN_TYPE_PROVISION);
        mPlSigMeshService.registerProvisionCb(mSigMeshProvisionCB);
        BluetoothDevice unProvisionedDev = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.getMacAddress());
        byte[] info = Util.hexStringToBytes(device.getHash());
        byte ele_num = info[15];
        mPlSigMeshService.startProvision(unProvisionedDev, ele_num);
    }

    @Override
    public void resetDevice(@NotNull Device device, @Nullable MeshDeviceRemoveListener listener) {
        this.meshDeviceRemoveListener = listener;
        mPlSigMeshService.resetNode((short) device.getPid());
        new Handler().postDelayed(() -> {
            if (meshDeviceRemoveListener != null)
                meshDeviceRemoveListener.onDeviceRemoved(device.getId());
        },  AppConfig.TIME_MS);
    }
}
