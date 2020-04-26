package com.easedroid.demos.system;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.easedroid.demos.R;
import com.easedroid.demos.Utils;

import java.util.ArrayList;
import java.util.List;

public class SystemInfoActivity extends AppCompatActivity {

    private RecyclerView rvList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sysinfo);
        rvList = findViewById(R.id.rv_infos);
        ViewAdapter adapter = new ViewAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvList.setLayoutManager(layoutManager);
        rvList.setAdapter(adapter);
        adapter.setData(buildItems());
    }

    private List<Pair<String, Object>> buildItems() {
        ArrayList<Pair<String, Object>> list = new ArrayList<>();
        Pair<String, Object> pair = new Pair<String, Object>("应用分辨率", Utils.getAppDisplayResolution(this));
        list.add(pair);
        String screenResolution = "-";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            screenResolution = Utils.getScreenResolution(this);
        }
        pair = new Pair<String, Object>("设备分辨率", screenResolution);
        list.add(pair);
        return list;
    }

    private class ViewAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final Context mContext;
        private final List<Pair<String, Object>> itemList = new ArrayList<>();

        private ViewAdapter(Context context) {
            this.mContext = context;
        }

        public void setData(List<Pair<String, Object>> data) {
            this.itemList.addAll(data);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.info_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Pair<String, Object> item = itemList.get(position);
            holder.setItemData(item);
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvValue;
        private Pair<String, Object> item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvValue = itemView.findViewById(R.id.tv_value);
        }

        public void setItemData(Pair<String, Object> item) {
            this.item = item;
            tvName.setText(item.first);
            tvValue.setText(item.second.toString());
        }
    }
}
