package com.easedroid.demos;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TVScreenAdaptiveActivity extends AppCompatActivity {

    private static int designWidth = 1920;
    private static int designHeight = 1080;
    private static int screenWidth = 1920;
    private static int screenHeight = 1080;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        setContentView(frameLayout);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        float ratioX = screenWidth * 1.0F / designWidth;
        float ratioY = screenHeight * 1.0F / designHeight;

        FrameLayout root = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(designWidth, designHeight);
        params.gravity = Gravity.LEFT | Gravity.TOP;
        root.setLayoutParams(params);
        root.setBackgroundColor(Color.BLUE);


        frameLayout.addView(root);
        root.setScaleX(ratioX);
        root.setScaleY(ratioY);
        root.setPivotX(0);
        root.setPivotY(0);

        TextView textView = new TextView(this);
        textView.setText("100000");
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(960, 540);
        params1.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        textView.setBackgroundColor(Color.BLACK);
        textView.setTextSize(70);
        textView.setTextColor(Color.WHITE);
        textView.setLayoutParams(params1);

        root.addView(textView);
    }
}
