package cn.yhsh.surfaceviewmodel;

import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import cn.yhsh.surfaceviewmodel.utils.VisualizerHelper;
import cn.yhsh.surfaceviewmodel.view.ColumnarMusicSurfaceView;
import cn.yhsh.surfaceviewmodel.view.ColumnarView;


public class ColumnarActivity extends AppCompatActivity {
    private ColumnarView columnar;
    private Visualizer visualizer;
    private MediaPlayer player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_columnar);
        columnar = findViewById(R.id.columnar);
        ColumnarMusicSurfaceView columnarSurfaceView = findViewById(R.id.columnar_surface_view);
        VisualizerHelper.getInstance().addCallBack(columnarSurfaceView);
        VisualizerHelper.getInstance().addCallBack(columnar);
        ((SeekBar) findViewById(R.id.seek_bar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress == 0) {
                        return;
                    }
                    columnar.setBlockSpeed(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        VisualizerHelper.getInstance().removeCallBack(columnar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VisualizerHelper.getInstance().addCallBack(columnar);
    }

    public void showAnim(View view) {
        if (null != player) {
            return;
        }
        player = MediaPlayer.create(this, R.raw.shangcheng);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        player.setLooping(true);
        player.start();

        int mediaPlayerId = player.getAudioSessionId();
        if (visualizer == null) {
            visualizer = new Visualizer(mediaPlayerId);
        } else {
            visualizer.release();
        }
        //可视化数据的大小： getCaptureSizeRange()[0]为最小值，getCaptureSizeRange()[1]为最大值
        int captureSize = Visualizer.getCaptureSizeRange()[1];
        int captureRate = Visualizer.getMaxCaptureRate() * 3 / 4;

        visualizer.setCaptureSize(captureSize);
        visualizer.setDataCaptureListener(VisualizerHelper.getInstance().getDataCaptureListener(), captureRate, true, true);
        visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
        visualizer.setEnabled(true);
    }
}
