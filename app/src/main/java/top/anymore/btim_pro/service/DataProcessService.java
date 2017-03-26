package top.anymore.btim_pro.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import java.sql.Date;

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
            switch (msg.what){
                case CommunicationThread.ACTION_MSG_GET:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String msg_content = (String) msg.obj;
                            Date msg_date = new Date(System.currentTimeMillis());
                            int msg_type = Message.MESSAGE_TYPE_GET;
                            Message message = new Message(msg_date,msg_content,msg_type);
                            mDataProcessUtil.addData(message);
                        }
                    }).start();
                    break;
                case CommunicationThread.ACTION_MSG_SENG:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String msg_content = (String) msg.obj;
                            Date msg_date = new Date(System.currentTimeMillis());
                            int msg_type = Message.MESSAGE_TYPE_SEND;
                            Message message = new Message(msg_date,msg_content,msg_type);
                            mDataProcessUtil.addData(message);
                        }
                    }).start();
                    break;
            }
        }
    };
    private CommunicationBinder mBinder = new CommunicationBinder();
    public DataProcessService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDataProcessUtil = new DataProcessUtil(getApplicationContext(),"message.db");
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
    public class CommunicationBinder extends Binder{
        public void sendMessage(String msg_content){
            mCommunicationThread.send(msg_content);
        }
    }

}
