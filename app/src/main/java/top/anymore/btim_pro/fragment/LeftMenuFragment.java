package top.anymore.btim_pro.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import top.anymore.btim_pro.R;
import top.anymore.btim_pro.logutil.LogUtil;

/**
 * Created by anymore on 17-3-23.
 */

public class LeftMenuFragment extends Fragment {
    private static final String tag = "LeftMenuFragment";
    private SwitchCompat scOpenBluetooth,scAdvancedFunction;//两个开关
    private Button btnScan,btnExit;//扫描键和退出按钮
    private RecyclerView rvDevices;//设备列表
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.v(tag,"onCreateView");
        //获取加载指定布局
        View leftMenuLayout = inflater.inflate(R.layout.left_menu_layout,container,false);
        //控件初始化及设定监听器
        scOpenBluetooth = (SwitchCompat) leftMenuLayout.findViewById(R.id.sc_open_bluetooth);
        scAdvancedFunction = (SwitchCompat) leftMenuLayout.findViewById(R.id.sc_advanced_function);
        btnScan = (Button) leftMenuLayout.findViewById(R.id.btn_scan);
        btnExit = (Button) leftMenuLayout.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(listener);
        btnScan.setOnClickListener(listener);
        return leftMenuLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.v(tag,"onStart");
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
            }
        }
    };
}
