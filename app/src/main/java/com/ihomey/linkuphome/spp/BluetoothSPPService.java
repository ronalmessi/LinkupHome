package com.ihomey.linkuphome.spp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class BluetoothSPPService {


    // Debugging
    private static final String TAG = "BluetoothSPPService";

    // Name for the SDP record when creating server socket
    private static final String NAME_SERVICE = "BluetoothSPPService";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;

    private int mState;

    private ConnectThread mConnectThread;
    private HashMap<String,ConnectedThread> mConnectedThreadMap;


    // Constructor. Prepares a new BluetoothChat session
    // context : The UI Activity Context
    // handler : A Handler to send messages back to the UI Activity
    public BluetoothSPPService(Context context, Handler handler) {
        mConnectedThreadMap= new HashMap<>();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = BluetoothSPPState.STATE_NONE;
        mHandler = handler;
    }

    // Return the current connection state.
    public synchronized int getState() {
        return mState;
    }

    // Set the current state of the chat connection
    // state : An integer defining the current connection state
    private synchronized void setState(int state) {
        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(BluetoothSPPState.MESSAGE_STATE_CHANGE);
        Bundle bundle = new Bundle();
        bundle.putInt(BluetoothSPPState.DEVICE_STATE, state);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }


    // Set the current state of the chat connection
    // state : An integer defining the current connection state
    private synchronized void setDeviceState(BluetoothDevice device,int state) {
        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(BluetoothSPPState.MESSAGE_STATE_CHANGE);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothSPPState.DEVICE_NAME, device.getName());
        bundle.putString(BluetoothSPPState.DEVICE_ADDRESS, device.getAddress());
        bundle.putInt(BluetoothSPPState.DEVICE_STATE, state);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    // Start the chat service. Specifically start AcceptThread to begin a
    // session in listening (server) mode. Called by the Activity onResume()
    public synchronized void start() {
        if (mConnectThread != null) { mConnectThread.cancel();mConnectThread = null;}
        for(ConnectedThread mConnectedThread : mConnectedThreadMap.values()){
            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;}
        }
        setState(BluetoothSPPState.STATE_LISTEN);
    }

    // Start the ConnectThread to initiate a connection to a remote device
    // device : The BluetoothDevice to connect
    // secure : Socket Security type - Secure (true) , Insecure (false)
    public synchronized void connect(BluetoothDevice device) {
        if(!isDeviceConnected(device.getAddress())){
            if (mAcceptThread == null) {
                mAcceptThread = new AcceptThread();
                mAcceptThread.start();
            }
            // Start the thread to connect with the given device
            mConnectThread = new ConnectThread(device);
            mConnectThread.start();
            setDeviceState(device,BluetoothSPPState.STATE_CONNECTING);
        }
    }

    private Boolean isDeviceConnected(String deviceAddress){
        ConnectedThread mConnectedThread=mConnectedThreadMap.get(deviceAddress);
        return mConnectedThread!=null&&mConnectedThread.mmSocket.isConnected();
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        ConnectedThread  connectedThread = new ConnectedThread(socket);
        connectedThread.start();
        mConnectedThreadMap.put(device.getAddress(),connectedThread);

        setDeviceState(device,BluetoothSPPState.STATE_CONNECTED);
    }

    // Stop all threads
    public synchronized void stop() {

        if (mConnectThread != null) { mConnectThread.cancel();mConnectThread = null;}

        // Cancel any thread currently running a connection
        for(ConnectedThread mConnectedThread : mConnectedThreadMap.values()){
            if (mConnectedThread != null) {
                mConnectedThread.cancel();
                mConnectedThread = null;}
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        setState(BluetoothSPPState.STATE_NONE);
    }

    // Write to the ConnectedThread in an unsynchronized manner
    // out : The bytes to write
    void write(String deviceAddress, byte[] out) {
        ConnectedThread mConnectedThread=mConnectedThreadMap.get(deviceAddress);
        if(mConnectedThread!=null&&mConnectedThread.mmSocket.isConnected()){
            mConnectedThread.write(out);
        }
    }


    // Indicate that the connection attempt failed and notify the UI Activity
     void disconnect(String deviceAddress) {
        // Start the service over to restart listening mode
//        BluetoothSPPService.this.start();
        // Cancel any thread currently running a connection
        ConnectedThread mConnectedThread=mConnectedThreadMap.get(deviceAddress);
        if(mConnectedThread!=null){
            mConnectedThread.cancel();
            mConnectedThread = null;
            mConnectedThreadMap.remove(deviceAddress);
        }
    }


    // Indicate that the connection attempt failed and notify the UI Activity
    private void connectionFailed(BluetoothDevice device) {
        // Start the service over to restart listening mode
//        BluetoothSPPService.this.start();
        // Cancel any thread currently running a connection
        ConnectedThread mConnectedThread=mConnectedThreadMap.get(device.getAddress());
        if(mConnectedThread!=null){
            mConnectedThread.cancel();
            mConnectedThread = null;
            mConnectedThreadMap.remove(device.getAddress());
        }

        setDeviceState(device,BluetoothSPPState.STATE_CONNECT_FAILED);
    }

    // Indicate that the connection was lost and notify the UI Activity
    private void connectionLost(BluetoothDevice device) {
        // Start the service over to restart listening mode
//        BluetoothSPPService.this.start();
        // Cancel any thread currently running a connection
        ConnectedThread mConnectedThread=mConnectedThreadMap.get(device.getAddress());
        if(mConnectedThread!=null){
            mConnectedThread.cancel();
            mConnectedThread = null;
            mConnectedThreadMap.remove(device.getAddress());
        }
        setDeviceState(device,BluetoothSPPState.STATE_CONNECTION_LOST);
    }

    // This thread runs while listening for incoming connections. It behaves
    // like a server-side client. It runs until a connection is accepted
    // (or until cancelled)
    private class AcceptThread extends Thread {
        // The local server socket
        private BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SERVICE, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != BluetoothSPPState.STATE_CONNECTED&&mmServerSocket!=null) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothSPPService.this) {
                        switch (mState) {
                            case BluetoothSPPState.STATE_LISTEN:
                            case BluetoothSPPState.STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case BluetoothSPPState.STATE_NONE:
                            case BluetoothSPPState.STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) { }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                if(mmServerSocket!=null){
                    mmServerSocket.close();
                    mmServerSocket = null;
                }
            } catch (IOException e) {

            }
        }

    }


    // This thread runs while attempting to make an outgoing connection
    // with a device. It runs straight through
    // the connection either succeeds or fails
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                if(mmSocket==null) return;
                mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                e.printStackTrace();
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                connectionFailed(mmDevice);
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothSPPService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                if(mmSocket!=null) mmSocket.close();
            } catch (IOException e) {

            }
        }
    }

    // This thread runs during a connection with a remote device.
    // It handles all incoming and outgoing transmissions.
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    Message msg = mHandler.obtainMessage(BluetoothSPPState.MESSAGE_RECEIVE_DATA);
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("data",buffer);
                    if(mmSocket!=null&&mmSocket.getRemoteDevice()!=null) bundle.putString(BluetoothSPPState.DEVICE_ADDRESS, mmSocket.getRemoteDevice().getAddress());
                    bundle.putInt("length", bytes);
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                } catch (IOException e) {
                    connectionLost(mmSocket.getRemoteDevice());
                    break;
                }
            }
