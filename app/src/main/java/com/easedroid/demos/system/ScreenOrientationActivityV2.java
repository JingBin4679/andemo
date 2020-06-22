package com.easedroid.demos.system;

import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.easedroid.demos.R;

import java.io.IOException;

public class ScreenOrientationActivityV2 extends AppCompatActivity {

    public static final String TAG = "ScreenOrientation";
    private RelativeLayout root;

    private String[] uri = {
            "https://zlzpmgrv3.qsyservice.cn/file/dfs/1.0/data/qisyun/150722/20190906/vod/9lmopaV9WkLQ0W8pyeP6JQ.mp4-auto.m3u8",
            "https://zlzpmgrv3.qsyservice.cn/file/dfs/1.0/data/qisyun/150722/20190819/vod/M1H34w8M6wNTzzxpszbJ5w.mp4-auto.m3u8"
    };
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_orientation_v2);
        root = findViewById(R.id.root);
        View surfaceView = getSurfaceView();
        root.addView(surfaceView, 0);
        initPlayer();
    }

    private View getSurfaceView() {
        View surfaceView = new TextureView(this);
        if (surfaceView instanceof SurfaceView) {
            SurfaceHolder holder = ((SurfaceView) surfaceView).getHolder();
            holder.setFormat(PixelFormat.TRANSLUCENT);
            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    Log.i(TAG, "surfaceCreated");
                    _onInitialized(holder);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    Log.i(TAG, "surfaceChanged");
                    _onInitialized(holder);
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    Log.i(TAG, "surfaceDestroyed");
                    _onSurfaceDestroyed();
                }
            });
        } else if (surfaceView instanceof TextureView) {
            ((TextureView) surfaceView).setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    _onInitialized(new Surface(surface));
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                    _onInitialized(new Surface(surface));
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    _onSurfaceDestroyed();
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                    _onInitialized(new Surface(surface));
                }
            });
        }

        return surfaceView;
    }

    private void initPlayer() {

        releasePlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG, "onPrepared");
                mp.start();// play when ready
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "onCompletion");
                mp.start(); // 从头开始
            }
        });
        Uri uri = Uri.parse("https://zlzpmgrv3.qsyservice.cn/file/dfs/1.0/data/qisyun/150722/20190906/vod/9lmopaV9WkLQ0W8pyeP6JQ.mp4-auto.m3u8");
        try {
            mediaPlayer.setDataSource(this, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    private void _onSurfaceDestroyed() {
        if (mediaPlayer != null) {
            mediaPlayer.setDisplay(null);
        }
    }

    private void _onInitialized(SurfaceHolder holder) {
        if (mediaPlayer == null) {
            Log.w(TAG, "mediaPlayer is null");
            return;
        }
        if (holder == null) {
            Log.w(TAG, "SurfaceHolder is null");
            return;
        }
        mediaPlayer.setDisplay(holder);
    }

    private void _onInitialized(Surface surface) {
        if (mediaPlayer == null) {
            Log.w(TAG, "mediaPlayer is null");
            return;
        }
        if (surface == null) {
            Log.w(TAG, "Surface is null");
            return;
        }
        mediaPlayer.setSurface(surface);
    }

    public void rotateNormal(View view) {
        this.rotate("none");
    }

    public void rotateVFlip(View view) {
        this.rotate("vflip");
    }

    public void rotateRight(View view) {
        this.rotate("right");
    }

    public void rotateLeft(View view) {
        this.rotate("left");
    }

    public void rotate(String option) {
        if (TextUtils.isEmpty(option)) {
            return;
        }
        View decorView = getWindow().getDecorView();
        int windowWidth = decorView.getWidth();
        int windowHeight = decorView.getHeight();
        float rotate = 0F;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
        switch (option) {
            case "left":
                layoutParams.height = windowWidth;
                layoutParams.width = windowHeight;
                layoutParams.leftMargin = (windowWidth - windowHeight) / 2;
                layoutParams.topMargin = (windowHeight - windowWidth) / 2;
                rotate = -90F;
                break;
            case "right":
                layoutParams.height = windowWidth;
                layoutParams.width = windowHeight;
                layoutParams.leftMargin = (windowWidth - windowHeight) / 2;
                layoutParams.topMargin = (windowHeight - windowWidth) / 2;
                rotate = 90F;
                break;
            case "vflip":
                layoutParams.height = -1;
                layoutParams.width = -1;
                layoutParams.leftMargin = 0;
                layoutParams.topMargin = 0;
                rotate = 180F;
                break;
            default:
                layoutParams.height = -1;
                layoutParams.width = -1;
                layoutParams.leftMargin = 0;
                layoutParams.topMargin = 0;
                rotate = 0F;
                break;
        }
        root.setRotation(rotate);
        root.setLayoutParams(layoutParams);
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnPreparedListener(null);
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
