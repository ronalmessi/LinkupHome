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
import android.util.ArrayMap;
import android.util.SparseIntArray;

import com.csr.mesh.ConfigModelApi;
import com.csr.mesh.MeshService;
import com.ihomey.linkuphome.AppConfig;
import com.ihomey.linkuphome.R;
import com.ihomey.linkuphome.data.entity.Device;
import com.ihomey.linkuphome.data.entity.Zone;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CSRMeshServiceManager implements Connector {

    private static final CSRMeshServiceManager ourInstance = new CSRMeshServiceManager();

    public static CSRMeshServiceManager getInstance() {
        return ourInstance;
    }

    private CSRMeshServiceManager() {

    }

    private MeshService mService;

    private Handler mMeshHandler;

    private Boolean mConnected = false;

    private ArrayMap<String, String> addressToNameMap = new ArrayMap<>();

    private SparseIntArray mDeviceIdToUuidHash = new SparseIntArray();

    private MeshDeviceScanListener meshDeviceScanListener;

    private MeshStateListener meshStateListener;

    private MeshDeviceAssociateListener meshDeviceAssociateListener;

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
        mMeshHandler = new MeshHandler();
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


    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder rawBinder) {
            mService = ((MeshService.LocalBinder) rawBinder).getService();
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


    private BluetoothAdapter.LeScanCallback mScanCallBack = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            mService.processMeshAdvert(device, scanRecord, rssi);
            if (!TextUtils.isEmpty(device.getName()) && !addressToNameMap.containsKey(device.getAddress())) {
                addressToNameMap.put(device.getAddress(), device.getName());
            }
        }
    };


    private int getDeviceTypeByShortName(String shortName) {
        switch (shortName) {
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

    @Override
    public void associateDevice(@NotNull Device device, MeshDeviceAssociateListener listener) {
        this.meshDeviceAssociateListener = listener;
        mService.associateDevice(Integer.valueOf(device.getHash()), 0, false);
    }

    @Override
    public void resetDevice(@NotNull Device device, @Nullable MeshDeviceRemoveListener listener) {
        this.meshDeviceRemoveListener=listener;
        ConfigModelApi.resetDevice(device.getInstructId());
        mMeshHandler.postDelayed(() -> {
            if(meshDeviceRemoveListener!=null) meshDeviceRemoveListener.onDeviceRemoved(device.getId());
        }, AppConfig.TIME_MS);
    }

    @SuppressLint("HandlerLeak")
    private class MeshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MeshService.MESSAGE_LE_CONNECTED:
                    mConnected = true;
                    String connectedAddress = msg.getData().getString(MeshService.EXTRA_DEVICE_ADDRESS);
                    String connectedDeviceName = addressToNameMap.get(connectedAddress);
                    if (meshStateListener != null && connectedAddress != null && connectedDeviceName != null)
                        meshStateListener.onDeviceConnected(connectedDeviceName);
                    break;
                case MeshService.MESSAGE_LE_DISCONNECTED:
                    mConnected = false;
                    String disConnectedAddress = msg.getData().getString(MeshService.EXTRA_DEVICE_ADDRESS);
                    String disConnectedDeviceName = addressToNameMap.get(disConnectedAddress);
                    int numConnections = msg.getData().getInt(MeshService.EXTRA_NUM_CONNECTIONS);
                    if (numConnections == 0 && meshStateListener != null && disConnectedAddress != null && disConnectedDeviceName != null) meshStateListener.onDeviceDisConnected(disConnectedDeviceName);
                    break;
                case MeshService.MESSAGE_DEVICE_APPEARANCE:
                    String shortName = msg.getData().getString(MeshService.EXTRA_SHORTNAME);
                    if (!TextUtils.isEmpty(shortName) && meshDeviceScanListener != null && shortName != null) {
                        Device device = new Device(getDeviceTypeByShortName(shortName), shortName.substring(shortName.length() - 2));
                        device.setHash(msg.getData().getInt(MeshService.EXTRA_UUIDHASH_31) + "");
                        meshDeviceScanListener.onDeviceFound(device);
                    }
                    break;
                case MeshService.MESSAGE_ASSOCIATING_DEVICE:
                    int progress = msg.getData().getInt(MeshService.EXTRA_PROGRESS_INFORMATION);
                    if (meshDeviceAssociateListener != null) meshDeviceAssociateListener.associationProgress(progress);
                    break;

                case MeshService.MESSAGE_DEVICE_ASSOCIATED:
                    int deviceId = msg.getData().getInt(MeshService.EXTRA_DEVICE_ID);
                    mDeviceIdToUuidHash.put(deviceId, msg.getData().getInt(MeshService.EXTRA_UUIDHASH_31));
                    ConfigModelApi.getInfo(deviceId, ConfigModelApi.DeviceInfo.MODEL_LOW);
                    break;

                case MeshService.MESSAGE_CONFIG_DEVICE_INFO:
                    int extraDeviceId = msg.getData().getInt(MeshService.EXTRA_DEVICE_ID);
                    int uuidHash =mDeviceIdToUuidHash.get(extraDeviceId);
                    ConfigModelApi.DeviceInfo infoType = ConfigModelApi.DeviceInfo.values()[(int) msg.getData().getByte(MeshService.EXTRA_DEVICE_INFO_TYPE)];
                    if (infoType == ConfigModelApi.DeviceInfo.MODEL_LOW && uuidHash != 0) {
                        mDeviceIdToUuidHash.removeAt(mDeviceIdToUuidHash.indexOfKey(extraDeviceId));
                        if (meshDeviceAssociateListener != null) meshDeviceAssociateListener.deviceAssociated(extraDeviceId, uuidHash, "");
                    }
                    break;

                case MeshService.MESSAGE_TIMEOUT:
                    int expectedMsg = msg.getData().getInt(MeshService.EXTRA_EXPECTED_MESSAGE);
                    if (expectedMsg == MeshService.MESSAGE_DEVICE_ASSOCIATED || expectedMsg == MeshService.MESSAGE_CONFIG_MODELS) {
                        if (meshDeviceAssociateListener != null) meshDeviceAssociateListener.deviceAssociateFailed(R.string.msg_device_connect_failed);
                    }
                    break;
            }
        }
    }

}
