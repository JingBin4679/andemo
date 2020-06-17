package com.easedroid.demos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easedroid.demos.list.exo.ExoPlayerActivity;
import com.easedroid.demos.system.NaviBarActivity;
import com.easedroid.demos.system.SystemInfoActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvDemoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvDemoList = findViewById(R.id.rv_demos);
        DemoViewAdapter adapter = new DemoViewAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvDemoList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvDemoList.setLayoutManager(layoutManager);
        rvDemoList.setAdapter(adapter);
        adapter.setData(getList());

    }

    private List<DemoItem> getList() {
        List<DemoItem> arrayList = new ArrayList<>();
        arrayList.add(new DemoItem("ExoPlayer", ExoPlayerActivity.class));
        arrayList.add(new DemoItem("SystemInfo", SystemInfoActivity.class));
        arrayList.add(new DemoItem("SystemBar", NaviBarActivity.class));
        arrayList.add(new DemoItem("Scale", TVScreenAdaptiveActivity.class));
        return arrayList;
    }

    private class DemoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tvName;
        private DemoItem demoItem;

        public DemoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            itemView.setOnClickListener(this);
        }

        public void setItemData(DemoItem demoItem) {
            this.demoItem = demoItem;
            tvName.setText(demoItem.getName());
        }

        @Override
        public void onClick(View v) {
            if (this.demoItem != null) {
                Intent intent = new Intent(MainActivity.this, demoItem.targetActivityClazz);
                startActivity(intent);
            }
        }
    }

    private class DemoViewAdapter extends RecyclerView.Adapter<DemoViewHolder> {

        private final Context mContext;
        private final List<DemoItem> itemList = new ArrayList<>();

        private DemoViewAdapter(Context context) {
            this.mContext = context;
        }

        public void setData(List<DemoItem> data) {
            this.itemList.addAll(data);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public DemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.rc_item, parent, false);
            return new DemoViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull DemoViewHolder holder, int position) {
            DemoItem demoItem = itemList.get(position);
            holder.setItemData(demoItem);
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }

    private class DemoItem {

        private String name;

        private Class<? extends Activity> targetActivityClazz;

        public DemoItem(String name, Class targetActivityClazz) {
            this.name = name;
            this.targetActivityClazz = targetActivityClazz;
        }

        public String getName() {
            return name;
        }

        public Class<? extends Activity> getTargetActivityClazz() {
            return targetActivityClazz;
        }
    }
}
