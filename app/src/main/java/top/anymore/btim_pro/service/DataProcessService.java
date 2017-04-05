package top.anymore.btim_pro.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import top.anymore.btim_pro.ExtraDataStorage;
import top.anymore.btim_pro.R;
import top.anymore.btim_pro.activity.MainActivity;
import top.anymore.btim_pro.bluetooth.CommunicationThread;
import top.anymore.btim_pro.bluetooth.CommunicationThreadManager;
import top.anymore.btim_pro.dataprocess.DataConversionHelper;
import top.anymore.btim_pro.dataprocess.WarnTemperature;
import top.anymore.btim_pro.dataprocess.sqlite.DataProcessUtil;
import top.anymore.btim_pro.dataprocess.sqlite.TemperatureDataProcessUtil;
import top.anymore.btim_pro.entity.Message;
import top.anymore.btim_pro.entity.TemperatureDataEntity;
import top.anymore.btim_pro.logutil.LogUtil;

public class DataProcessService extends Service {
    public static final String ACTION_DATA_STORAGED = "top.anymore.btim_pro.action_data_storaged";
    public static final String ACTION_TEMPER_OVER_WARN_TEMPER = "top.anymore.btim_pro.action_temper_over_warn_temper";
    public static final String EXTRA_MESSAGE = "top.anymore.btim_pro.extra_message";
    private static final String tag = "DataProcessService";
    private CommunicationThread mCommunicationThread;
    private DataProcessUtil mDataProcessUtil;
    private TemperatureDataProcessUtil mTemperatureDataProcessUtil;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(final android.os.Message msg) {
//            LogUtil.v(tag,"info:"+msg.obj);
            //经测试，如果在下面的线程中去拿取msg.obj对象，将会得到空对象
            //我觉得应该是线程同步的问题，就把这个obj对象拿出来作为常量再传递到下面的Message实例中
            final String msg_content = (String) msg.obj;

            //**************
            switch (msg.what){
                case CommunicationThread.ACTION_MSG_GET:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            double[] temper_data = DataConversionHelper.Strings2doubles(msg_content);
                            long time = System.currentTimeMillis();
                            List<TemperatureDataEntity> entities = new ArrayList<TemperatureDataEntity>();
                            boolean isDanger = false;
                            StringBuilder dangerMessage = new StringBuilder();
                            for (int i = 0; i < temper_data.length; i++) {
                                if (!WarnTemperature.getIsInited()){
                                    WarnTemperature.initWarnTempers(getApplicationContext());
                                }
                                double warn_temper = WarnTemperature.warnTempers[i];
                                int is_danger = (temper_data[i] > warn_temper)?TemperatureDataEntity.STATE_DANGER:TemperatureDataEntity.STATE_NOT_DANGER;
                                int is_handle = (is_danger == TemperatureDataEntity.STATE_DANGER)?TemperatureDataEntity.STATE_NOT_HANDLE:TemperatureDataEntity.STATE_HANDLE;
                                TemperatureDataEntity entity = new TemperatureDataEntity(i,time,temper_data[i],warn_temper,is_danger,is_handle);
                                if (is_danger == TemperatureDataEntity.STATE_DANGER){
                                    isDanger = true;
                                    dangerMessage.append("房间："+i+" 温度： "+temper_data[i]+",");
                                }
                                entities.add(entity);
                            }
                            //当某个房间温度超过预警温度时候，发出广播
                            //在MainActivity的
                            if (isDanger){
                                dangerMessage.append(",已经超过预警温度，请您查看！");
                                Intent intent = new Intent(ACTION_TEMPER_OVER_WARN_TEMPER);
                                intent.putExtra(EXTRA_MESSAGE,dangerMessage.toString());
                                sendOrderedBroadcast(intent,null);
                            }

                            mTemperatureDataProcessUtil.addData(entities);
                            Intent intent = new Intent(ACTION_DATA_STORAGED);
                            sendBroadcast(intent);
                        }
                    }).start();

                    break;
            }


            //****************
//            //由isUIAlive判断是否更新界面
//            if (ExtraDataStorage.isUIAlive){
//                LogUtil.v(tag,"需要更新界面");
//                android.os.Message uiMsg = android.os.Message.obtain();
//                uiMsg.what = msg.what;
//                uiMsg.obj = msg.obj;
//                UIHandler.sendMessage(uiMsg);
//            }else {
//                LogUtil.v(tag,"不需要更新界面");
//            }

//            switch (msg.what){
//                case CommunicationThread.ACTION_MSG_GET:
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            LogUtil.v(tag,"msg_content:"+msg_content);
//                            Date msg_time = new Date(System.currentTimeMillis());
//                            int msg_type = Message.MESSAGE_TYPE_GET;
//                            Message message = new Message(msg_time,msg_content,msg_type);
//                            mDataProcessUtil.addData(message);
//                        }
//                    }).start();
//                    break;
//                case CommunicationThread.ACTION_MSG_SENG:
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            LogUtil.v(tag,"msg_content:"+msg_content);
//                            Date msg_time = new Date(System.currentTimeMillis());
//                            int msg_type = Message.MESSAGE_TYPE_SEND;
//                            Message message = new Message(msg_time,msg_content,msg_type);
//                            mDataProcessUtil.addData(message);
//                        }
//                    }).start();
//                    break;
//            }
        }
    };
    private Handler UIHandler;
    private CommunicationBinder mBinder = new CommunicationBinder();
    public DataProcessService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mDataProcessUtil = new DataProcessUtil(getApplicationContext(),"message.db");
        mTemperatureDataProcessUtil = new TemperatureDataProcessUtil(getApplicationContext(),ExtraDataStorage.currentDeviceAddress+"_temperdata.db");
        UIHandler = new Handler(){
            @Override
            public void handleMessage(android.os.Message msg) {
                LogUtil.v(tag,"无操作");
            }
        };
        IntentFilter filter = new IntentFilter(ACTION_TEMPER_OVER_WARN_TEMPER);
        filter.setPriority(50);
        registerReceiver(dangerMessageReceiver,filter);
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
    private BroadcastReceiver dangerMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DataProcessService.ACTION_TEMPER_OVER_WARN_TEMPER)){
                String msg = intent.getStringExtra(DataProcessService.EXTRA_MESSAGE);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Intent intent1 = new Intent(context,MainActivity.class);
                PendingIntent pi = PendingIntent.getActivities(context,0,new Intent[]{intent1},0);
                Notification notification = new NotificationCompat.Builder(context)
                        .setContentTitle("温度异常提醒")
                        .setContentText(msg)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                R.mipmap.ic_launcher))
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .build();
                manager.notify(1,notification);
            }
        }
    };
}
