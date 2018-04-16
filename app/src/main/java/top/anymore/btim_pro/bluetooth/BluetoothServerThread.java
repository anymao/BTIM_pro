package top.anymore.btim_pro.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.UUID;

import top.anymore.btim_pro.logutil.LogUtil;

/**蓝牙作为服务端，开启此线程，蓝牙才能被客户端连接
 * Created by anymore on 17-3-24.
 */

public class BluetoothServerThread extends Thread{
    private static final String tag = "BluetoothServerThread";
    public static final String UUID_SERVER_DEFAULT = "d4ae77eb-923f-4723-8fdf-720905275221";
    public static final String ACTION_BLUETOOTH_CONNECT = "top.anymore.btim_pro.bluetooth.bluetoothserverthread.action_bluetooth_connect";
    private String uuid_server = UUID_SERVER_DEFAULT;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothServerSocket mServerSocket;
    private BluetoothSocket mSocket;
    private Context mContext;

    public BluetoothServerThread(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null){
            LogUtil.e(tag,"本机没有蓝牙设备，APP出现异常");
        }else{
            LogUtil.v(tag,"本机拥有蓝牙设备");
        }
        BluetoothServerSocket temp = null;
        try {
            temp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("top.anymore.btim_pro", UUID.fromString(uuid_server));
        } catch (IOException e) {
            e.printStackTrace();
            temp = null;
        }
        mServerSocket = temp;
        LogUtil.v(tag,"[mServerSocket]"+mServerSocket.toString());
    }

    @Override
    public void run() {
        try {
            //线程会在这里阻塞，知道有设备连接才会执行下一步
            mSocket = mServerSocket.accept();
            LogUtil.v(tag,"有设备连接进来");
            //在这里发送一个广播通知有设备连接，或者这里应该还可以采取异步消息处理，
            //发送广播通知
            Intent intent = new Intent(ACTION_BLUETOOTH_CONNECT);
            mContext.sendBroadcast(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于设置当蓝牙开启服务功能等待连接时候的UUID
     * @param uuid
     */
    public void setUuid_server(String uuid){
        if (null == uuid){
            uuid_server = UUID_SERVER_DEFAULT;
        }else {
            uuid_server = uuid;
        }
    }
    /**
     * 获得连接之后的BluetoothSocket实例
     * 用于在IO通信线程中交互
     * @return mSocket
     */
    public BluetoothSocket getBluetoothSocket(){
        return mSocket;
    }
}
