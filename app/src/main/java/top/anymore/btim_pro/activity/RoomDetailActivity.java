package top.anymore.btim_pro.activity;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import top.anymore.btim_pro.ExtraDataStorage;
import top.anymore.btim_pro.R;
import top.anymore.btim_pro.adapter.RoomDetailStateAdapter;
import top.anymore.btim_pro.dataprocess.sqlite.TemperatureDataProcessUtil;
import top.anymore.btim_pro.entity.TemperatureDataEntity;
import top.anymore.btim_pro.logutil.LogUtil;

public class RoomDetailActivity extends AppCompatActivity {
    private static final String tag = "RoomDetailActivity";
    private static final int TYPE_UP_FRESH = 0;
    private static final int TYPE_INIT_DATA = 1;
    private ProgressBar pbLoading;
    private RecyclerView rvRoomDetail;
    private SwipeRefreshLayout srlRefreshLayout;
    private int roomId;
    private List<TemperatureDataEntity> dataEntities;
    private TemperatureDataProcessUtil mTemperatureDataProcessUtil;
    private RoomDetailStateAdapter mRoomDetailStateAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);
        roomId = getIntent().getIntExtra("room_id",-1);
        if (roomId == -1){
            LogUtil.v(tag,"room_is == -1 excuse me?");
            finish();
        }
        init();
    }

    private void init() {
//        srlRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_fresh_layout);
        rvRoomDetail = (RecyclerView) findViewById(R.id.rv_room_detail);
        View footer = LayoutInflater.from(this).inflate(R.layout.item_load_more,null,false);
        View header = LayoutInflater.from(this).inflate(R.layout.item_room_detail_header,null,false);
        dataEntities = new ArrayList<>();
        mTemperatureDataProcessUtil = new TemperatureDataProcessUtil(this, ExtraDataStorage.currentDeviceAddress+"_temperdata.db");
        mRoomDetailStateAdapter = new RoomDetailStateAdapter(dataEntities,mTemperatureDataProcessUtil);
//        mRoomDetailStateAdapter.setHeader(header);
//        mRoomDetailStateAdapter.setFooter(footer);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvRoomDetail.setLayoutManager(layoutManager);
        rvRoomDetail.setAdapter(mRoomDetailStateAdapter);
        new LoadDataTask().execute(TYPE_INIT_DATA);

    }

    private class LoadDataTask extends AsyncTask<Integer,Void,Void>{

        @Override
        protected Void doInBackground(Integer... params) {
            List<TemperatureDataEntity> entities = new ArrayList<>();
            if (params[0] == TYPE_INIT_DATA){//加载前20条数据
                entities = mTemperatureDataProcessUtil.getTemperatureDatas(roomId,0,20);
                dataEntities.addAll(entities);
            }else if (params[0] == TYPE_UP_FRESH){
                //上拉刷新逻辑
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mRoomDetailStateAdapter.notifyDataSetChanged();
        }
    }
}
