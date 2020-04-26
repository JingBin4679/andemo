package com.easedroid.demos.system;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.easedroid.demos.R;

public class NaviBarActivity extends AppCompatActivity {

    boolean status = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_bar);
        View btnSystemBar = findViewById(R.id.btn_toggle_systembar);
        btnSystemBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = !status;
                if (status) {
                    setNavigationAndStatusBar(getWindow());
                } else {
                    getWindow().getDecorView().setSystemUiVisibility(0);
                }
            }
        });
    }


    public static void setNavigationAndStatusBar(Window window) {
        int systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(systemUiVisibility);
    }
}
