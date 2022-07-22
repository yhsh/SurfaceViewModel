package cn.yhsh.textureviewwidget.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author xiayiye5
 * @date 2022/7/21 10:53
 */
public class TextureViewWidget extends TextureView implements TextureView.SurfaceTextureListener {
    public static final String TAG = "TextureViewWidget";
    private Paint mPaint;

    public TextureViewWidget(@NonNull Context context) {
        this(context, null);
    }

    public TextureViewWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextureViewWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(20);
        setSurfaceTextureListener(this);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        Log.d(TAG, "onDrawForeground");
        canvas.drawText("我是后台绘制方法", 100, 100, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure");
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable");
        Canvas canvas = this.lockCanvas();
        Log.d(TAG, "打印画布" + canvas);
        new Thread(() -> {
            canvas.drawText("我是onSurfaceTextureAvailable绘制方法", 100, 100, mPaint);
            TextureViewWidget.this.unlockCanvasAndPost(canvas);
        }).start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureDestroyed");
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureUpdated");
    }
}
