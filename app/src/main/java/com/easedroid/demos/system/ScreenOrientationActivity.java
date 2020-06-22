package com.easedroid.demos.system;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.easedroid.demos.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;

import java.net.CookieManager;
import java.net.CookiePolicy;

public class ScreenOrientationActivity extends AppCompatActivity {

    public static final String TAG = "ScreenOrientation";
    private RelativeLayout root;

    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private PlayerView playerView;

    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private boolean startAutoPlay = true;
    private DefaultDataSourceFactory dataSourceFactory;

    private String[] uri = {
            "https://zlzpmgrv3.qsyservice.cn/file/dfs/1.0/data/qisyun/150722/20190906/vod/9lmopaV9WkLQ0W8pyeP6JQ.mp4-auto.m3u8",
            "https://zlzpmgrv3.qsyservice.cn/file/dfs/1.0/data/qisyun/150722/20190819/vod/M1H34w8M6wNTzzxpszbJ5w.mp4-auto.m3u8"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_orientation);
        root = findViewById(R.id.root);

        playerView = findViewById(R.id.player_view);
        playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        releasePlayer();
        clearStartPosition();
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
            if (playerView != null) {
                playerView.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            // Empty results are triggered if a permission is requested while another request was already
            // pending and can be safely ignored in this case.
            return;
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializePlayer();
        } else {
            showToast("没有存储权限");
            finish();
        }
    }


    // Activity input

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // See whether the player view wants to handle media or DPAD keys events.
        return playerView.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }


    // Internal methods

    private void initializePlayer() {
        if (player == null) {
            Intent intent = getIntent();


            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(this).build();

            dataSourceFactory =
                    new DefaultDataSourceFactory(this,
                            Util.getUserAgent(this, "ZLZP Sunday/3.0"),
                            bandwidthMeter);

            mediaSource = createTopLevelMediaSource(intent);
            if (mediaSource == null) {
                return;
            }
            RenderersFactory renderersFactory = new DefaultRenderersFactory(this);
            player = new SimpleExoPlayer.Builder(this, renderersFactory)
                    .build();
            player.addListener(new PlayerEventListener());
            player.setPlayWhenReady(startAutoPlay);
            playerView.setPlayer(player);
        }
        player.prepare(mediaSource, false, false);
    }

    @Nullable
    private MediaSource createTopLevelMediaSource(Intent intent) {
        if (uri.length == 0) {
            return null;
        }
        ConcatenatingMediaSource concatenatingMediaSource = new ConcatenatingMediaSource();
        for (String s : uri) {
            MediaSource mediaSource = createLeafMediaSource(Uri.parse(s));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        return new LoopingMediaSource(concatenatingMediaSource);
    }


    private MediaSource createLeafMediaSource(Uri uri) {
        @C.ContentType int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri);
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            updateStartPosition();
            player.release();
            player = null;
            mediaSource = null;
        }
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
    }


    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private class PlayerEventListener implements Player.EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, @Player.State int playbackState) {
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            if (isBehindLiveWindow(e)) {
                clearStartPosition();
                initializePlayer();
            }
        }

        @Override
        @SuppressWarnings("ReferenceEquality")
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        }
    }


    private class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {

        @Override
        public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
            String errorString = "未知错误";
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = e.getRendererException();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                            (MediaCodecRenderer.DecoderInitializationException) cause;
                    if (decoderInitializationException.codecInfo == null) {
                        if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                            errorString = "找不到解码器";
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            errorString = String.format("缺少安全解码器：%s", decoderInitializationException.mimeType);
                        } else {
                            errorString = String.format("没有对应的解码器：%s", decoderInitializationException.mimeType);
                        }
                    } else {
                        errorString =
                                String.format("实例化解码器失败：%s", decoderInitializationException.codecInfo.name);
                    }
                }
            }
            return Pair.create(0, errorString);
        }
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


}
