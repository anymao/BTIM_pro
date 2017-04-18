package top.anymore.btim_pro.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import top.anymore.btim_pro.R;
import top.anymore.btim_pro.activity.RoomDetailActivity;
import top.anymore.btim_pro.logutil.LogUtil;

/**
 * 这个是ContentFragment的RecyclerView的适配器
 * Created by anymore on 17-4-3.
 */

public class RoomsStateAdapter extends RecyclerView.Adapter<RoomsStateAdapter.ViewHolder> {
    private static final String tag = "RoomsStateAdapter";
    private List<SpannableString> strings;//数据源
    private Context mContext;
    public RoomsStateAdapter(List<SpannableString> strings,Context context) {
        this.strings = strings;
        mContext = context;
        LogUtil.v(tag,"构造器");
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_state,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SpannableString spannableString = strings.get(position);
        LogUtil.v(tag,spannableString.toString()+"position"+position);
        holder.tvRoomState.setText(spannableString);
        holder.tvRoomState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到RoomDetailActivity中，并且携带roomid作为唯一识别符号
                Intent intent = new Intent(mContext, RoomDetailActivity.class);
                intent.putExtra("room_id",position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvRoomState;
        public ViewHolder(View itemView) {
            super(itemView);
            tvRoomState = (TextView) itemView.findViewById(R.id.tv_room_state);
        }
    }
}
