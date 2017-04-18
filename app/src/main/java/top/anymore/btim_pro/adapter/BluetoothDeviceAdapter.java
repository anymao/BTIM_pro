package top.anymore.btim_pro.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.anymore.btim_pro.R;
import top.anymore.btim_pro.bluetooth.BluetoothConnectThread;

/**
 * 这个类是LeftFragment中的RecylerView的适配器
 * Created by anymore on 17-3-24.
 */
public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder>{
    private List<BluetoothDevice> mDeviceList;//数据源
    private BluetoothConnectThread mBluetoothConnectThread;//连接线程
    private Map<BluetoothDevice,Boolean> isConnected;//判断某一个设备是否发起览连接请求
    public BluetoothDeviceAdapter(List<BluetoothDevice> mDeviceList,BluetoothConnectThread mBluetoothConnectThread) {
        this.mDeviceList = mDeviceList;
        this.mBluetoothConnectThread = mBluetoothConnectThread;
        isConnected = new HashMap<>();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.device_item,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDevice device = mDeviceList.get(holder.getAdapterPosition());
                if (isConnected.get(device) == null){//判断是否发起了连接请求
                    mBluetoothConnectThread.setTarget(device);
                    mBluetoothConnectThread.start();
                    isConnected.put(device,Boolean.TRUE);
                }else {
                    Toast.makeText(context,"正在连接，请稍后",Toast.LENGTH_SHORT).show();
                }

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BluetoothDevice device = mDeviceList.get(position);
        holder.tvDeviceName.setText(device.getName());
        if (device.getBondState() == BluetoothDevice.BOND_BONDED){
            holder.tvDeviceState.setText("已配对");
        }else {
            holder.tvDeviceState.setText("可连接");
        }

    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvDeviceName;
        TextView tvDeviceState;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDeviceName = (TextView) itemView.findViewById(R.id.tv_device_name);
            tvDeviceState = (TextView) itemView.findViewById(R.id.tv_device_state);
        }
    }
}
