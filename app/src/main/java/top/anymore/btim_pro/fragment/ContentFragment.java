package top.anymore.btim_pro.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import top.anymore.btim_pro.ExtraDataStorage;
import top.anymore.btim_pro.R;
import top.anymore.btim_pro.activity.AdvancedFunctionActivity;
import top.anymore.btim_pro.adapter.RoomsStateAdapter;
import top.anymore.btim_pro.bluetooth.BluetoothConnectThread;
import top.anymore.btim_pro.bluetooth.BluetoothServerThread;
import top.anymore.btim_pro.bluetooth.CommunicationThreadManager;
import top.anymore.btim_pro.dataprocess.DataConversionHelper;
import top.anymore.btim_pro.dataprocess.sqlite.DataProcessUtil;
import top.anymore.btim_pro.dataprocess.sqlite.TemperatureDataProcessUtil;
import top.anymore.btim_pro.entity.TemperatureDataEntity;
import top.anymore.btim_pro.logutil.LogUtil;
import top.anymore.btim_pro.service.DataProcessService;
import top.anymore.btim_pro.service.TemperatureDataService;

/**
 * 抽屉视图中，中间视图碎片
 * Created by anymore on 17-3-23.
 */

public class ContentFragment extends Fragment{
    private static final String tag = "ContentFragment";
    private TextView tv_linkstate,tv_record;//显示连接状态和发送消息记录
    private EditText et_msg;//消息输入框
    private Button btn_send;//发送按钮
    private TemperatureDataProcessUtil mTemperatureDataProcessUtil;
    private DataProcessService.CommunicationBinder mCommunicationBinder;
    private List<SpannableString> roomStateList;//
    private RoomsStateAdapter mRoomsStateAdapter;
    private RecyclerView rvRoomsState;
//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case ACTION_MSG_GET:
//                    tv_record.append("收到："+msg.obj);
//                    break;
//                case ACTION_MSG_SENG:
//                    tv_record.append("发送："+msg.obj);
//                    et_msg.setText("");
//                    break;
//            }
//        }
//    };
    public static final int ACTION_MSG_GET = 0;
    public static final int ACTION_MSG_SENG = 1;
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCommunicationBinder = (DataProcessService.CommunicationBinder) service;
//            mCommunicationBinder.setUIHandler(mHandler);
            LogUtil.v(tag,"服务与活动绑定");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_send:
                    String msg_content = et_msg.getText().toString()+"\n";
                    mCommunicationBinder.sendMessage(msg_content);
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //初始化
        View contentLayout = inflater.inflate(R.layout.content_layout,container,false);
        tv_linkstate = (TextView) contentLayout.findViewById(R.id.tv_linkstate);
        tv_record = (TextView) contentLayout.findViewById(R.id.tv_record);
        rvRoomsState = (RecyclerView) contentLayout.findViewById(R.id.rv_rooms_state);
        et_msg = (EditText) contentLayout.findViewById(R.id.et_msg);
        btn_send = (Button) contentLayout.findViewById(R.id.btn_send);
        return contentLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        //获取连接状态
        boolean connectstate = getBluetoothConnectState();
        if (connectstate){
            tv_linkstate.setText("已连接.");
        }else {
            tv_linkstate.setText("未连接.");
        }
        mTemperatureDataProcessUtil = new TemperatureDataProcessUtil(getContext(),ExtraDataStorage.currentDeviceAddress+"_temperdata.db");
        roomStateList = new ArrayList<>();
        mRoomsStateAdapter = new RoomsStateAdapter(roomStateList,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvRoomsState.setLayoutManager(layoutManager);
        rvRoomsState.setAdapter(mRoomsStateAdapter);
        //异步加载历史记录
//        new LoadRecordsTask().execute();
        new LoadRecordCommandTask().execute();
        new LoadNewDataTask().execute();
        //重新绑定服务
        if (ExtraDataStorage.isServiceStarted ){
            Intent bindIntent = new Intent(getContext(),DataProcessService.class);
            getActivity().bindService(bindIntent,serviceConnection,Context.BIND_AUTO_CREATE);
            ExtraDataStorage.isServiceBind = true;
        }
    }

