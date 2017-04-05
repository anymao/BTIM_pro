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
 * Created by anymore on 17-4-5.
 */

public class RoomWarnTemperAdapter extends RecyclerView.Adapter<RoomWarnTemperAdapter.ViewHolder>{
    private Context mContext;

    public RoomWarnTemperAdapter(Context mContext) {
        this.mContext = mContext;
        WarnTemperature.initWarnTempers(mContext);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_warn_temper,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.sbWarnTemper.setMax(200);
        int progress = (int) WarnTemperature.warnTempers[position];
        holder.sbWarnTemper.setProgress(progress);
        holder.tvRoomId.setText("room: "+position);
        holder.tvWarnTemper.setText(progress+"℃");
        holder.sbWarnTemper.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                holder.tvWarnTemper.setText(progress+"℃");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

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