//            byte[] buffer;
//            ArrayList<Integer> arr_byte = new ArrayList<Integer>();
//
//            // Keep listening to the InputStream while connected
//            while (true) {
//                try {
//
//                    int data = mmInStream.read();
//                    Log.d("bg_timer_setting_on_v2","tttttttttt--"+data);
//                    if(data == 0x0A) {
//
//                    } else if(data == 0x0D) {
//                        buffer = new byte[arr_byte.size()];
//                        for(int i = 0 ; i < arr_byte.size() ; i++) {
//                            buffer[i] = arr_byte.get(i).byteValue();
//                        }
//                        Log.d("bg_timer_setting_on_v2","gggggggg");
//                        // Send the obtained bytes to the UI Activity
//                        mHandler.obtainMessage(BluetoothSPPState.MESSAGE_READ
//                                , buffer.length, -1, buffer).sendToTarget();
//                        arr_byte = new ArrayList<Integer>();
//                    } else {
//                        arr_byte.add(data);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    connectionLost();
//                    // Start the service over to restart listening mode
//                    BluetoothSPPService.this.start(BluetoothSPPService.this.isAndroid);
//                    break;
//                }
//            }
        }

        // Write to the connected OutStream.
        // @param buffer  The bytes to write
        public void write(byte[] buffer) {
            try {/*
                byte[] buffer2 = new byte[buffer.length + 2];
                for(int i = 0 ; i < buffer.length ; i++)
                    buffer2[i] = buffer[i];
                buffer2[buffer2.length - 2] = 0x0A;
                buffer2[buffer2.length - 1] = 0x0D;*/
                mmOutStream.write(buffer);
                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(BluetoothSPPState.MESSAGE_WRITE
                        , -1, -1, buffer).sendToTarget();
            } catch (IOException e) { }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
