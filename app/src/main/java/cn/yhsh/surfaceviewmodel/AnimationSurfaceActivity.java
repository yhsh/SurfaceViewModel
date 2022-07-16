package cn.yhsh.surfaceviewmodel;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * @author Zheng Cong
 * @date 2022/7/11 11:07
 */
public class AnimationSurfaceActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        ColumnarSurfaceView surfaceView = findViewById(R.id.surface_view);
//        surfaceView.setPaintColor(0xffff0000);
        surfaceView.setShaderColor(0xff00FA9A, 0xffEE82EE, 0xffFFC0CB);
    }
}
