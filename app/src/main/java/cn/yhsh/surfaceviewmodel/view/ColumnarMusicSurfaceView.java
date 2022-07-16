package cn.yhsh.surfaceviewmodel.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.yhsh.surfaceviewmodel.R;
import cn.yhsh.surfaceviewmodel.utils.VisualizerHelper;

/**
 * @author Zheng Cong
 * @date 2022/7/11 10:03
 * 在次感谢原作者提供获取音波图数据的方法 https://github.com/michael007js/SpectrumForAndroid
 */
public class ColumnarMusicSurfaceView extends SurfaceView implements SurfaceHolder.Callback, VisualizerHelper.OnVisualizerEnergyCallBack {
    //每一个能量柱的宽度
    private static int rectWidth = 30;
    //每一个能量柱之间的间距
    private int rectSpace = 2;
    private int rectHeightMin = 8;
    private int rectHeightMax = 80;
    private int rectColor;
    //能量块高度
    private int blockHeight = 5;
    //能量块下将速度
    private int blockSpeed = 3;
    //能量块与能量柱之间的距离
    private int distance = 2;

    private final Paint paint = new Paint();
    private final List<RectF> newData = new ArrayList<>();
    private SurfaceHolder surfaceHolder;
    private ExecutorService executorService;


    public ColumnarMusicSurfaceView(Context context) {
        this(context, null);
    }

    public ColumnarMusicSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColumnarMusicSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ColumnarMusicSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColumnarMusicSurfaceView);
        rectWidth = (int) typedArray.getDimension(R.styleable.ColumnarMusicSurfaceView_rect_music_width, 30);
        rectSpace = (int) typedArray.getDimension(R.styleable.ColumnarMusicSurfaceView_rect_music_space, 5);
        rectHeightMin = (int) typedArray.getDimension(R.styleable.ColumnarMusicSurfaceView_rect_music_height_min, 8);
        rectHeightMax = (int) typedArray.getDimension(R.styleable.ColumnarMusicSurfaceView_rect_music_height_max, 80);
        rectColor = typedArray.getColor(R.styleable.ColumnarMusicSurfaceView_rect_music_color, Color.parseColor("#4D2A354B"));
        typedArray.recycle();
        initView();
    }

    private void initView() {
        paint.setColor(rectColor);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setZOrderOnTop(true);
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        Log.e("打印生命周期", "surfaceCreated");
        executorService = Executors.newSingleThreadExecutor();
        startDrawable();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        Log.e("打印生命周期", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        Log.e("打印生命周期", "surfaceDestroyed：");
        if (null != executorService) {
            executorService.shutdown();
            Log.d("打印线程", "线程状态：" + executorService.isShutdown());
        }
    }

    private void startDrawable() {
        if (null == executorService || executorService.isShutdown()) {
            return;
        }
        executorService.execute(() -> {
//            Log.d("打印线程", Thread.currentThread().getName());
            drawRectContent(newData);
        });
    }

    private void drawRectContent(List<RectF> newData) {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (null != canvas) {
            for (int i = 0; i < newData.size(); i++) {
                if (i == 0) {
                    //每次绘制一排矩形后清空下画布
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
        this.rectColor = color;
        paint.setColor(rectColor);
    }

    public void setShaderColor(@ColorInt int... colors) {
        int[] intColors = new int[colors.length];
        System.arraycopy(colors, 0, intColors, 0, colors.length);
        this.post(() -> paint.setShader(new LinearGradient(0f, 0f, getWidth(), getHeight(), intColors,
                new float[]{0, 0.5f, 1f}, Shader.TileMode.CLAMP)));

    }

    private final List<Byte> listData = new ArrayList<>();

    @Override
    public void setWaveData(byte[] data, float totalEnergy) {
        if (getWidth() == 0) {
            return;
        }
        listData.clear();
        for (byte datum : data) {
            listData.add(datum);
        }
        Log.e("打印拿到的值", listData.toString());
        float min = Collections.min(listData);
        float max = Collections.max(listData);
        max = Math.max(max, 20);
        float heightScale = ((rectHeightMax - rectHeightMin) * 1.0f) / (max - min);
//        Log.e("打印数据", "=最大值：" + max + "=最小值：" + min + "=比例：" + heightScale);
        RectF rect;
        if (rectWidth == 30) {
            //证明未自定义矩形的宽度，因为拿到view的宽度进行动态计算出矩形的宽度           rectWidth = (getWidth() - rectSpace * (AppConstant.LUMP_COUNT - 1)) / AppConstant.LUMP_COUNT;
        }
//        Log.e("打印控件的总宽度", getWidth() + "==");
//        Log.d("打印柱状图宽度", rectWidth + "");
        for (int i = 0; i < data.length; i++) {
            if (newData.size() == 0) {
                for (byte datum : data) {
                    rect = new RectF();
                    if (newData.size() == 0) {
                        rect.left = 0;
                    } else {
                        rect.left = newData.get(newData.size() - 1).right + rectSpace;
                        Log.d("打印矩形左边距", rect.left + "");
                    }
                    rect.top = (getHeight() - datum * heightScale - rectHeightMin);
                    rect.right = rect.left + rectWidth + rectSpace * i;
                    rect.bottom = getHeight();
                    newData.add(rect);
                }
            } else {
                rect = newData.get(i);
                rect.top = (getHeight() - data[i] * heightScale - rectHeightMin);
//                Log.e("打印矩形数据", rect.top + ":" + data[i] * heightScale);
            }
        }
        startDrawable();
    }
}
