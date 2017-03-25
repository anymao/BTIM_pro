package top.anymore.btim_pro.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import top.anymore.btim_pro.R;

/**
 * Created by anymore on 17-3-24.
 */

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder>{
    private List<BluetoothDevice> mDeviceList;

    public BluetoothDeviceAdapter(List<BluetoothDevice> mDeviceList) {
        this.mDeviceList = mDeviceList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
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