    private void init() {
        btn_send.setOnClickListener(listener);
        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(LeftMenuFragment.ACTION_BLUETOOTH_CONNECT);
//        filter.setPriority(50);//低优先级的广播接收器，保证了先实现服务启动，再收到这个广播
        filter.addAction(AdvancedFunctionActivity.ACTION_BLUETOOTH_CONNECT);
        filter.addAction(DataProcessService.ACTION_DATA_STORAGED);
        filter.addAction(DataProcessService.ACTION_MESSAGE_SEND);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        getActivity().registerReceiver(receiver,filter);
    }
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //蓝牙状态改变
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int state = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE);
                if (state == BluetoothAdapter.STATE_OFF){
                    setBluetoothConnectState(false);
                    tv_linkstate.setText("未连接.");
                }
            }
            //蓝牙连接，特指建立通信进程（客户端）
            if (action.equals(LeftMenuFragment.ACTION_BLUETOOTH_CONNECT)){
                LogUtil.v(tag,"contentFragment get ");
                tv_linkstate.setText("连接状态：已连接");
                //*********修改****//
                Intent bindIntent = new Intent(getContext(),DataProcessService.class);
                getActivity().bindService(bindIntent,serviceConnection,Context.BIND_AUTO_CREATE);
//                Intent bindIntent = new Intent(getContext(), TemperatureDataService.class);
//                getActivity().bindService(bindIntent,serviceConnection,Context.BIND_AUTO_CREATE);
                ExtraDataStorage.isServiceBind = true;
                ExtraDataStorage.isConnected = true;
            }
            //蓝牙连接特指建立通信进程（服务端）
            if (action.equals(AdvancedFunctionActivity.ACTION_BLUETOOTH_CONNECT)){
                tv_linkstate.setText("连接状态：已连接");
                Intent bindIntent = new Intent(getContext(),DataProcessService.class);
                getActivity().bindService(bindIntent,serviceConnection,Context.BIND_AUTO_CREATE);
                ExtraDataStorage.isServiceBind = true;
                ExtraDataStorage.isConnected = true;
//                mCommunicationBinder.setUIHandler(mHandler);
            }
            //数据存储动作，收到此动作应该更新UI
            if (action.equals(DataProcessService.ACTION_DATA_STORAGED)){
//                Toast.makeText(getContext(),"数据存储完毕",Toast.LENGTH_SHORT).show();
                new LoadNewDataTask().execute();
            }
            //发送消息动作
            if (action.equals(DataProcessService.EXTRA_MESSAGE_SEND)){
                top.anymore.btim_pro.entity.Message msg = (top.anymore.btim_pro.entity.Message)
                        intent.getSerializableExtra(DataProcessService.EXTRA_MESSAGE_SEND);
                tv_record.append(msg.getDate().toString()+"\n"+msg.getContent()+"\n");
                et_msg.setText("");
            }
            //蓝牙断开连接动作，应该更新界面
            if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
                LogUtil.v(tag,"收到广播:蓝牙连接断开");
                setBluetoothConnectState(false);
                tv_linkstate.setText("未连接.");
            }
            //系统api中的监听连接
            if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_CONNECTED){
                    LogUtil.v(tag,"来自系统API 蓝牙设备连接");
                }
                if (state == BluetoothAdapter.STATE_DISCONNECTED){
                    LogUtil.v(tag,"来自系统Api 蓝牙设备断开连接");
                    setBluetoothConnectState(false);
                    tv_linkstate.setText("未连接.");
                }
            }
        }
    };
    //从数据库中读取历史记录(异步加载)
