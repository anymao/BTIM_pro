package top.anymore.btim_pro.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import top.anymore.btim_pro.logutil.Base64Utils;
import top.anymore.btim_pro.logutil.LogUtil;
import top.anymore.btim_pro.logutil.Test;

/**蓝牙通道建立之后，用于接收和发送数据的类
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
            while ((line = br.readLine()) != null){
                LogUtil.e(tag,"changdu = " + line.length());
                LogUtil.e(tag,line);
                Test.saveImage(line);
                /*line += "\n";
                Message msg = Message.obtain();
                msg.what = ACTION_MSG_GET;
                msg.obj = line;
                mHandler.sendMessage(msg);*/
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

    /**
     * 用于发送数据的方法
     * @param s
     */
    public void send(String s){
        try {

           /* StringBuffer sb = new StringBuffer();
            for (int i = 0 ; i < 25 * 1024; i++){
                sb.append("abc1234567");
            }
            byte[] bytes = sb.toString().getBytes();
            LogUtil.e(tag,"bytes.length = " + bytes.length);*/
            //Test.readImage("/sdcard/a.jpg");
            /*String data = Test.readImage(Environment.getExternalStorageDirectory().getAbsolutePath() + "/a.jpg");
            byte[] bytes1 = data.getBytes();*/
            //mOutputStream.write(s.getBytes());
            mOutputStream.write(Test.readImage(Environment.getExternalStorageDirectory().getAbsolutePath() + "/a.jpg").getBytes());
            /*DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(mOutputStream));
            String[] str = Test.readImage(Environment.getExternalStorageDirectory().getAbsolutePath() + "/a.jpg",65535);
            for (String tep: str) {
                dataOutputStream.writeUTF(tep);
            }
            dataOutputStream.flush();

            LogUtil.e(tag,"start = " + str[0].substring(0,1));
            LogUtil.e(tag,"end = " + str[str.length - 1].substring(str[str.length - 1].length() - 1));*/


            //dataOutputStream.writeUTF(data);

            LogUtil.d(tag,"发送base64成功");
            //发送览数据之后，会异步进行之后的方法
            Message msg = Message.obtain();
            msg.what = ACTION_MSG_SENG;
            msg.obj = s;
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //关闭线程
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
