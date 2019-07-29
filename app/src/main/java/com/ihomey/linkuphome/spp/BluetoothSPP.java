package com.ihomey.linkuphome.spp;

import java.util.ArrayList;
import java.util.Set;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


public class BluetoothSPP {
    // Listener for Bluetooth Status & Connection
    private BluetoothStateListener mBluetoothStateListener = null;

    private OnDataReceivedListener mDataReceivedListener = null;
    private BluetoothConnectionListener mBluetoothConnectionListener = null;

    final ArrayList<String> mAutoConnectDeviceAddressList = new ArrayList<String>();


    // Context from activity which call this class
    private Context mContext;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothSPPService mChatService = null;


    private static final BluetoothSPP ourInstance = new BluetoothSPP();

    public static BluetoothSPP getInstance() {
        return ourInstance;
    }

    private BluetoothSPP() {

    }

    public void initialize(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public interface BluetoothStateListener {
        public void onServerStartListen();
        public void onDeviceDisConnected(String name, String address);
    }

    public interface OnDataReceivedListener {
        public void onDataReceived(byte[] data, String message);
    }

    public interface BluetoothConnectionListener {
        public void onDeviceConnecting(String name, String address);
        public void onDeviceConnected(String name, String address);
        public void onDeviceConnectFailed(String name, String address);
    }

    public boolean isBluetoothAvailable() {
        try {
            if (mBluetoothAdapter == null || mBluetoothAdapter.getAddress().equals(null))
                return false;
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    public boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public boolean isServiceAvailable() {
        return mChatService != null;
    }

    public boolean startDiscovery() {
        return mBluetoothAdapter.startDiscovery();
    }

    public boolean isDiscovery() {
        return mBluetoothAdapter.isDiscovering();
    }

    public boolean cancelDiscovery() {
        return mBluetoothAdapter.cancelDiscovery();
    }

    public void setupService() {
        mChatService = new BluetoothSPPService(mContext, mHandler);
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }


    public void startService() {
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothSPPState.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    public void stopService() {
        if (mChatService != null) {
            mChatService.stop();
        }
        new Handler().postDelayed(() -> {
            if (mChatService != null) {
                mChatService.stop();
            }
        }, 500);
    }


    /**
     * 截取byte数组   不改变原数组
     * @param b 原数组
     * @param off 偏差值（索引）
     * @param length 长度
     * @return 截取后的数组
     */
    public byte[] subByte(byte[] b,int off,int length) {
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothSPPState.MESSAGE_RECEIVE_DATA:
                    byte[] readBuf = subByte((byte[]) msg.obj,0,msg.arg1);
                    String readMessage = new String(readBuf);
                    if(readBuf.length > 0) {
                        if(mDataReceivedListener != null)
                            mDataReceivedListener.onDataReceived(readBuf, readMessage);
                    }
                    break;
                case BluetoothSPPState.MESSAGE_TOAST:
                    Toast.makeText(mContext, msg.getData().getString(BluetoothSPPState.TOAST), Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothSPPState.MESSAGE_STATE_CHANGE:
                    int mDeviceState = msg.getData().getInt(BluetoothSPPState.DEVICE_STATE);
                    String mDeviceName = msg.getData().getString(BluetoothSPPState.DEVICE_NAME);
                    String  mDeviceAddress = msg.getData().getString(BluetoothSPPState.DEVICE_ADDRESS);
                    if(mDeviceState==BluetoothSPPState.STATE_LISTEN){
                        mBluetoothStateListener.onServerStartListen();
                    }else if(mDeviceState==BluetoothSPPState.STATE_CONNECTING){
                        mBluetoothConnectionListener.onDeviceConnecting(mDeviceName,mDeviceAddress);
                    }else if(mDeviceState==BluetoothSPPState.STATE_CONNECTED){
                        mBluetoothConnectionListener.onDeviceConnected(mDeviceName,mDeviceAddress);
                    }else if(mDeviceState==BluetoothSPPState.STATE_CONNECT_FAILED){
                        mBluetoothConnectionListener.onDeviceConnectFailed(mDeviceName,mDeviceAddress);
                        if(mAutoConnectDeviceAddressList.contains(mDeviceAddress)) connect(mDeviceAddress);
                    }else if(mDeviceState==BluetoothSPPState.STATE_CONNECTION_LOST){
                        mBluetoothStateListener.onDeviceDisConnected(mDeviceName,mDeviceAddress);
                        if(mAutoConnectDeviceAddressList.contains(mDeviceAddress)) connect(mDeviceAddress);
                    }
                    break;
            }
        }
    };

    public void stopAutoConnect(String address) {
        mAutoConnectDeviceAddressList.remove(address);
    }

    public void connect(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if(mChatService!=null)mChatService.connect(device);
    }

    public void autoConnect(String address) {
        if(!TextUtils.isEmpty(address)){
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            if(mChatService!=null&&device!=null){
                mAutoConnectDeviceAddressList.add(address);
                mChatService.connect(device);
            }
        }
    }

    public void disconnect(String deviceAddress) {
        mAutoConnectDeviceAddressList.remove(deviceAddress);
        if(mChatService != null) {
            mChatService.disconnect(deviceAddress);
        }
    }

    public void setBluetoothStateListener (BluetoothStateListener listener) {
        mBluetoothStateListener = listener;
    }

    public void setOnDataReceivedListener (OnDataReceivedListener listener) {
        mDataReceivedListener = listener;
    }

    public void setBluetoothConnectionListener (BluetoothConnectionListener listener) {
        mBluetoothConnectionListener = listener;
    }

    public void enable() {
        mBluetoothAdapter.enable();
    }

    public void send(String deviceAddress,byte[] data, boolean CRLF) {
        if(mChatService!=null) {
            if(CRLF) {
                byte[] data2 = new byte[data.length + 2];
                for(int i = 0 ; i < data.length ; i++)
                    data2[i] = data[i];
                data2[data2.length - 2] = 0x0A;
                data2[data2.length - 1] = 0x0D;
                mChatService.write(deviceAddress,data2);
            } else {
                mChatService.write(deviceAddress,data);
            }
        }
    }

    public void send(String deviceAddress,String data, boolean CRLF) {
        if(mChatService!=null&&mChatService.getState() == BluetoothSPPState.STATE_CONNECTED) {
            if(CRLF)
                data += "\r\n";
            mChatService.write(deviceAddress,data.getBytes());
        }
    }


    public String[] getPairedDeviceName() {
        int c = 0;
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        String[] name_list = new String[devices.size()];
        for(BluetoothDevice device : devices) {
            name_list[c] = device.getName();
            c++;
        }
        return name_list;
    }

    public String[] getPairedDeviceAddress() {
        int c = 0;
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        String[] address_list = new String[devices.size()];
        for(BluetoothDevice device : devices) {
            address_list[c] = device.getAddress();
            c++;
        }
        return address_list;
    }

}
