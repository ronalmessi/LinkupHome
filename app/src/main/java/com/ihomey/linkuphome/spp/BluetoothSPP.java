package com.ihomey.linkuphome.spp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class BluetoothSPP {
    private static final BluetoothSPP ourInstance = new BluetoothSPP();
    final ArrayList<String> mAutoConnectDeviceAddressList = new ArrayList<String>();
    // Listener for Bluetooth Status & Connection
    private BluetoothStateListener mBluetoothStateListener = null;
    private List<OnDataReceivedListener> mDataReceivedListenerList = new ArrayList<OnDataReceivedListener>();
    private BluetoothConnectionListener mBluetoothConnectionListener = null;
    // Context from activity which call this class
    private Context mContext;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothSPPService mChatService = null;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothSPPState.MESSAGE_RECEIVE_DATA:
                    int length = msg.getData().getInt("length");
                    String mAddress = msg.getData().getString(BluetoothSPPState.DEVICE_ADDRESS);
                    byte[] data = msg.getData().getByteArray("data");
                    byte[] readBuf = subByte(data, 0, length);
                    String readMessage = new String(readBuf);
                    if (readBuf.length > 0) {
                        for (OnDataReceivedListener listener : mDataReceivedListenerList) {
                            listener.onDataReceived(readBuf, readMessage, mAddress);
                        }
                    }
                    break;
                case BluetoothSPPState.MESSAGE_TOAST:
                    Toast.makeText(mContext, msg.getData().getString(BluetoothSPPState.TOAST), Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothSPPState.MESSAGE_STATE_CHANGE:
                    int mDeviceState = msg.getData().getInt(BluetoothSPPState.DEVICE_STATE);
                    String mDeviceName = msg.getData().getString(BluetoothSPPState.DEVICE_NAME);
                    String mDeviceAddress = msg.getData().getString(BluetoothSPPState.DEVICE_ADDRESS);
                    if (mDeviceState == BluetoothSPPState.STATE_LISTEN) {
                        if (mBluetoothStateListener != null)
                            mBluetoothStateListener.onServerStartListen();
                    } else if (mDeviceState == BluetoothSPPState.STATE_CONNECTING) {
                        if (mBluetoothConnectionListener != null)
                            mBluetoothConnectionListener.onDeviceConnecting(mDeviceName, mDeviceAddress);
                    } else if (mDeviceState == BluetoothSPPState.STATE_CONNECTED) {
                        if (mBluetoothConnectionListener != null)
                            mBluetoothConnectionListener.onDeviceConnected(mDeviceName, mDeviceAddress);
                        if (mBluetoothStateListener != null)
                            mBluetoothStateListener.onDeviceConnected(mDeviceName, mDeviceAddress);
                    } else if (mDeviceState == BluetoothSPPState.STATE_CONNECT_FAILED) {
                        if (mBluetoothConnectionListener != null)
                            mBluetoothConnectionListener.onDeviceConnectFailed(mDeviceName, mDeviceAddress);
                        if (mAutoConnectDeviceAddressList.contains(mDeviceAddress))
                            connect(mDeviceAddress);
                    } else if (mDeviceState == BluetoothSPPState.STATE_CONNECTION_LOST) {
                        if (mBluetoothStateListener != null)
                            mBluetoothStateListener.onDeviceDisConnected(mDeviceName, mDeviceAddress);
                        if (mAutoConnectDeviceAddressList.contains(mDeviceAddress))
                            connect(mDeviceAddress);
                    }
                    break;
            }
        }
    };

    private BluetoothSPP() {

    }

    public static BluetoothSPP getInstance() {
        return ourInstance;
    }

    public void initialize(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public ArrayList<String> getAutoConnectDeviceAddressList() {
        return mAutoConnectDeviceAddressList;
    }

    public boolean isBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public void startService() {
        if (isBluetoothEnabled()) {
            mChatService = new BluetoothSPPService(mContext, mHandler);
            if (mChatService.getState() == BluetoothSPPState.STATE_NONE) {
                mChatService.start();
                for (String address : mAutoConnectDeviceAddressList) {
                    connect(address);
                }
            }
        }
    }

    public void stopService() {
        if (mChatService != null) {
            mChatService.stop();
            mChatService = null;
        }
    }

    public void release() {
        mDataReceivedListenerList.clear();
        mAutoConnectDeviceAddressList.clear();
        mBluetoothConnectionListener = null;
        mBluetoothStateListener = null;
        stopService();
    }

    /**
     * 截取byte数组   不改变原数组
     *
     * @param b      原数组
     * @param off    偏差值（索引）
     * @param length 长度
     * @return 截取后的数组
     */
    public byte[] subByte(byte[] b, int off, int length) {
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }

    public void connect(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (mChatService != null) mChatService.connect(device);
    }

    public void autoConnect(String address) {
        if (!TextUtils.isEmpty(address)) {
            if (!mAutoConnectDeviceAddressList.contains(address)) {
                mAutoConnectDeviceAddressList.add(address);
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                if (mChatService != null && device != null) {
                    mChatService.connect(device);
                }
            }
        }
    }

    public void disconnect(String deviceAddress) {
        mAutoConnectDeviceAddressList.remove(deviceAddress);
        if (mChatService != null) {
            mChatService.disconnect(deviceAddress);
        }
    }

    public void setBluetoothStateListener(BluetoothStateListener listener) {
        mBluetoothStateListener = listener;
    }

    public void addOnDataReceivedListener(OnDataReceivedListener listener) {
        mDataReceivedListenerList.add(listener);
    }

    public void removeOnDataReceivedListener(OnDataReceivedListener listener) {
        mDataReceivedListenerList.remove(listener);
    }

    public void setBluetoothConnectionListener(BluetoothConnectionListener mBluetoothConnectionListener) {
        this.mBluetoothConnectionListener = mBluetoothConnectionListener;
    }

    public void send(String deviceAddress, byte[] data, boolean CRLF) {
        if (mChatService != null) {
            if (CRLF) {
                byte[] data2 = new byte[data.length + 2];
                for (int i = 0; i < data.length; i++)
                    data2[i] = data[i];
                data2[data2.length - 2] = 0x0A;
                data2[data2.length - 1] = 0x0D;
                mChatService.write(deviceAddress, data2);
            } else {
                mChatService.write(deviceAddress, data);
            }
        }
    }

    public void send(String deviceAddress, String data, boolean CRLF) {
        if (mChatService != null && mChatService.getState() == BluetoothSPPState.STATE_CONNECTED) {
            if (CRLF)
                data += "\r\n";
            mChatService.write(deviceAddress, data.getBytes());
        }
    }

    public String[] getPairedDeviceAddress() {
        int c = 0;
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        String[] address_list = new String[devices.size()];
        for (BluetoothDevice device : devices) {
            address_list[c] = device.getAddress();
            c++;
        }
        return address_list;
    }

    public interface BluetoothStateListener {
        void onServerStartListen();

        void onDeviceDisConnected(String name, String address);

        void onDeviceConnected(String name, String address);
    }

    public interface OnDataReceivedListener {
        void onDataReceived(byte[] data, String message, String address);
    }

    public interface BluetoothConnectionListener {
        void onDeviceConnecting(String name, String address);

        void onDeviceConnected(String name, String address);

        void onDeviceConnectFailed(String name, String address);
    }

}
