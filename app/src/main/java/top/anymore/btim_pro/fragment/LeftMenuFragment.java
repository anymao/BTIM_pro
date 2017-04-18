package top.anymore.btim_pro.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import top.anymore.btim_pro.ExtraDataStorage;
import top.anymore.btim_pro.R;
import top.anymore.btim_pro.activity.AdvancedFunctionActivity;
import top.anymore.btim_pro.adapter.BluetoothDeviceAdapter;
import top.anymore.btim_pro.bluetooth.BluetoothConnectThread;
import top.anymore.btim_pro.bluetooth.BluetoothUtil;
import top.anymore.btim_pro.bluetooth.CommunicationThread;
import top.anymore.btim_pro.bluetooth.CommunicationThreadManager;
import top.anymore.btim_pro.logutil.LogUtil;
import top.anymore.btim_pro.service.DataProcessService;
import top.anymore.btim_pro.service.TemperatureDataService;

/**
 * 抽屉布局的左布局碎片
 * Created by anymore on 17-3-23.
 */

public class LeftMenuFragment extends Fragment {
    private static final String tag = "LeftMenuFragment";
    private SwitchCompat scOpenBluetooth;
    private TextView tvScan,tvPairedDevices, tvAvailableDevices;
    private Button btnScan,btnExit,btnAdvancedFunction;//扫描键和退出按钮
    private RecyclerView rvPairedDevices, rvAvailableDevices;//设备列表
    private BluetoothUtil mBluetoothUtil;
    private List<BluetoothDevice> mPairedDeviceList,mAvailableDeviceList;
    private BluetoothDeviceAdapter mPairedDeviceAdapter,mAvailableDeviceAdapter;
    private BluetoothConnectThread mBluetoothConnectThread;
    public static final String ACTION_BLUETOOTH_CONNECT = "top.anymore.btim_pro.fragment.leftmenufragment.action_bluetooth_connect";
    public static final String BLUETOOTH_CONNECT_STATE = "state";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化一些成员变量
        mBluetoothUtil = BluetoothUtil.getInstance();
        mPairedDeviceList = new ArrayList<>();
        mAvailableDeviceList = new ArrayList<>();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.v(tag,"onCreateView");
        //获取加载指定布局
        View leftMenuLayout = inflater.inflate(R.layout.left_menu_layout,container,false);
        //控件初始化
        scOpenBluetooth = (SwitchCompat) leftMenuLayout.findViewById(R.id.sc_open_bluetooth);
        btnAdvancedFunction = (Button) leftMenuLayout.findViewById(R.id.btn_advanced_function);
        btnScan = (Button) leftMenuLayout.findViewById(R.id.btn_scan);
        btnExit = (Button) leftMenuLayout.findViewById(R.id.btn_exit);
        tvScan = (TextView) leftMenuLayout.findViewById(R.id.tv_scan);
        rvPairedDevices = (RecyclerView) leftMenuLayout.findViewById(R.id.rv_paired_devices);
        rvAvailableDevices = (RecyclerView) leftMenuLayout.findViewById(R.id.rv_available_devices);
        tvPairedDevices = (TextView) leftMenuLayout.findViewById(R.id.tv_paired_devices);
        tvAvailableDevices = (TextView) leftMenuLayout.findViewById(R.id.tv_available_devices);
        return leftMenuLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        initViews();
        registerBroadcastReceiver();
        LogUtil.v(tag,"onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.v(tag,"onResume");
    }

