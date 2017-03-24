package top.anymore.btim_pro.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.List;

import top.anymore.btim_pro.bluetooth.BluetoothServerThread;
import top.anymore.btim_pro.bluetooth.BluetoothUtil;
import top.anymore.btim_pro.logutil.LogUtil;

/**
 * 用作"前台服务"，即当程序运行时的进程服务
 * 包括，开启蓝牙设备，搜索功能，和扫描
 */
public class BluetoothService extends Service {
    private static final String tag = "BluetoothService";
    private BluetoothUtil mBluetoothUtil;
    public BluetoothService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothUtil = new BluetoothUtil();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public class BluetoothBinder extends Binder{
        private Handler mHandler;

        public BluetoothBinder() {
            mHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    LogUtil.w(tag,"[如果看到这条消息，说明你忘记设置mHandler]");
                }
            };
        }
        public void setHandler(Handler handler){
            mHandler = handler;
        }
        public List<BluetoothDevice> getPairedDevices(){
            return null;
        }
    }
}
