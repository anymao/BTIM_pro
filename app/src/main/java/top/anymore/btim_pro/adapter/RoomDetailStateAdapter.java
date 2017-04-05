package top.anymore.btim_pro.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import top.anymore.btim_pro.R;
import top.anymore.btim_pro.dataprocess.DataConversionHelper;
import top.anymore.btim_pro.dataprocess.sqlite.TemperatureDataProcessUtil;
import top.anymore.btim_pro.entity.TemperatureDataEntity;
import top.anymore.btim_pro.logutil.LogUtil;

/**
 * Created by anymore on 17-4-4.
 */

public class RoomDetailStateAdapter extends RecyclerView.Adapter<RoomDetailStateAdapter.ViewHolder>{
    private static final String tag = "RoomDetailStateAdapter";
    private List<TemperatureDataEntity> dataEntities;
    private TemperatureDataProcessUtil mTemperatureDataProcessUtil;
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_HEADER = 2;
    private static final int ACTION_REFRESH = 89;
    private View footer,header;
    private Button mbtnLoadMore;
    private ProgressBar mpbLoadMore;
    private AlertDialog.Builder mDialog;
    private Context mContext;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ACTION_REFRESH:
//                    int postion = msg.arg1;
                    LogUtil.v(tag,"genggai postion");
                    dataEntities.add(msg.arg1-1, (TemperatureDataEntity) msg.obj);
                    notifyItemChanged(msg.arg1);
                    break;
            }
        }
    };
    public RoomDetailStateAdapter(Context context,List<TemperatureDataEntity> dataEntities, TemperatureDataProcessUtil temperatureDataProcessUtil) {
        mContext = context;
        this.dataEntities = dataEntities;
        mTemperatureDataProcessUtil = temperatureDataProcessUtil;
        mDialog = new AlertDialog.Builder(mContext);
        mDialog.setTitle("title");
        mDialog.setCancelable(true);
        mDialog.setMessage("are you sure safe?");
        mDialog.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LogUtil.v(tag,"diag no");
                dialog.dismiss();
            }
        });
    }

    public View getFooter() {
        return footer;
    }

    public void setFooter(View footer) {
        this.footer = footer;
        notifyItemChanged(getItemCount()-1);
    }

    public View getHeader() {
        return header;
    }

    public void setHeader(View header) {
        this.header = header;
        notifyItemChanged(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (footer == null || header == null){
            footer = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more,parent,false);
//            notifyItemChanged(getItemCount()-1);
            header = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_detail_header,parent,false);
//            notifyItemChanged(0);
        }
        if (viewType == TYPE_HEADER){
            return new ViewHolder(header);
        }
        if (viewType == TYPE_FOOTER){
            return new ViewHolder(footer);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_detail,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (getItemViewType(position) == TYPE_HEADER){
            return;
        }else if (getItemViewType(position) == TYPE_FOOTER){
            if (dataEntities.isEmpty()){
                holder.btnLoadMore.setText(R.string.nodata);
                holder.btnLoadMore.setEnabled(false);
                return;
            }
            TemperatureDataEntity lastEntity = dataEntities.get(dataEntities.size()-1);
            final int room_id = lastEntity.getRoom_id();
//            final long lastTime = lastEntity.getTime();
            holder.btnLoadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.v(tag,"点击加载更多");
                    new LoadMoreDataTask().execute(new Long(room_id));
                }
            });
        }else {
            final TemperatureDataEntity entity = dataEntities.get(position-1);
            holder.tvDataId.setText(""+position);
            holder.tvDataTime.setText(DataConversionHelper.long2Date(entity.getTime()));
            holder.tvDataTemper.setText(entity.getReal_temper()+"℃");
            if (entity.getIs_dager() == TemperatureDataEntity.STATE_DANGER && entity.getIs_handle() == TemperatureDataEntity.STATE_NOT_HANDLE){
                final int room_id = entity.getRoom_id();
                final long time = entity.getTime();
                final double real_temper = entity.getReal_temper();
                holder.btnState.setText("状态异常");
                holder.btnState.setTextColor(Color.RED);
                holder.btnState.setEnabled(true);
                holder.btnState.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(mContext,"position"+position,Toast.LENGTH_SHORT).show();
//                        //test
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                LogUtil.v(tag,"vvvvvvvvvvvvvvvvv");
//                                ContentValues values = new ContentValues();
//                                values.put("is_handle",TemperatureDataEntity.STATE_HANDLE);
//                                mTemperatureDataProcessUtil.updateData(values,"room_id = ? AND time = ? AND real_temper = ?",new String[]{""+room_id,""+time,""+real_temper});
//                                TemperatureDataEntity temp = entity;
//                                temp.setIs_handle(TemperatureDataEntity.STATE_HANDLE);
//                                Message message = Message.obtain();
//                                message.what = ACTION_REFRESH;
//                                message.arg1 = position;
//                                message.obj = temp;
//                                mHandler.sendMessage(message);
////                                        Toast.makeText(mContext,"change",Toast.LENGTH_SHORT).show();
//                            }
//                        }).start();
//                        //修改数据的状态
                        mDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LogUtil.v(tag,"diag yes");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ContentValues values = new ContentValues();
                                        values.put("is_handle",TemperatureDataEntity.STATE_HANDLE);
                                        mTemperatureDataProcessUtil.updateData(values,"room_id = ? AND time = ? AND real_temper = ?",new String[]{""+room_id,""+time,""+real_temper});
                                        TemperatureDataEntity temp = entity;
                                        temp.setIs_handle(TemperatureDataEntity.STATE_HANDLE);
                                        Message message = Message.obtain();
                                        message.what = ACTION_REFRESH;
                                        message.arg1 = position;
                                        message.obj = temp;
                                        mHandler.sendMessage(message);
//                                        Toast.makeText(mContext,"change",Toast.LENGTH_SHORT).show();
                                    }
                                }).start();