//    class LoadRecordsTask extends AsyncTask<Void,Void,String>{
//
//        @Override
//        protected String doInBackground(Void... params) {
//            DataProcessUtil dataProcessUtil = new DataProcessUtil(getContext(),"message.db");
//            List<top.anymore.btim_pro.entity.Message> messages = dataProcessUtil.getAllMessage();
//            StringBuilder stringBuilder = new StringBuilder();
//            for (top.anymore.btim_pro.entity.Message message:messages) {
//                if (message.getType() == ACTION_MSG_GET){
//                    stringBuilder.append("收到："+message.getContent()+"\n");
//                }else {
//                    stringBuilder.append("发送："+message.getContent()+"\n");
//                }
//            }
//            return stringBuilder.toString();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            LogUtil.v(tag,"LoadRecordsTask->onPostExecute");
//            super.onPostExecute(s);
//            tv_record.setText(s);
//        }
//    }
    //异步加载新的消息记录
    private class LoadNewDataTask extends AsyncTask<Void,Void,List<SpannableString>>{
        @Override
        protected void onPostExecute(List<SpannableString> spannableStrings) {
            super.onPostExecute(spannableStrings);
            roomStateList.clear();
            roomStateList.addAll(spannableStrings);
            mRoomsStateAdapter.notifyDataSetChanged();
        }

        @Override
        protected List<SpannableString> doInBackground(Void... params) {
            LogUtil.v(tag,"LoadNewDataTask");
            List<SpannableString> spannableStrings = new ArrayList<>();
            SpannableStringBuilder ssb = new SpannableStringBuilder();
            //时间格式转换类
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < ExtraDataStorage.ROOM_NUM; i++) {
                LogUtil.v(tag,"doing");
                List<TemperatureDataEntity> entities = mTemperatureDataProcessUtil.getTemperatureDatas(i,0,2);
//                //test
//                for (TemperatureDataEntity entity:entities) {
//                    LogUtil.v(tag,entity.toString());
//                }
//                //test end
                ssb.append("房间号："+i+"\n");
                for (TemperatureDataEntity entity:entities) {
                    ssb.append(format.format(new Date(entity.getTime()))+"  温度："+entity.getReal_temper()+"℃ 状态： ");
                    if (entity.getIs_dager()==TemperatureDataEntity.STATE_DANGER && entity.getIs_handle() == TemperatureDataEntity.STATE_NOT_HANDLE){
                        SpannableString ss = new SpannableString("异常\n");
                        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
                        ss.setSpan(span,0,2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(ss);
                    }else if (entity.getIs_dager() == TemperatureDataEntity.STATE_DANGER && entity.getIs_handle()== TemperatureDataEntity.STATE_HANDLE){
                        SpannableString ss = new SpannableString("异常已处理\n");
                        ForegroundColorSpan span = new ForegroundColorSpan(Color.BLUE);
                        ss.setSpan(span,0,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(ss);
                    }else {
                        SpannableString ss = new SpannableString("正常\n");
                        ForegroundColorSpan span = new ForegroundColorSpan(Color.GREEN);
                        ss.setSpan(span,0,2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.append(ss);
                    }
                }
                spannableStrings.add(new SpannableString(ssb));
                ssb.clear();
            }
//            roomStateList = spannableStrings;
            return spannableStrings;
        }
    }
    private class LoadRecordCommandTask extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tv_record.append(s);
        }
        //主线程更新界面
        @Override
        protected String doInBackground(Void... params) {
            DataProcessUtil dataProcessUtil = new DataProcessUtil(getContext(),ExtraDataStorage.currentDeviceAddress+"_message.db");
            List<top.anymore.btim_pro.entity.Message> messages = dataProcessUtil.getAllMessage();
            StringBuilder sb = new StringBuilder();
            for (top.anymore.btim_pro.entity.Message m :messages) {
                sb.append(m.getDate()+"\n"+m.getContent()+"\n");
            }
            return sb.toString();
        }
    }

    /**
     * 修改蓝牙连接状态
     * @param state
     */
    private void setBluetoothConnectState(boolean state){
        SharedPreferences.Editor editor = getContext()
                .getSharedPreferences("connectstate",Context.MODE_PRIVATE).edit();
        editor.putBoolean(LeftMenuFragment.BLUETOOTH_CONNECT_STATE,state);
        editor.apply();
    }

    /**
     * 获取蓝牙连接状态
     * @return
     */
    private boolean getBluetoothConnectState(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("connectstate",Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(LeftMenuFragment.BLUETOOTH_CONNECT_STATE,false);
    }
}
