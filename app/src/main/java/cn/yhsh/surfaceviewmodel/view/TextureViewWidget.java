package cn.yhsh.surfaceviewmodel.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.yhsh.surfaceviewmodel.R;
import cn.yhsh.surfaceviewmodel.constant.AppConstant;
import cn.yhsh.surfaceviewmodel.utils.VisualizerHelper;


/**
 * @author xiayiye5
 * @date 2022/7/21 10:53
 * TextureView是为了解决SurfaceView在多个fragment切换的时候一直附着在window窗口上不隐藏的问题
 */
public class TextureViewWidget extends TextureView implements TextureView.SurfaceTextureListener, VisualizerHelper.OnVisualizerEnergyCallBack {
    public static final String TAG = "TextureViewWidget";
    //每一个能量柱的宽度
    private static int rectWidth = 30;
    //每一个能量柱之间的间距
    private int rectSpace = 2;
    private int rectHeightMin = 8;
    private int rectHeightMax = 80;
    private int rectColor;

    private final Paint mPaint = new Paint();
    private final List<RectF> newData = new ArrayList<>();
    private ExecutorService executorService;

    public TextureViewWidget(@NonNull Context context) {
        this(context, null);
    }

    public TextureViewWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextureViewWidget(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextureViewWidget);
        rectWidth = (int) typedArray.getDimension(R.styleable.TextureViewWidget_rect_texture_width, 30);
        rectSpace = (int) typedArray.getDimension(R.styleable.TextureViewWidget_rect_texture_space, 5);
        rectHeightMin = (int) typedArray.getDimension(R.styleable.TextureViewWidget_rect_texture_height_min, 8);
        rectHeightMax = (int) typedArray.getDimension(R.styleable.TextureViewWidget_rect_texture_height_max, 80);
        rectColor = typedArray.getColor(R.styleable.TextureViewWidget_rect_texture_color, Color.parseColor("#4D2A354B"));
        typedArray.recycle();
        initView();
    }

    private void initView() {
        mPaint.setTextSize(20);
        mPaint.setColor(rectColor);
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
        executorService = Executors.newSingleThreadExecutor();
        startDrawable();
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureDestroyed");
        if (null != executorService) {
            executorService.shutdown();
            Log.d("打印线程", "线程状态：" + executorService.isShutdown());
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureUpdated");
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
        Canvas canvas = TextureViewWidget.this.lockCanvas();
        if (null != canvas) {
            for (int i = 0; i < newData.size(); i++) {
                if (i == 0) {
                    //每次绘制一排矩形后清空下画布
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                    //再次设置textureView的背景色为白色
                    canvas.drawColor(Color.WHITE);
                }
                canvas.drawRect(newData.get(i), mPaint);
            }
            TextureViewWidget.this.unlockCanvasAndPost(canvas);
        }
    }

    public void setPaintColor(@ColorInt int color) {
        this.rectColor = color;
        mPaint.setColor(rectColor);
    }

    public void setShaderColor(@ColorInt int... colors) {
        int[] intColors = new int[colors.length];
        System.arraycopy(colors, 0, intColors, 0, colors.length);
        this.post(() -> mPaint.setShader(new LinearGradient(0f, 0f, getWidth(), getHeight(), intColors,
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
        Log.e("打印TextureViewWidget拿到的值", listData.toString());
        float min = Collections.min(listData);
        float max = Collections.max(listData);
        max = Math.max(max, 20);
        float heightScale = ((rectHeightMax - rectHeightMin) * 1.0f) / (max - min);
//        Log.e("打印数据", "=最大值：" + max + "=最小值：" + min + "=比例：" + heightScale);
        RectF rect;
        if (rectWidth == 30) {
            //证明未自定义矩形的宽度，因为拿到view的宽度进行动态计算出矩形的宽度
            rectWidth = (getWidth() - rectSpace * (AppConstant.LUMP_COUNT - 1)) / AppConstant.LUMP_COUNT;
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