//                                notifyItemChanged(position);
//                                notifyDataSetChanged();
                            }
                        });
                        mDialog.show();
                    }
                });
            }else if (entity.getIs_dager() == TemperatureDataEntity.STATE_DANGER && entity.getIs_handle() == TemperatureDataEntity.STATE_HANDLE){
                holder.btnState.setEnabled(false);
                holder.btnState.setTextColor(Color.BLUE);
                holder.btnState.setText("异常已查看");
            }else {
                holder.btnState.setEnabled(false);
                holder.btnState.setTextColor(Color.GREEN);
                holder.btnState.setText("状态正常");
            }
        }

    }

    @Override
    public int getItemCount() {
        return dataEntities.size()+2;
//        if (footer == null && header == null){
//            return dataEntities.size();
//        }else if (footer == null && header != null){
//            return dataEntities.size()+1;
//        }else if (footer != null && header == null){
//            return dataEntities.size()+1;
//        }else {
//            return dataEntities.size()+2;
//        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_HEADER;
        }
        if (position == getItemCount()-1){
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
//        if (footer == null && header == null){
//            return TYPE_NORMAL;
//        }else if (header != null && position == 0){
//            return TYPE_HEADER;
//        }else if (footer != null && position == getItemCount()-1){
//            return TYPE_FOOTER;
//        }else {
//            return TYPE_NORMAL;
//        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //正常项的布局控件
        TextView tvDataId,tvDataTime,tvDataTemper;
        Button btnState;
        //footer控件
        Button btnLoadMore;
        ProgressBar pbLoadMore;
        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView == footer){
                btnLoadMore = (Button) itemView.findViewById(R.id.btn_load_more);
                pbLoadMore = (ProgressBar) itemView.findViewById(R.id.pb_load_more);
                mbtnLoadMore = btnLoadMore;
                mpbLoadMore = pbLoadMore;
                return;
            }
            if (itemView == header){
                return;
            }
            tvDataId = (TextView) itemView.findViewById(R.id.tv_data_id);
            tvDataTime = (TextView) itemView.findViewById(R.id.tv_data_time);
            tvDataTemper = (TextView) itemView.findViewById(R.id.tv_data_temper);
            btnState = (Button) itemView.findViewById(R.id.btn_state);
        }
    }
    private class LoadMoreDataTask extends AsyncTask<Long,Void,List<TemperatureDataEntity>>{
        @Override
        protected void onPostExecute(List<TemperatureDataEntity> temperatureDataEntities) {
            super.onPostExecute(temperatureDataEntities);
            //echo
//            for (TemperatureDataEntity e :temperatureDataEntities) {
//                LogUtil.v(tag,e.toString());
//            }
            //end
            if (temperatureDataEntities.isEmpty() || temperatureDataEntities == null){
                mpbLoadMore.setVisibility(View.GONE);
                mbtnLoadMore.setText(R.string.nomoredata);
                mbtnLoadMore.setEnabled(false);
                mbtnLoadMore.setVisibility(View.VISIBLE);
                return;
            }

            dataEntities.addAll(temperatureDataEntities);
            notifyDataSetChanged();
            mpbLoadMore.setVisibility(View.GONE);
            mbtnLoadMore.setVisibility(View.VISIBLE);
            if (temperatureDataEntities.size()<20){
                //没有更多数据了
                mbtnLoadMore.setText(R.string.nomoredata);
                mbtnLoadMore.setEnabled(false);
            }else {
                mbtnLoadMore.setEnabled(true);
                mbtnLoadMore.setText(R.string.loadmore);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mbtnLoadMore.setVisibility(View.GONE);
            mpbLoadMore.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<TemperatureDataEntity> doInBackground(Long... params) {
//            LogUtil.v(tag,"background"+params[1]);
            List<TemperatureDataEntity> entities = mTemperatureDataProcessUtil.getDatas(null,"room_id=?",new String[]{""+params[0]},null,null,"time DESC",dataEntities.size()+","+20);
            if (entities.isEmpty()){
                LogUtil.v(tag,"empty");
            }
            for (TemperatureDataEntity e : entities) {
                LogUtil.v(tag,e.toString());
            }
            return entities;
        }
    }
}
