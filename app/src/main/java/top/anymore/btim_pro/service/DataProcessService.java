package top.anymore.btim_pro.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import java.util.Date;

import top.anymore.btim_pro.ExtraDataStorage;
import top.anymore.btim_pro.bluetooth.CommunicationThread;
import top.anymore.btim_pro.bluetooth.CommunicationThreadManager;
import top.anymore.btim_pro.dataprocess.sqlite.DataProcessUtil;
import top.anymore.btim_pro.entity.Message;
import top.anymore.btim_pro.logutil.LogUtil;

public class DataProcessService extends Service {
    private static final String tag = "DataProcessService";
    private CommunicationThread mCommunicationThread;
    private DataProcessUtil mDataProcessUtil;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(final android.os.Message msg) {
//            LogUtil.v(tag,"info:"+msg.obj);
            //经测试，如果在下面的线程中去拿取msg.obj对象，将会得到空对象
            //我觉得应该是线程同步的问题，就把这个obj对象拿出来作为常量再传递到下面的Message实例中
            final String msg_content = (String) msg.obj;
            //由isUIAlive判断是否更新界面
            if (ExtraDataStorage.isUIAlive){
                LogUtil.v(tag,"需要更新界面");
                android.os.Message uiMsg = android.os.Message.obtain();
                uiMsg.what = msg.what;
                uiMsg.obj = msg.obj;
                UIHandler.sendMessage(uiMsg);
            }else {
                LogUtil.v(tag,"不需要更新界面");
            }
            switch (msg.what){
                case CommunicationThread.ACTION_MSG_GET:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.v(tag,"msg_content:"+msg_content);
                            Date msg_time = new Date(System.currentTimeMillis());
                            int msg_type = Message.MESSAGE_TYPE_GET;
                            Message message = new Message(msg_time,msg_content,msg_type);
                            mDataProcessUtil.addData(message);
                        }
                    }).start();
                    break;
                case CommunicationThread.ACTION_MSG_SENG:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.v(tag,"msg_content:"+msg_content);
                            Date msg_time = new Date(System.currentTimeMillis());
                            int msg_type = Message.MESSAGE_TYPE_SEND;
                            Message message = new Message(msg_time,msg_content,msg_type);
                            mDataProcessUtil.addData(message);
                        }
                    }).start();
                    break;
            }
        }
    };
    private Handler UIHandler;
    private CommunicationBinder mBinder = new CommunicationBinder();
    public DataProcessService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDataProcessUtil = new DataProcessUtil(getApplicationContext(),"message.db");
        UIHandler = new Handler(){
            @Override
            public void handleMessage(android.os.Message msg) {
                LogUtil.v(tag,"无操作");
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.v(tag,"onBind");
        mCommunicationThread = CommunicationThreadManager.getCommunicationThread();
        if (mCommunicationThread == null){
            LogUtil.v(tag,"GG");
        }
        mCommunicationThread.setHandler(mHandler);
        mCommunicationThread.start();
        LogUtil.v(tag,"mCommunicationThread.start();");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.v(tag,"onDestroy");
    }

    public class CommunicationBinder extends Binder{
        public void setUIHandler(Handler handler){
            UIHandler = handler;
        }
        public void sendMessage(String msg_content){
            mCommunicationThread.send(msg_content);
        }
    }

}
