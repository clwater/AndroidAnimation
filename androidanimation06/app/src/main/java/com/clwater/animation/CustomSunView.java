package com.clwater.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * @author: clwater
 */
public class CustomSunView extends View {


    public CustomSunView(Context context) {
        super(context);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setAnimations();
    }

    public CustomSunView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setAnimations();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }


    //当前view使用的宽高
    private int width, height;

    private Paint textPaint;
    private Paint linePaint;

    private String text;

    private int textSize = 300;
    private int textStrokeWidth = 5;
    private int lineStrokeWidth = 30;

    private List<Float> animations = new ArrayList<>();
    private HashMap<Integer, Float> animationEnd = new HashMap<>();

    public void setText(String text) {
        this.text = text;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
        invalidate();
    }

    public void setTextStrokeWidth(int textStrokeWidth) {
        this.textStrokeWidth = textStrokeWidth;
    }

    public void setLineStrokeWidth(int lineStrokeWidth) {
        this.lineStrokeWidth = lineStrokeWidth;
    }

    private void setAnimations(){
        for (int i = 0; i < 50; i++) {
            animations.add(0f);
        }
    }

    public void start(){
        addAnimations(5);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        textPaint = new Paint();
        int layerId = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);

        drawText(canvas);
        // 设置取交集显示
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        linePaint.setXfermode(porterDuffXfermode);
        drawBackground(canvas);

        textPaint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }

    private void drawText(Canvas canvas){
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(textStrokeWidth);
        float textWidth = textPaint.measureText(text);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
//        float textHeight = fontMetrics.descent - fontMetrics.ascent;
        float textHeight = (fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
        canvas.drawText(text, (width - textWidth) / 2f, height / 2f + textHeight, textPaint);
    }

    private void drawBackground(Canvas canvas){
        float textWidth = textPaint.measureText(text);
        float lineStart = width / 2f - textWidth / 2f;
        float lineEnd = width / 2f + textWidth / 2f;
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.aaa);
//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, getWidth(), getHeight(), true);
//        canvas.drawBitmap(scaledBitmap, 5, 5, paint);

//        paint.setColor(Color.WHITE);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(lineStrokeWidth);
//        canvas.drawRect(0,0,width,height, paint);
        float[][] point = new float[3][2];
        linePaint.setColor(Color.BLUE);
        Path path = new Path();
        point[0] = getRandomCirclePoint(width / 8f, height / 2f, width / 16f, height / 16f, animations.get(0), animations.get(1));
        point[1] = getRandomCirclePoint(width / 2f, height / 2f, width / 8f, height / 8f, animations.get(2), animations.get(3));
        point[2] = getRandomCirclePoint(width - width / 8f, height / 2f, width / 16f, height / 16f, animations.get(4), animations.get(5));

        path.moveTo(point[0][0], point[0][1]);
        path.quadTo(point[1][0], point[1][1], point[2][0], point[2][1]);
        canvas.drawPath(path, linePaint);
    }

    private void addAnimations(int max){
        for (int i = 0; i <= max; i++) {
            addAnimation(i);
        }
    }

    private void addAnimation(int index){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f, 0f);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration((long) (7000 * getRandom(8, 10) / 10f));
        valueAnimator.setRepeatCount(-1);
//        valueAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                //重新设置差值范围
//                float _start = animationEnd.get(index);
//                float _end = getRandom(8, 12) / 10f;
//                animationEnd.put(index, _end);
//                valueAnimator.setFloatValues(_start, _end);
//                valueAnimator.start();
//            }
//        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                animations.set(index , (float) valueAnimator.getAnimatedValue());
                invalidate();
            }
        });

        valueAnimator.start();
    }


    private float[] getRandomCirclePoint(float x, float y, float maxX, float maxY, float offsetX, float offsetY){
        float[] point = new float[2];
        point[0] = x + maxX * offsetX;
        point[1] = y + maxY * offsetY;
        return point;
    }


    /**
     * @description 工具方法, 获取一定范围内的随机数
     */
    private int getRandom(int MIN, int MAX){
        Random random = new Random();
        return random.nextInt(MAX - MIN + 1) + MIN;
    }
}
