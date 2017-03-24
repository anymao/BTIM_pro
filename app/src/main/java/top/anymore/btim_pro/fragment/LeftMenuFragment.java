package top.anymore.btim_pro.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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

import top.anymore.btim_pro.R;
import top.anymore.btim_pro.adapter.BluetoothDeviceAdapter;
import top.anymore.btim_pro.bluetooth.BluetoothUtil;
import top.anymore.btim_pro.logutil.LogUtil;

/**
 * Created by anymore on 17-3-23.
 */

public class LeftMenuFragment extends Fragment {
    private static final String tag = "LeftMenuFragment";
    private SwitchCompat scOpenBluetooth,scAdvancedFunction;//两个开关
    private TextView tvScan;
    private Button btnScan,btnExit;//扫描键和退出按钮
    private RecyclerView rvDevices;//设备列表
    private BluetoothUtil mBluetoothUtil;
    private List<BluetoothDevice> mDeviceList;
    private BluetoothDeviceAdapter mBluetoothDeviceAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothUtil = new BluetoothUtil();
        mDeviceList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.v(tag,"onCreateView");
        //获取加载指定布局
        View leftMenuLayout = inflater.inflate(R.layout.left_menu_layout,container,false);
        //控件初始化
        scOpenBluetooth = (SwitchCompat) leftMenuLayout.findViewById(R.id.sc_open_bluetooth);
        scAdvancedFunction = (SwitchCompat) leftMenuLayout.findViewById(R.id.sc_advanced_function);
        btnScan = (Button) leftMenuLayout.findViewById(R.id.btn_scan);
        btnExit = (Button) leftMenuLayout.findViewById(R.id.btn_exit);
        tvScan = (TextView) leftMenuLayout.findViewById(R.id.tv_scan);
        rvDevices = (RecyclerView) leftMenuLayout.findViewById(R.id.rv_devices);
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
        mDeviceList = mBluetoothUtil.getPairedDevices();
        mBluetoothDeviceAdapter = new BluetoothDeviceAdapter(mDeviceList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvDevices.setLayoutManager(layoutManager);
        rvDevices.setAdapter(mBluetoothDeviceAdapter);
    }

    /**
     * 注册广播相关
     */
    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        //监听蓝牙状态改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //注册广播
        getActivity().registerReceiver(receiver,filter);
    }
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_scan:
                    LogUtil.v(tag,"扫描");
                    break;
                case R.id.btn_exit:
                    LogUtil.v(tag,"退出");
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
            scAdvancedFunction.setClickable(true);
            scAdvancedFunction.setTextColor(getResources().getColor(R.color.colorClickable));
            tvScan.setTextColor(getResources().getColor(R.color.colorClickable));
            btnScan.setClickable(true);
        }else {
            scAdvancedFunction.setClickable(false);
            scAdvancedFunction.setTextColor(getResources().getColor(R.color.colorUnClickable));
            tvScan.setTextColor(getResources().getColor(R.color.colorUnClickable));
            btnScan.setClickable(false);
        }
    }
}