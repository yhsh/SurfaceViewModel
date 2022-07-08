package cn.yhsh.surfaceviewmodel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

/**
 * @author Zheng Cong
 * @date 2022/7/8 13:46
 */
public class SurfaceViewModel extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private static final String TAG = "SurfaceViewModel";
    // SurfaceHolder
    private SurfaceHolder mHolder;
    // 用于绘图的Canvas
    private Canvas mCanvas;
    // 子线程标志位
    private boolean mIsDrawing;
    private Paint mPaint;
    private Path mPath;
    private int x = 0;
    private int y = 0;

    public SurfaceViewModel(Context context) {
        super(context);
        initView();
    }

    public SurfaceViewModel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SurfaceViewModel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public SurfaceViewModel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        ColorMatrix colorMatrix = new ColorMatrix();
        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        mPaint.setColorFilter(colorFilter);
        mPaint.setTextSize(30);
        mPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();

    }

    @Override
    public void run() {
        while (mIsDrawing) {
            drawPage();
            x += 1;
            y = (int) (100 * Math.cos(x * 2 * Math.PI / 180) + 400);
            mPath.lineTo(x, y);
        }
    }

    private void drawPage() {
        try {
            mCanvas = mHolder.lockCanvas();
            if (null != mCanvas) {
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                mCanvas.drawPath(mPath, mPaint);
            }
//            mCanvas.drawLines(new float[]{0, 100, 200, 400}, mPaint);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != mCanvas) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        mIsDrawing = true;
        mPath.moveTo(0, 400);
        //创建的时候开启子线程
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        mIsDrawing = false;
    }
}
