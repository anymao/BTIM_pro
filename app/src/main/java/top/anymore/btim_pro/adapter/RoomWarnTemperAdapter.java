package top.anymore.btim_pro.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import top.anymore.btim_pro.R;
import top.anymore.btim_pro.dataprocess.WarnTemperature;

/**
 * 这个是与RightFragment中recylerView的适配器
 * Created by anymore on 17-4-5.
 */

public class RoomWarnTemperAdapter extends RecyclerView.Adapter<RoomWarnTemperAdapter.ViewHolder>{
    private Context mContext;

    //我用了一个全局变量将每个房间的警戒温度存储在一个数组中，当这个温度改变的时候，会通过SharedPreferences写入
    //以达到持久化的目的
    public RoomWarnTemperAdapter(Context mContext) {
        this.mContext = mContext;
        //初始化获取警戒温度
        WarnTemperature.initWarnTempers(mContext);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warn_temper,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //设置进度条范围0-200
        holder.sbWarnTemper.setMax(200);
        //获取指定房间的警戒温度
        int progress = (int) WarnTemperature.warnTempers[position];
        //设置警戒温度
        holder.sbWarnTemper.setProgress(progress);
        holder.tvRoomId.setText("room: "+position);
        holder.tvWarnTemper.setText(progress+"℃");
        //设置进度条拖动监听
        holder.sbWarnTemper.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //当进度条拖动时候
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                holder.tvWarnTemper.setText(progress+"℃");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            //当进度条停止拖动的时候
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int endProgress = seekBar.getProgress();
                WarnTemperature.changeWarnTemper(mContext,position,endProgress);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return WarnTemperature.warnTempers.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvRoomId;
        SeekBar sbWarnTemper;
        TextView tvWarnTemper;
        public ViewHolder(View itemView) {
            super(itemView);
            tvRoomId = (TextView) itemView.findViewById(R.id.tv_room_id);
            sbWarnTemper = (SeekBar) itemView.findViewById(R.id.sb_warn_temper);
            tvWarnTemper = (TextView) itemView.findViewById(R.id.tv_warn_temper);
        }
    }
}
