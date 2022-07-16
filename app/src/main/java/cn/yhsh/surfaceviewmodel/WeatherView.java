package cn.yhsh.surfaceviewmodel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Random;

/**
 * @author xiayiye5
 * @date 2022/7/16 15:10
 */
public class WeatherView extends View {
    private int viewWidthSize = 200;
    private int viewHeightSize = 100;
    /**
     * view控件的左右边距
     */
    private int viewMargin = 20;
    /**
     * 矩形的默认宽度
     */
    private int rectWidth = 40;
    private Paint mPaint;
    private int measuredHeight;
    private int measuredWidth;
    private Rect textRect;
    private RectF weatherRectF;
    private Random random;
    private String windName;

    public WeatherView(Context context) {
        this(context, null);
    }

    public WeatherView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(16);
        textRect = new Rect();
        weatherRectF = new RectF();
        random = new Random();
        windName = "东南风";
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int countWidth = (measuredWidth - viewMargin * 2) / 24;
        Log.d("打印正方形大小", countWidth + "=");
        if (measuredWidth < measuredHeight) {
            //矩形宽度自己动态适配横竖屏
            rectWidth = (measuredWidth - viewMargin * 2) / 24 - 10;
            mPaint.setTextSize(12);
            mPaint.setColor(Color.BLUE);
        }
        canvas.drawLine(viewMargin, measuredHeight - viewMargin, measuredWidth - viewMargin, measuredHeight - viewMargin, mPaint);
        canvas.drawLine(viewMargin, measuredHeight - viewMargin, viewMargin, viewMargin, mPaint);
        canvas.drawLine(measuredWidth - viewMargin, measuredHeight - viewMargin, measuredWidth - viewMargin, viewMargin, mPaint);
        canvas.drawLine(viewMargin, measuredHeight - viewMargin - countWidth, measuredWidth - viewMargin, measuredHeight - viewMargin - countWidth, mPaint);
        mPaint.getTextBounds(windName, 0, windName.length(), textRect);
        int bottomWidth = textRect.width();
        int bottomHeight = textRect.height();
        for (int i = 0; i < 24; i++) {
            String windLevel = i + "级";
            mPaint.getTextBounds(windLevel, 0, windLevel.length(), textRect);
            int w = textRect.width();
            int h = textRect.height();
            //让文字左右居中显示的算法,画东西南北风和风级
            canvas.drawText(windLevel, viewMargin + countWidth * i + (countWidth - w) / 2f, measuredHeight - viewMargin - countWidth / 2f, mPaint);
            canvas.drawText(windName, viewMargin + countWidth * i + (countWidth - bottomWidth) / 2f, measuredHeight - viewMargin - countWidth / 4f, mPaint);
            //画矩形
            if (i == 0) {
                weatherRectF.left = viewMargin + (countWidth - rectWidth) * (i + 1) / 2f;
            } else {
                weatherRectF.left = weatherRectF.left + rectWidth + (countWidth - rectWidth);
            }
            Log.d("打印左边距", weatherRectF.left + "");
            weatherRectF.right = weatherRectF.left + rectWidth;
            int heightCount = (measuredHeight - viewMargin) / rectWidth;
            Log.d("打印矩形高度分多少个", heightCount + "");
            int nextInt = random.nextInt(heightCount - 1);
            while (true) {
                if (nextInt <= 1) {
                    nextInt = random.nextInt(heightCount - 1);
                }
                break;
            }
            weatherRectF.top = measuredHeight - viewMargin - countWidth - rectWidth * nextInt;
            weatherRectF.bottom = measuredHeight - viewMargin - countWidth;
            canvas.drawRect(weatherRectF, mPaint);

            //矩形上面画雨水量
            String rainText = nextInt + "mm";
            mPaint.getTextBounds(rainText, 0, rainText.length(), textRect);
            canvas.drawText(rainText, viewMargin + countWidth * i + (countWidth - textRect.width()) / 2f, measuredHeight - viewMargin - countWidth - rectWidth * nextInt - 4, mPaint);
            if (i == 0) {
                continue;
            }
            canvas.drawLine(viewMargin + countWidth * i, measuredHeight - viewMargin, viewMargin + countWidth * i + 1, viewMargin, mPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取宽度的测量模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        //获取高度的测量模式和大小
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int viewMeasureWidth = measureViewSize(widthMode, widthSize);
        int viewMeasureHeight = measureViewSize(heightMode, heightSize);
        Log.d("打印测量之前的宽高", widthSize + "=" + heightSize);
        setMeasuredDimension(viewMeasureWidth, viewMeasureHeight);
    }

    private int measureViewSize(int widthMode, int widthSize) {
        //判断宽高的测量模式
        if (widthMode == MeasureSpec.AT_MOST) {
            //未设置大小，例如：wrap_content
            return viewWidthSize;
        } else if (widthMode == MeasureSpec.EXACTLY) {
            //设置了大小，例如：20dp
            return widthSize;
        } else {
            //可以无限大小
            return widthSize;
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取控件的宽高
        measuredHeight = getMeasuredHeight();
        measuredWidth = getMeasuredWidth();
        Log.d("打印控件宽高", measuredHeight + "=" + measuredWidth);
    }
}
