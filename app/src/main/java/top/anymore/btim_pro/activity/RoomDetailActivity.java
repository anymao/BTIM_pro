package top.anymore.btim_pro.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import top.anymore.btim_pro.service.DataProcessService;

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

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(DataProcessService.ACTION_DATA_STORAGED);
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void init() {
        srlRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_fresh_layout);
        rvRoomDetail = (RecyclerView) findViewById(R.id.rv_room_detail);
        getSupportActionBar().setTitle("Room "+roomId+" 历史概况");
//        View footer = LayoutInflater.from(this).inflate(R.layout.item_load_more,null,false);
//        View header = LayoutInflater.from(this).inflate(R.layout.item_room_detail_header,null,false);
        dataEntities = new ArrayList<>();
        mTemperatureDataProcessUtil = new TemperatureDataProcessUtil(this, ExtraDataStorage.currentDeviceAddress+"_temperdata.db");
        mRoomDetailStateAdapter = new RoomDetailStateAdapter(this,dataEntities,mTemperatureDataProcessUtil);
//        mRoomDetailStateAdapter.setHeader(header);
//        mRoomDetailStateAdapter.setFooter(footer);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvRoomDetail.setLayoutManager(layoutManager);
        rvRoomDetail.setAdapter(mRoomDetailStateAdapter);
        new LoadDataTask().execute();
        srlRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new PullDownFreshTask().execute();
            }
        });


    }

    private class LoadDataTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            List<TemperatureDataEntity> entities;
            //加载前20条数据
            entities = mTemperatureDataProcessUtil.getTemperatureDatas(roomId,0,20);
            //添加数据
            dataEntities.addAll(entities);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mRoomDetailStateAdapter.notifyDataSetChanged();
        }
    }
    private class PullDownFreshTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            List<TemperatureDataEntity> entities;
            if (dataEntities.isEmpty()){
                entities = mTemperatureDataProcessUtil.getTemperatureDatas(roomId);
                dataEntities.addAll(entities);
                LogUtil.v(tag,"下拉刷新完毕");
                return null;
            }
            long lastTime = dataEntities.get(0).getTime();
            entities = mTemperatureDataProcessUtil.getDatas(null,"room_id = ? AND time > ?",new String[]{""+roomId,""+lastTime},null,null,"time ASC",null);
            for (TemperatureDataEntity e : entities) {
                dataEntities.add(0,e);
            }
            LogUtil.v(tag,"下拉刷新完毕");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (srlRefreshLayout.isRefreshing()){
                srlRefreshLayout.setRefreshing(false);
            }
            mRoomDetailStateAdapter.notifyDataSetChanged();
        }
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DataProcessService.ACTION_DATA_STORAGED)){
                new PullDownFreshTask().execute();
            }
        }
    };
}
