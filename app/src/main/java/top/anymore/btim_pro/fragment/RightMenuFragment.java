package top.anymore.btim_pro.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import top.anymore.btim_pro.R;
import top.anymore.btim_pro.activity.AboutActivity;
import top.anymore.btim_pro.adapter.RoomWarnTemperAdapter;
import top.anymore.btim_pro.dataprocess.WarnTemperature;

/**
 * Created by anymore on 17-3-23.
 */

public class RightMenuFragment extends Fragment {
    private RecyclerView rvWarnTempers;
    private Button btnAbout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.right_menu_layout,container,false);
        rvWarnTempers = (RecyclerView) view.findViewById(R.id.rv_warn_tempers);
        btnAbout = (Button) view.findViewById(R.id.btn_about);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AboutActivity.class));
            }
        });
        RoomWarnTemperAdapter adapter = new RoomWarnTemperAdapter(getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvWarnTempers.setLayoutManager(layoutManager);
        rvWarnTempers.setAdapter(adapter);
    }
}
