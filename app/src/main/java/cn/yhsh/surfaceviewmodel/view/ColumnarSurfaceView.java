package cn.yhsh.surfaceviewmodel.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.yhsh.surfaceviewmodel.R;

/**
 * @author xiayiye5
 * @date 2022/7/11 10:03
 */
public class ColumnarSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    //每一个能量柱的宽度
    private static int rectWidth = 30;
    //每一个能量柱之间的间距
    private int rectSpace = 2;
    //能量块高度
    private int blockHeight = 5;
    //能量块下将速度
    private int blockSpeed = 3;
    //能量块与能量柱之间的距离
    private int distance = 2;

    private final Paint paint = new Paint();
    private final List<Rect> newData = new ArrayList<>();
    private boolean isLoop;
    private SurfaceHolder surfaceHolder;
    byte[] bytes = new byte[20];
    Random random = new Random();
    private ExecutorService executorService;
    private final CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 200) {
        @Override
        public void onTick(long millisUntilFinished) {
            startDrawable(executorService);
        }

        @Override
        public void onFinish() {
            countDownTimer.start();
        }
    };

    public ColumnarSurfaceView(Context context) {
        this(context, null);
    }

    public ColumnarSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColumnarSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ColumnarSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColumnarSurfaceView);
        rectWidth = (int) typedArray.getDimension(R.styleable.ColumnarSurfaceView_rect_width, 30);
        rectSpace = (int) typedArray.getDimension(R.styleable.ColumnarSurfaceView_rect_space, 2);
        typedArray.recycle();
        initView();
    }

    private void initView() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Log.e("打印生命周期", "surfaceCreated");
        executorService = Executors.newSingleThreadExecutor();
        startDrawable(executorService);
        countDownTimer.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Log.e("打印生命周期", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        Log.e("打印生命周期", "surfaceDestroyed：");
        isLoop = false;
        countDownTimer.cancel();
        if (null != executorService) {
            executorService.shutdown();
            Log.d("打印线程", "线程状态：" + executorService.isShutdown());
        }
    }

    private void startDrawable(ExecutorService executorService) {
        executorService.execute(() -> {
            Log.d("打印线程", Thread.currentThread().getName());
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) random.nextInt(128);
            }
            setWaveData(bytes);
            drawRectContent(newData);
        });
    }

    public void setWaveData(byte[] data) {
        isLoop = true;
        Rect rect;
        for (int i = 0; i < data.length; i++) {
            if (newData.size() == 0) {
                for (byte datum : data) {
                    rect = new Rect();
                    if (newData.size() == 0) {
                        rect.left = 35;
                    } else {
                        rect.left = newData.get(newData.size() - 1).right + rectSpace;
                    }
                    rect.top = 720 - datum * 3;
                    rect.right = rect.left + rectWidth + rectSpace;
                    rect.bottom = 720;
                    Log.e("打印矩形数据", rect.left + "=" + rect.top + "==" + rect.right + "==" + rect.bottom);
                    newData.add(rect);
                }
            } else {
                rect = newData.get(i);
                rect.top = 720 - data[i] * 3;
            }
        }

    }


    private void drawRectContent(List<Rect> newData) {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (null != canvas) {
            for (int i = 0; i < newData.size(); i++) {
                if (i == 0) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                }
                canvas.drawRect(newData.get(i), paint);
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e("打印生命周期", "onSizeChanged");
//        paint.setShader(new LinearGradient(0f, 0f, getWidth(), getHeight(),
//                new int[]{0xffff0000, 0xff00ff00, 0xff0000ff},
//                new float[]{0, 0.5f, 1f}, Shader.TileMode.CLAMP));
    }

    public void setPaintColor(@ColorInt int color) {
        if (null != paint) {
            paint.setColor(color);
        }
    }

    public void setShaderColor(@ColorInt int... colors) {
        if (null != paint) {
            int[] intColors = new int[colors.length];
            System.arraycopy(colors, 0, intColors, 0, colors.length);
            this.post(() -> {
                Log.e("打印宽高1", getWidth() + "==" + getHeight());
                Log.e("打印宽高2", getMeasuredWidth() + "==" + getMeasuredHeight());
                paint.setShader(new LinearGradient(0f, 0f, getWidth(), getHeight(), intColors,
                        new float[]{0, 0.5f, 1f}, Shader.TileMode.CLAMP));
            });
        }

    }
}
