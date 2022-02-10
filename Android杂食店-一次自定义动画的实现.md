---
title: Android杂食店-一次自定义动画的实现
date: 2022-02-09 14:43:16
tags:
cover : false
top_img : https://image-random-clwater.vercel.app/api/random
---

> 半年更系列

> 之前写过不少自定义View及Canvas绘制的文章, 不过都是怎么做, 没有写明为什么要这么做. 刚好这次有了一个比较简单经典的动画, 记录下完成的过去, 希望可以起到一个抛转引玉的效果.

# 最终效果

![preview.gif](https://raw.githubusercontent.com/clwater/AndroidAnimation/master/androidanimation05/preview.gif)


可以看到这个效果还是比较简单的, 是一个模拟日光晃动的效果, 一般出现与和天气有关的场景中.

# 设计思路

设计思路和大家都一样:
* 分解静态动画
* 实现静态动画
* 实现动态动画

关于动画如何动起来. 我一般习惯于先将静态的动画完成, 再重新考虑如何动起来, 虽然看起来影响到之前已经完成的代码. 不过相较于在动态静态一并完成, 这样分开考虑更加的简单. 而且针对与自定义动画的实现来说, 代码量还没有大到无法二次开发的程度. 

## 分解静态动画

简单来说就是将动画分解为一个个最基础的效果, 毕竟代码还是要讲规则的.

需要注意的是, 有的动画在前后的变化过程中, 出现的元素时不一样的.这都是需要一个一个进行分解的. 在这个过程中, 也不能完全规避动态的部分.

简单来说, 如果你的动画中有一个圆, 你可以把它看成一个圆, 也可以把它看成两个半圆拼接而成. 如果动画流程中, 它一直都是一个圆, 那这两个方法倒无差异, 但如果你最后需要在中间加一个矩形使其可以自动填充的话, 一个圆的实现必然不合时宜.(可以想象以下你的未读消息小红点)

这个动画相对来说还是比较简单的, 可以分解为三个颜色相近的圆在一定范围内浮动. 整个流程中也都只需要这三个圆就行了.

## 实现静态动画

如果实现静态动画就仁者见仁智者见智了, 我一般习惯于使用Canvas绘制. 当然这个也和实际情况有关.

具体的内容可以参考后面的代码部分

## 实现动态动画

动态动画简单来说就是让动画动起来(字面意思), 换个说法, 让静态动画在指定的时间上绘制为指定的效果. 动画一般和时间时有关的. 

我更倾向于通过ValueAnimator和Interpolator来完成, 一是系统提供的方法更加的稳定, 二是Interpolator也提供了大量的效果, 足以满足99%的开发需求.(对于我来说, 自定义Interpolator一次也没用过)

这个动画来说, 动态的效果可以控制每个圆的圆心在一定范围内移动即可.

# 代码实现
```java
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
        //这里针对不同的检测值使用了不同的差值器, 理论上一次动画循环的时间为每个差值器
        //循环时间的最小公约数. 但是我们又在差值器一次周期完成后又设置了新的时间.
        //理论上这个重复的周期会变得不易被观测
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

```

# 最后

相关代码可以访问[我的GitHub:https://github.com/clwater/AndroidAnimation/tree/master/androidanimation05](https://github.com/clwater/AndroidAnimation/tree/master/androidanimation05)