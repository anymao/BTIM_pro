package top.anymore.btim_pro.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.Toast;

import top.anymore.btim_pro.ExtraDataStorage;
import top.anymore.btim_pro.R;
import top.anymore.btim_pro.bluetooth.BluetoothServerThread;
import top.anymore.btim_pro.bluetooth.BluetoothUtil;
import top.anymore.btim_pro.bluetooth.CommunicationThread;
import top.anymore.btim_pro.bluetooth.CommunicationThreadManager;
import top.anymore.btim_pro.fragment.LeftMenuFragment;
import top.anymore.btim_pro.service.DataProcessService;
import top.anymore.btim_pro.service.TemperatureDataService;

public class AdvancedFunctionActivity extends AppCompatActivity {
    private static final String tag = "AdvancedFunctionActivity";
    private BluetoothUtil mBluetoothUtil;
    private SwitchCompat scOpenServer,scVisiable;//开启服务功能开关和允许被发现开关
    private BluetoothServerThread mBluetoothServerThread;//连接进程
    public static final String ACTION_BLUETOOTH_CONNECT = "top.anymore.btim_pro.fragment.advancedfunctionactivity.action_bluetooth_connect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_function);
        mBluetoothUtil = BluetoothUtil.getInstance();
        //初始化控件状态
        scOpenServer = (SwitchCompat) findViewById(R.id.sc_open_server);
        scVisiable = (SwitchCompat) findViewById(R.id.sc_visible);
        //初始化控件状态，通过静态变量来判断服务是否开启
        if (ExtraDataStorage.isServerOpen){
            scOpenServer.setChecked(true);
        }else {
            scOpenServer.setChecked(false);
        }
        //读取上一次的状态
        if (ExtraDataStorage.isOpenToFind){
            scVisiable.setChecked(true);
        }else {
            scVisiable.setChecked(false);
        }
        //设置监听器
        scOpenServer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mBluetoothServerThread = new BluetoothServerThread(AdvancedFunctionActivity.this);
                    mBluetoothServerThread.start();
                    ExtraDataStorage.isServerOpen = true;
                }else {
                    ExtraDataStorage.isServerOpen = false;
                    mBluetoothServerThread = null;
                }
            }
        });
        scVisiable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mBluetoothUtil.setVisibility(isChecked);
                    ExtraDataStorage.isOpenToFind = isChecked;
                }
            }
        });
        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        //扫描模式改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        //连接状态监听，特指建立通信进程
        filter.addAction(BluetoothServerThread.ACTION_BLUETOOTH_CONNECT);
//        filter.setPriority(100);
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册广播接收器
        unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                Bundle bundle = intent.getExtras();
                int mode = bundle.getInt(BluetoothAdapter.EXTRA_SCAN_MODE);
                if (mode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                    scVisiable.setChecked(true);
                    ExtraDataStorage.isOpenToFind = true;
                }else {
                    scOpenServer.setChecked(false);
                    ExtraDataStorage.isOpenToFind = false;
                }
            }
            //客户端连接动作
            if (action == BluetoothServerThread.ACTION_BLUETOOTH_CONNECT){
                Toast.makeText(AdvancedFunctionActivity.this,"客户端连入成功...",Toast.LENGTH_SHORT).show();
                CommunicationThread thread= new CommunicationThread(mBluetoothServerThread.getBluetoothSocket());
                CommunicationThreadManager.addCommunicationThread(thread);
                setBluetoothConnectState(true);
//                ExtraDataStorage.isConnected = true;
                //启动后台通信进程
                Intent intent1 = new Intent(AdvancedFunctionActivity.this, DataProcessService.class);
                startService(intent1);
//                Intent intent1 = new Intent(AdvancedFunctionActivity.this, TemperatureDataService.class);
//                startService(intent1);
                ExtraDataStorage.isServiceStarted = true;
                Intent intent2 = new Intent(ACTION_BLUETOOTH_CONNECT);
                //通知主页面：客户端连接进来了
                sendBroadcast(intent2);
            }
        }
    };

    /**
     * 为了将蓝牙的连接动作持久化保存，我选择了使用SharedPreferences，
     * 以便于在重新回到前台时候能够正确获得蓝牙连接状态
     * @param state
     */
    private void setBluetoothConnectState(boolean state){
        SharedPreferences.Editor editor = getSharedPreferences("connectstate",Context.MODE_PRIVATE).edit();
        editor.putBoolean(LeftMenuFragment.BLUETOOTH_CONNECT_STATE,state);
        editor.apply();
    }
}
