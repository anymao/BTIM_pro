package top.anymore.btim_pro.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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
import top.anymore.btim_pro.dataprocess.sqlite.DataProcessUtil;
import top.anymore.btim_pro.dataprocess.sqlite.TemperatureDataProcessUtil;
import top.anymore.btim_pro.entity.TemperatureDataEntity;
import top.anymore.btim_pro.logutil.LogUtil;
import top.anymore.btim_pro.service.DataProcessService;
import top.anymore.btim_pro.service.TemperatureDataService;

/**
 * Created by anymore on 17-3-23.
 */

public class ContentFragment extends Fragment{
    private static final String tag = "ContentFragment";
    private TextView tv_linkstate,tv_record;
    private EditText et_msg;
    private Button btn_send;
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
        View contentLayout = inflater.inflate(R.layout.content_layout,container,false);
        tv_linkstate = (TextView) contentLayout.findViewById(R.id.tv_linkstate);
//        tv_record = (TextView) contentLayout.findViewById(R.id.tv_record);
        rvRoomsState = (RecyclerView) contentLayout.findViewById(R.id.rv_rooms_state);
        et_msg = (EditText) contentLayout.findViewById(R.id.et_msg);
        btn_send = (Button) contentLayout.findViewById(R.id.btn_send);
        return contentLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
        mTemperatureDataProcessUtil = new TemperatureDataProcessUtil(getContext(),ExtraDataStorage.currentDeviceAddress+"_temperdata.db");
        roomStateList = new ArrayList<>();
        mRoomsStateAdapter = new RoomsStateAdapter(roomStateList,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvRoomsState.setLayoutManager(layoutManager);
        rvRoomsState.setAdapter(mRoomsStateAdapter);
        //异步加载历史记录
//        new LoadRecordsTask().execute();
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
        filter.addAction(LeftMenuFragment.ACTION_BLUETOOTH_CONNECT);
//        filter.setPriority(50);//低优先级的广播接收器，保证了先实现服务启动，再收到这个广播
        filter.addAction(AdvancedFunctionActivity.ACTION_BLUETOOTH_CONNECT);
        filter.addAction(DataProcessService.ACTION_DATA_STORAGED);
        getActivity().registerReceiver(receiver,filter);
    }
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
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
            if (action.equals(AdvancedFunctionActivity.ACTION_BLUETOOTH_CONNECT)){
                tv_linkstate.setText("连接状态：已连接");
                Intent bindIntent = new Intent(getContext(),DataProcessService.class);
                getActivity().bindService(bindIntent,serviceConnection,Context.BIND_AUTO_CREATE);
                ExtraDataStorage.isServiceBind = true;
                ExtraDataStorage.isConnected = true;
//                mCommunicationBinder.setUIHandler(mHandler);
            }
            if (action.equals(DataProcessService.ACTION_DATA_STORAGED)){
//                Toast.makeText(getContext(),"数据存储完毕",Toast.LENGTH_SHORT).show();
                new LoadNewDataTask().execute();
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
                    ssb.append("时间："+format.format(new Date(entity.getTime()))+"        温度："+entity.getReal_temper()+"℃   状态：  ");
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
}
