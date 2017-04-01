package top.anymore.btim_pro.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

import top.anymore.btim_pro.ExtraDataStorage;
import top.anymore.btim_pro.bluetooth.CommunicationThread;
import top.anymore.btim_pro.bluetooth.CommunicationThreadManager;
import top.anymore.btim_pro.dataprocess.DataConversionHelper;
import top.anymore.btim_pro.dataprocess.WarnTemperature;
import top.anymore.btim_pro.dataprocess.sqlite.TemperatureDataProcessUtil;
import top.anymore.btim_pro.dataprocess.sqlite.TemperatureSQLiteHelper;
import top.anymore.btim_pro.entity.TemperatureDataEntity;
import top.anymore.btim_pro.logutil.LogUtil;

public class TemperatureDataService extends Service {
    public static final String ACTION_DATABASE_CHANGE = "top.anymore.btim_pro.action_database_change";
    private static final String tag = "TemperatureDataService";
    private CommunicationThread mCommunicationThread;
    private TemperatureDataProcessUtil mTemperatureDataProcessUtil;
    private CommunicationBinder mBinder = new CommunicationBinder();
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CommunicationThread.ACTION_MSG_GET:
                    final String data = (String) msg.obj;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            LogUtil.v(tag,"hehehe");
                            double[] temper_datas = DataConversionHelper.Strings2doubles(data);
                            long time = System.currentTimeMillis();
                            List<TemperatureDataEntity> entities = new ArrayList<TemperatureDataEntity>();
                            for (int i = 0; i < temper_datas.length; i++) {
                                int is_danger = (temper_datas[i]> WarnTemperature.getDefalutWarnTemperature())?TemperatureDataEntity.STATE_DANGER:TemperatureDataEntity.STATE_NOT_DANGER;
                                int is_handle = (is_danger == TemperatureDataEntity.STATE_DANGER)?TemperatureDataEntity.STATE_NOT_HANDLE:TemperatureDataEntity.STATE_HANDLE;
                                TemperatureDataEntity entity = new TemperatureDataEntity(i,time,temper_datas[i],WarnTemperature.getDefalutWarnTemperature(),is_danger,is_handle);
                            }
                            mTemperatureDataProcessUtil.addData(entities);
                            Intent intent = new Intent(ACTION_DATABASE_CHANGE);
                            sendBroadcast(intent);
                        }
                    }).start();
                    break;
                case CommunicationThread.ACTION_MSG_SENG:
                    LogUtil.v(tag,"发送消息逻辑[未填写]");
                    break;
            }
        }
    };
    public TemperatureDataService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTemperatureDataProcessUtil = new TemperatureDataProcessUtil(getApplicationContext(),ExtraDataStorage.currentDeviceAddress+"_temp.db");
    }

    @Override
    public IBinder onBind(Intent intent) {
        mCommunicationThread = CommunicationThreadManager.getCommunicationThread();
        if (mCommunicationThread == null){
            LogUtil.v(tag,"GG");
        }
        mCommunicationThread.setHandler(mHandler);
        mCommunicationThread.start();
        LogUtil.v(tag,"mCommunicationThread.start();");
        return mBinder;
    }

    public class CommunicationBinder extends Binder {
        public void sendMessage(String msg_content){
            mCommunicationThread.send(msg_content);
        }
    }
}
