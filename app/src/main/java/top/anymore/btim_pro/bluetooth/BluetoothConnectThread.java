package top.anymore.btim_pro.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.UUID;

import top.anymore.btim_pro.logutil.LogUtil;

/**
 * Created by anymore on 17-3-24.
 */

public class BluetoothConnectThread extends Thread{
    private static final String tag = "BluetoothConnectThread";
    //据说这个是串口通信的UUID
    public static final String UUID_CONNECT_DEFAULT = "00001101-0000-1000-8000-00805F9B34FB";
    public static final String ACTION_BLUETOOTH_CONNECT = "top.anymore.btim_pro.bluetooth.bluetoothconnectthread.action_bluetooth_connect";
    private String uuid_connect = UUID_CONNECT_DEFAULT;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mSocket;
    private Context mContext;

    public BluetoothConnectThread(Context mContext) {
        this.mContext = mContext;
//        this.mBluetoothDevice = mBluetoothDevice;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null){
            LogUtil.e(tag,"本机没有蓝牙设备，APP出现异常");
        }else{
            LogUtil.v(tag,"本机拥有蓝牙设备");
        }
    }
    public void setTarget(BluetoothDevice device){
        mBluetoothDevice = device;
    }
    @Override
    public void run() {
        if (mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        try {
            mSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(uuid_connect));
            //线程将阻塞，连接设备之后才会进行下一步
            mSocket.connect();
            LogUtil.v(tag,"连接上服务器");
            Intent intent = new Intent(ACTION_BLUETOOTH_CONNECT);
            mContext.sendBroadcast(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public BluetoothSocket getBluetoothSocket(){
        return mSocket;
    }
}
