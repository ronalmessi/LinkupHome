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
import android.text.TextUtils;

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

    private MeshDeviceScanListener meshDeviceScanListener;
    private MeshStateListener meshStateListener;

    private MeshDeviceRemoveListener meshDeviceRemoveListener;

    public void setMeshDeviceRemoveListener(MeshDeviceRemoveListener meshDeviceRemoveListener) {
        this.meshDeviceRemoveListener = meshDeviceRemoveListener;
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
        activity.unbindService(mPlSigMeshServiceConnection);
    }


    @Override
    public void connect() {

    }

    @Override
    public void initService(@NotNull Zone zone) {
        mPlSigMeshNet = PlSigMeshService.getInstance().chooseMeshNet(0);
        mPlSigMeshService.scanDevice(true, Util.SCAN_TYPE_PROXY);
        mPlSigMeshService.registerProxyCb(mSigMeshProxyCB);
        mPlSigMeshService.registerProvisionCb(mSigMeshProvisionCB);
        mPlSigMeshService.proxyJoin();
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
            switch (status) {
                case Util.PL_MESH_READY:
                    mConnected = true;
                    if (meshStateListener != null)
                        meshStateListener.onDeviceConnected("SigMesh V1");
                    break;
                case Util.PL_MESH_JOIN_FAIL:
                case Util.PL_MESH_EXIT:
                    mConnected = false;
                    if (meshStateListener != null)
                        meshStateListener.onDeviceDisConnected("SigMesh V1");
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
        }
    };

    @Override
    public void associateDevice(@NotNull Device device, MeshDeviceAssociateListener listener) {
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
