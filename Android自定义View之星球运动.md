# Android自定义View 星球运动

>在dribbble闲逛的时候发现的一个有意思的星球运动的动画,刚好最近时间尚可,就简单实现了一下中间运动的部分,又是因为时间的原因,开头位移的部分没有完成.

![dribbble中发现的动画](http://ooymoxvz4.bkt.clouddn.com/18-2-24/19201885.jpg)
这是在dribbble中发现的动画

![自己实现的动画](http://ooymoxvz4.bkt.clouddn.com/18-2-24/92078484.jpg)
这是我自己实现的效果...  总觉得我这个星球有点胖...  因为胖所以转的慢么这是.速度等细节还有优化的余地

## 设计过程
老办法,先分解动画的构成.整个动画可以看做是一个自旋的星球从右上角由小变大的移动到屏幕的中央的.

星球的位移及缩放不说(其实是最近有需求,暂时没时间完善),主要完善了星球的旋转及尾部的处理.

最底层是背景的星星闪烁,每次在星球一定范围内随机出现,并缩放就好

最开始设计尾部效果的时候,是在没列中设计了两端线.再不断的运行及移动.但是实现起来很乱.最后采用了先绘制所有尾部展示的内容,然后在用和背景一样的颜色部分遮盖并移动此部分形成视觉上的效果的方法.(也可以设置PorterDuff模式来展示).设计过程中的效果如下

![尾部效果1](http://ooymoxvz4.bkt.clouddn.com/18-2-24/93671679.jpg)

![尾部效果2](http://ooymoxvz4.bkt.clouddn.com/18-2-24/22878685.jpg)

星球的设计,星球的本身使用简单的遮盖和贝塞尔曲线就能完成一个较为满意的星球背景.

![星球背景](http://ooymoxvz4.bkt.clouddn.com/18-2-24/67915702.jpg)

重点是星球地表的设计以及星球自转下的地表样式的移动.解决的方法是是先绘制三个重复并连续的地表样式,通过移动整个地表样式模拟星球的转动.最后通过PorterDuff来控制展示的部分和星球的位置重合.

未开启PorterDuff模式时绘制的样式如下:

![地貌的设置](http://ooymoxvz4.bkt.clouddn.com/18-2-24/86724391.jpg)

开启PorterDuff模式后再指定位置展示指定形状的图形如下:

![开启PorterDuff模式](http://ooymoxvz4.bkt.clouddn.com/18-2-24/99450653.jpg)

最后再移动设置好的星球地貌就可以模拟出星球转动的效果了

## 代码实现
### 背景的星星
```java
private fun drawStarts(canvas: Canvas, perIndexInAll: Float) {
    //背景的星星在星球附近的一定范围内随机出现
    val maxRand = 800

    canvas.translate(-maxRand / 2F , -maxRand / 2F)
    val Random = Random(perIndexInAll.toInt().toLong())

    //绘制背景的星星
    for (index in 0..4){
        drawStart(canvas ,  Random.nextFloat() * maxRand , Random.nextFloat() * maxRand , perIndex)
    }

    canvas.translate(maxRand / 2F , maxRand / 2F)
}

//绘制背景的星星内容
//绘制背景的星星内容
private fun drawStart(canvas: Canvas, x: Float, y: Float, per: Float) {
    var per = per
    //这个部分是为了让星星实现从小到大后再从大到小的变动
    if (per >= 1.0F){
        per -= 1F
    }
    if (per <= 0.5F){
        per *= 2
    }else{
        per = (1 - per) * 2
    }

    canvas.save()
    canvas.translate(x , y)

    canvas.scale(per , per)

    val paint = Paint()
    paint.color = 0xff78D8DF.toInt()

    val startLength = 30F
    val startOffset = startLength / 3F

    //通过路径描绘星星的形状
    val path = Path()
    path.moveTo(0F , startLength)
    path.lineTo(startOffset , startOffset )
    path.lineTo(startLength , 0F)
    path.lineTo(startOffset  , -startOffset )
    path.lineTo(0F , -startLength)
    path.lineTo(-startOffset  , -startOffset )
    path.lineTo(-startLength , 0F)
    path.lineTo(-startOffset  , startOffset )
    path.lineTo(0F , startLength)

    canvas.drawPath(path , paint)

    paint.color = viewBackgroundColor
    //通过缩小绘制星星内部形状
    canvas.scale(0.3F , 0.3F)
    canvas.drawPath(path , paint)

    canvas.restore()
}
```

### 星球外部
```java
private fun drawGas(canvas: Canvas, index: Float) {
    canvas.save()
    canvas.rotate(45F)

    val gasWidth = 18F
    val baseR = baseR * 0.7F
    val absBaseR = baseR / 5F

    val paint = Paint()
    paint.strokeWidth = gasWidth
    paint.style = Paint.Style.STROKE
    paint.color = 0xff2F3768.toInt()

    val paintArc = Paint()
    paintArc.color = 0xff2F3768.toInt()

    val gasLength = baseR * 2F
    canvas.save()

    val gsaL = gasWidth / 2F * 3
    var maxGasLength = (gasLength + gsaL ) / 2
    var index = index

    canvas.scale(1F , -1F)

    //绘制星球后面的气流情况
    //舍不得那么多定义好的变量
    //又不想写个参数很多的函数,就这么实现了
    canvas.save()
    canvas.translate(baseR , baseR * 1.2F)
    canvas.translate(0F , absBaseR)
    //drawLines函数一个绘制两头带半圆的线段
    drawLines(0F, maxGasLength, canvas, paint)
    drawWhite( maxGasLength * index, gasWidth , gsaL * 2 , canvas)
    drawWhite( maxGasLength * (index - 1 ) * 1.1F, gasWidth , gsaL * 2 , canvas)
    drawWhite( maxGasLength * (index + 1 ) * 1.1F, gasWidth , gsaL * 2 , canvas)
    canvas.restore()

    index = index + 0.3F

    //.....没有写函数就不上重复的代码了

    val rectf = RectF(-baseR , -baseR , baseR ,baseR)
    canvas.drawArc(rectf , 0F , 180F , false , paint)

    canvas.drawLine(baseR ,0F ,  baseR ,  -baseR, paint)
    canvas.drawLine(-baseR ,0F ,  -baseR ,  -baseR, paint)

    canvas.restore()
}

//绘制尾部空白部分
private fun drawWhite(offset: Float, gasWidth: Float, gsaL : Float , canvas: Canvas) {
    val r = gasWidth / 2F

    canvas.save()
    canvas.translate( 0F , offset - 2 * gsaL )

    val pointPaint = Paint()
    pointPaint.strokeWidth = 20F
    pointPaint.color = Color.RED

    //通过贝塞尔曲线绘制半圆效果
    val path = Path()
    path.moveTo(-r , gsaL)
    path.cubicTo(
            - r * C ,  gsaL - r,
            r * C ,  gsaL - r,
            r , gsaL
    )

    path.lineTo(r , - gsaL)
    path.cubicTo(
            r * C ,  - gsaL + r,
            -r * C ,  - gsaL + r,
            -r , - gsaL
    )

    path.lineTo(-r , gsaL * 1.5F)

    val paint = Paint()
    paint.color = viewBackgroundColor
    canvas.drawPath(path , paint)

    canvas.restore()
}
```

### 星球

```java
private fun drawPlanet(canvas: Canvas , index : Float) {
    //设置原图层
    val srcB = makeSrc(index)
    //设置遮罩层
    //遮罩层只有一和星球大小一样的圆
    val dstB = makeDst(index)

    val paint = Paint()
    canvas.saveLayer(-baseR, -baseR, baseR , baseR, null, Canvas.ALL_SAVE_FLAG)
    //绘制遮罩层
    canvas.drawBitmap(dstB,  -baseR / 2F, -baseR / 2F , paint)
    //设置遮罩模式为SRC_IN显示原图层中原图层与遮罩层相交部分
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(srcB, width / -2F, height / -2F , paint)
    paint.xfermode = null
}


//设置源图层
fun makeSrc(index :Float): Bitmap {
    val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bm)
    canvas.translate(width.toFloat() / 2F , height.toFloat() / 2F)

    val paint = Paint()
    paint.color = 0xff57BEC6.toInt()
    paint.style = Paint.Style.FILL

    val rectf = RectF(-baseR / 2F, -baseR / 2F, baseR / 2F, baseR / 2F)
    canvas.drawArc(rectf , 0F , 360F , true , paint)

    canvas.save()


    //绘制星球背景
    paint.color = 0xff78D7DE.toInt()
    var baseR = baseR * 0.9.toFloat()
    val rectf2 = RectF(-baseR / 2F, -baseR / 2F, baseR / 2F, baseR / 2F)
    canvas.translate(baseR / 6F , baseR / 6F)
    canvas.drawArc(rectf2 , 0F , 360F , true , paint)

    canvas.restore()
    canvas.rotate(-45F)
    canvas.save()

    val bottomBaseR = baseR / 0.9F / 2
    val path = Path()
    path.moveTo(-bottomBaseR , 0F)
    path.cubicTo(-bottomBaseR , bottomBaseR * 2, bottomBaseR  , bottomBaseR * 2, bottomBaseR , 0F)

    path.cubicTo(
            bottomBaseR * C,bottomBaseR ,
            -bottomBaseR * C,bottomBaseR ,
            -bottomBaseR , 0F
    )

    //绘制星球背景的阴影效果
    paint.color = 0xffAAEEF2.toInt()
    paint.style = Paint.Style.FILL
    canvas.drawPath(path , paint)

    //绘制星球的地貌
    drawPoints(index , canvas)

    canvas.restore()

    paint.strokeWidth = 30F
    paint.color = 0xff2F3768.toInt()
    paint.style = Paint.Style.STROKE
    canvas.drawArc(rectf , 0F , 360F , true , paint)

    return bm
}

private fun drawPoints(index: Float, canvas: Canvas) {
        val paintB = Paint()
        val paintS = Paint()
        paintS.style = Paint.Style.FILL
        paintS.color = 0xffE7F2FB.toInt()

        paintB.style = Paint.Style.FILL
        paintB.color = 0xff2F3768.toInt()

        val baseRB = baseR / 2F / 3
        val baseRS = baseR / 2F / 3 / 3

        val rectfB = RectF(-baseRB, -baseRB, baseRB, baseRB)
        val rectfS = RectF(-baseRS, -baseRS, baseRS, baseRS)

        val pointPaint = Paint()
        pointPaint.color = Color.BLACK
        pointPaint.strokeWidth = 50F

        val coverWidth = baseR

        //通过移动坐标原点模拟星球的自转效果
        canvas.translate(-coverWidth / 2F , coverWidth * 1.5F)

        val index = index
        canvas.translate(0F , coverWidth * index )

        //重复绘制三次星球的地貌使得星球的自转无缝连接
        for (i in 0..2){
            canvas.save()
            canvas.translate(coverWidth / 3F / 2  , -coverWidth / 3F * 2)
            canvas.drawArc(rectfB , 0F , 360F , true , paintB)
            canvas.drawArc(rectfS , 0F , 360F , true , paintS)
            canvas.restore()

            canvas.save()
            canvas.translate(coverWidth / 3F *2 , -coverWidth / 3F)
            canvas.drawArc(rectfB , 0F , 360F , true , paintB)
            canvas.drawArc(rectfS , 0F , 360F , true , paintS)
            canvas.restore()

            canvas.save()
            canvas.translate(coverWidth / 3F *2 , -coverWidth / 8F * 7 + -coverWidth / 10F )
            canvas.drawArc(rectfS , 0F , 360F , true , paintB)
            canvas.restore()

            canvas.save()
            canvas.translate(coverWidth / 3F *2 , -coverWidth / 8F * 7  - -coverWidth / 10F )
            canvas.drawArc(rectfS , 0F , 360F , true , paintB)
            canvas.restore()

            canvas.translate(0F , -coverWidth)
        }
    }
```

相关代码可以访问[我的GitHub](https://github.com/clwater/AndroidAnimation/tree/master/androidanimation03)来获取,欢迎大家start或者提供建议.
