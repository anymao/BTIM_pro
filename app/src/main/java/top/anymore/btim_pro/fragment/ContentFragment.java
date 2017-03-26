package top.anymore.btim_pro.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import top.anymore.btim_pro.R;
import top.anymore.btim_pro.bluetooth.CommunicationThreadManager;
import top.anymore.btim_pro.logutil.LogUtil;
import top.anymore.btim_pro.service.DataProcessService;

/**
 * Created by anymore on 17-3-23.
 */

public class ContentFragment extends Fragment{
    private static final String tag = "ContentFragment";
    private TextView tv_unlink,tv_record;
    private EditText et_msg;
    private Button btn_send;
    private DataProcessService.CommunicationBinder mCommunicationBinder;
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCommunicationBinder = (DataProcessService.CommunicationBinder) service;
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
        tv_unlink = (TextView) contentLayout.findViewById(R.id.tv_unlink);
        tv_record = (TextView) contentLayout.findViewById(R.id.tv_record);
        et_msg = (EditText) contentLayout.findViewById(R.id.et_msg);
        btn_send = (Button) contentLayout.findViewById(R.id.btn_send);
        return contentLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }

    private void init() {
        btn_send.setOnClickListener(listener);
        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(LeftMenuFragment.ACTION_BLUETOOTH_CONNECT);
        getActivity().registerReceiver(receiver,filter);
    }
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(LeftMenuFragment.ACTION_BLUETOOTH_CONNECT)){
                LogUtil.v(tag,"contentFragment get ");
                tv_unlink.setVisibility(View.GONE);
                Intent bindIntent = new Intent(getContext(),DataProcessService.class);
                getActivity().bindService(bindIntent,serviceConnection,Context.BIND_AUTO_CREATE);
            }
        }
    };
}
