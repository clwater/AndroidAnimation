package com.clwater.androidanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;


import java.util.Random;

/**
 * @author: clwater
 */
public class CustomSunView extends View {

    //预设的颜色信息
    public enum CustomColor{
        Blue("#dbf3fb", "#cbebfb", "#bbebfb"),
        Yellow("#fbf3e3", "#faf2cb", "#fbebbb"),
        Red("#fbebeb", "#fbe3e3", "#fbd3d3"),
        Pink("#fbebfb", "#fbdbfb", "#fbd3fb");

        private final String color1, color2, color3;

        CustomColor(String color1, String color2, String color3) {
            this.color1 = color1;
            this.color2 = color2;
            this.color3 = color3;
        }
    }

    private CustomColor customColor = CustomColor.Blue;
    //当前view使用的宽高
    private int width, height;
    //差值器
    //可优化, 差值器工厂生成
    private float offset_1_1, offset_1_2, offset_2_1, offset_2_2, offset_3_1, offset_3_2;
    //是否开启动画
    boolean isStart = false;

    public CustomSunView(Context context) {
        super(context);
    }

    public CustomSunView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = View.MeasureSpec.getSize(widthMeasureSpec);
        height = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public void setCustomColor(CustomColor customColor) {
        this.customColor = customColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setAlpha(50);
        paint.setStyle(Paint.Style.FILL);

        //圆的半径
        float radius = height * 0.75f;

        //圆的移动范围
        float baseRand =  height / 32f;

        //依次绘制相应的圆
        //三个圆的圆心不同
        paint.setColor(Color.parseColor(customColor.color1));
        canvas.drawCircle(width + baseRand * offset_1_1,
                0 + baseRand * offset_1_2,
                radius, paint);

        paint.setColor(Color.parseColor(customColor.color2));
        canvas.drawCircle(width - baseRand * 2 + baseRand * offset_2_1,
                0 - baseRand * 2 + baseRand * offset_2_2,
                radius * 0.95f, paint);


        paint.setColor(Color.parseColor(customColor.color3));
        canvas.drawCircle(width + baseRand * 2 + baseRand * offset_3_1,
                0 + baseRand * 2 + baseRand * offset_3_2,
                radius * 0.95f, paint);


        //是否已经开启了动画(差值器)
        if (!isStart){
            isStart = true;
            startAnimator();
        }
    }


    /**
     * @description 工具方法, 获取一定范围内带有正负的随机数
     */
    private float getRandomWithBool(){
        int random = getRandom(14, 6);
        if (getRandom(10, 0) > 5){
            return -random / 1f / 10;
        }else {
            return random / 1f / 10;
        }
    }

    /**
     * @description 工具方法, 获取指定范围内的随机数
     */
    private float getRandom(){
        int random = getRandom(14, 6);
        return random / 1f / 10;
    }

    /**
     * @description 工具方法, 获取一定范围内的随机数
     */
    private int getRandom(int MAX, int MIN){
        Random random = new Random();
        return random.nextInt(MAX - MIN + 1) + MIN;
    }

    /**
     * @description 差值器设定
     */
    public void animators(int index){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, getRandomWithBool(), 0f , getRandomWithBool() , 0f);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration((long) (8 * 1000 * getRandom()));
        valueAnimator.setRepeatCount(1);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //重新设置差值范围
                valueAnimator.setFloatValues(0f, getRandomWithBool(), 0f , getRandomWithBool() , 0f);
                valueAnimator.start();
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                //匹配不同差值
                switch (index){
                    case 0:
                        offset_1_1 = (float) animator.getAnimatedValue();
                        //已第一个差值器变化为页面更新
                        invalidate();
                        break;
                    case 1:
                        offset_1_2 = (float) animator.getAnimatedValue();
                        break;
                    case 2:
                        offset_2_1 = (float) animator.getAnimatedValue();
                        break;
                    case 3:
                        offset_2_2 = (float) animator.getAnimatedValue();
                        break;
                    case 4:
                        offset_3_1 = (float) animator.getAnimatedValue();
                        break;
                    case 5:
                        offset_3_2 = (float) animator.getAnimatedValue();
                        break;
                }
            }
        });
        valueAnimator.start();
    }

    /**
     * @description 开启动画
     */
    public void startAnimator() {
        for (int i = 0; i < 6; i++) {
            animators(i);
        }
    }
}
