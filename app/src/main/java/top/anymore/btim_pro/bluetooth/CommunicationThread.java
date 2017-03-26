package top.anymore.btim_pro.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import top.anymore.btim_pro.logutil.LogUtil;

/**
 * Created by anymore on 17-3-24.
 */

public class CommunicationThread extends Thread{
    private static final String tag = "CommunicationThread";
    private BluetoothSocket mBluetoothSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private Handler mHandler;
    public static final int ACTION_MSG_GET = 0;
    public static final int ACTION_MSG_SENG = 1;
    public CommunicationThread(BluetoothSocket mBluetoothSocket) {
        //设置一个默认无操作的handler，避免忘记设置handler出现的空指针异常
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                LogUtil.w(tag,"[警告]：[当您看见这个消息时，说明您忘记了设置handler]");
            }
        };
        this.mBluetoothSocket = mBluetoothSocket;
        try {
            mInputStream = mBluetoothSocket.getInputStream();
            mOutputStream = mBluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        if (mBluetoothSocket == null){
//            Log.v(tag,"Socket is null");
//        }
//        if (mInputStream == null){
//            Log.v(tag,"In is null");
//        }
//        if (mOutputStream == null){
//            Log.v(tag,"Out is null");
//        }
    }
    public void setHandler(Handler handler){
        mHandler = handler;
    }
    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(mInputStream));
        String line = "";
        try {
            while ((line=br.readLine()) != null){
                LogUtil.v(tag,line);
                line += "\n";
                Message msg = Message.obtain();
                msg.what = ACTION_MSG_GET;
                msg.obj = line;
                mHandler.sendMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                br.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    public void send(String s){
        try {
            byte[] bytes = s.getBytes();
            mOutputStream.write(bytes);
            Message msg = Message.obtain();
            msg.what = ACTION_MSG_SENG;
            msg.obj = s;
            mHandler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void cancel(){
        try {
            mInputStream.close();
            mOutputStream.close();
            mBluetoothSocket.close();
            LogUtil.v(tag,"关闭成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