    /**
     * 初始化布局控件
     */
    private void initViews(){
        if (mBluetoothUtil.isBluetoothOpened()){
            scOpenBluetooth.setChecked(true);
            setFunctionClickable(true);
            initDevicesList();
        }
        //设定监听器
        btnAdvancedFunction.setOnClickListener(listener);
        btnExit.setOnClickListener(listener);
        btnScan.setOnClickListener(listener);
        scOpenBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mBluetoothUtil.openBluetooth();
                }else {
                    mBluetoothUtil.closeBluetooth();
                }
            }
        });
    }

    /**
     * 初始化设备列表，这里的设备都是配对过的设备
     */
    private void initDevicesList(){
        mBluetoothConnectThread = new BluetoothConnectThread(getContext());
        //对已配对设备列表的初始化
        mPairedDeviceList = mBluetoothUtil.getPairedDevices();
        mPairedDeviceAdapter = new BluetoothDeviceAdapter(mPairedDeviceList,mBluetoothConnectThread);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        rvPairedDevices.setLayoutManager(layoutManager1);
        rvPairedDevices.setAdapter(mPairedDeviceAdapter);
        //对扫描附近可用设备的初始化
        mAvailableDeviceAdapter = new BluetoothDeviceAdapter(mAvailableDeviceList,mBluetoothConnectThread);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        rvAvailableDevices.setLayoutManager(layoutManager2);
        rvAvailableDevices.setAdapter(mAvailableDeviceAdapter);
    }

    /**
     * 注册广播相关
     */
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        //监听蓝牙状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //监听扫描状态改变
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //监听新设备加入
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        //注册广播
        filter.addAction(BluetoothConnectThread.ACTION_BLUETOOTH_CONNECT);
        //绑定状态
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //监听连接断开
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
//        filter.setPriority(100);
        getActivity().registerReceiver(receiver,filter);
    }
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_advanced_function:
                    Intent intent = new Intent(getContext(), AdvancedFunctionActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_scan:
                    LogUtil.v(tag,"扫描");
                    mBluetoothUtil.startDiscovery();
                    break;
                //整个程序退出，结束服务
                case R.id.btn_exit:
                    LogUtil.v(tag,"退出");
                    if (ExtraDataStorage.isServiceStarted){
                        getActivity().stopService(new Intent(getContext(),DataProcessService.class));
                        ExtraDataStorage.isServiceStarted = false;
                    }
                    setBluetoothConnectState(false);
                    getActivity().finish();
                    break;
            }
        }
    };
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle bundle = intent.getExtras();
            //监听蓝牙状态变化，当其他程序打开或者关闭蓝牙时，界面改变
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int state = bundle.getInt(BluetoothAdapter.EXTRA_STATE);
                if (state == BluetoothAdapter.STATE_ON){
                   scOpenBluetooth.setChecked(true);
                   setFunctionClickable(true);
                   initDevicesList();
                }
                if (state == BluetoothAdapter.STATE_OFF){
                   scOpenBluetooth.setChecked(false);
                   setFunctionClickable(false);
                   setBluetoothConnectState(false);
                }

            }
            //监听当扫描到新设备的广播
            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                LogUtil.v(tag,"发现新设备");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED && !mAvailableDeviceList.contains(device)){
                    mAvailableDeviceList.add(device);
                    mAvailableDeviceAdapter.notifyDataSetChanged();
                }
            }
            //监听当前扫描状态

            //扫描开始
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
//                Drawable drawable = getResources().getDrawable(R.drawable.scan_ing_anim);
                AnimationDrawable animationDrawable = (AnimationDrawable) getResources()
                        .getDrawable(R.drawable.scan_ing_anim);
                btnScan.setBackground(animationDrawable);
                animationDrawable.start();
            }
            //扫描结束
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                Drawable drawable = getResources().getDrawable(R.drawable.scan_btn_bg);
                btnScan.setBackground(drawable);
            }
            //监听配对状态
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,BluetoothDevice.BOND_NONE);
                //配对成功
                if (state==BluetoothDevice.BOND_BONDED){
                    LogUtil.v(tag,"第一次配对成功");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    mAvailableDeviceList.remove(device);
                    mAvailableDeviceAdapter.notifyDataSetChanged();
                    mPairedDeviceList.add(device);
                    mPairedDeviceAdapter.notifyItemChanged(mPairedDeviceList.size()-1);
                }
            }
            //监听连接,特指监听通信线程连接
            if (action.equals(BluetoothConnectThread.ACTION_BLUETOOTH_CONNECT)){
                LogUtil.v(tag,"客户端连入成功");
                setBluetoothConnectState(true);
                BluetoothSocket socket = mBluetoothConnectThread.getBluetoothSocket();
                if (socket == null){
                    LogUtil.v(tag,"GG socket 为空");
                }else {
                    LogUtil.v(tag,"perfect ,good job");
                }
                CommunicationThread communicationThread = new CommunicationThread(mBluetoothConnectThread.getBluetoothSocket());
                CommunicationThreadManager.addCommunicationThread(communicationThread);
                //***********修改逻辑*******************//
                Intent intent1 = new Intent(getContext(), DataProcessService.class);
                getActivity().startService(intent1);
//                Intent intent1 = new Intent(getContext(), TemperatureDataService.class);
//                getActivity().startService(intent1);
                ExtraDataStorage.isServiceStarted = true;
                Intent intent2 = new Intent(ACTION_BLUETOOTH_CONNECT);
                getActivity().sendBroadcast(intent2);
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
                }
            }
        }
    };

    /**
     * 当蓝牙关闭时，屏蔽某些控件的交互，开启时恢复
     * @param enable
     */
    private void setFunctionClickable(boolean enable){
        if (enable){
            btnAdvancedFunction.setEnabled(true);
            btnAdvancedFunction.setTextColor(getResources().getColor(R.color.colorClickable));
            tvScan.setTextColor(getResources().getColor(R.color.colorClickable));
            btnScan.setEnabled(true);
            tvPairedDevices.setVisibility(View.VISIBLE);
            rvPairedDevices.setVisibility(View.VISIBLE);
            tvAvailableDevices.setVisibility(View.VISIBLE);
            rvAvailableDevices.setVisibility(View.VISIBLE);
        }else {
            btnAdvancedFunction.setEnabled(false);
            btnAdvancedFunction.setTextColor(getResources().getColor(R.color.colorUnClickable));
            tvScan.setTextColor(getResources().getColor(R.color.colorUnClickable));
            btnScan.setEnabled(false);
            tvPairedDevices.setVisibility(View.INVISIBLE);
            rvPairedDevices.setVisibility(View.INVISIBLE);
            tvAvailableDevices.setVisibility(View.INVISIBLE);
            rvAvailableDevices.setVisibility(View.INVISIBLE);
        }
    }

    private void setBluetoothConnectState(boolean state){
        SharedPreferences.Editor editor = getContext()
                .getSharedPreferences("connectstate",Context.MODE_PRIVATE).edit();
        editor.putBoolean(BLUETOOTH_CONNECT_STATE,state);
        editor.apply();
    }
    private boolean getBluetoothConnectState(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("connectstate",Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(BLUETOOTH_CONNECT_STATE,false);
    }
}
