#Android自定义View之元素按钮


>之前在dribbble看到的三个元素的按钮,参考了设计的创意,添加了自己定义的动画效果来实现.先看效果

![效果图](http://ooymoxvz4.bkt.clouddn.com/18-1-16/91123430.jpg)


分别是水火电三个元素的按钮实现.其中电的实现最简单,水的次之,火的实际还并不满意,没有火焰扰动的感觉,尝试过几次但是效果都不理想,最后只保留了自下向上的扇形遮罩.如果有好的效果再优化实现.

## 闪电篇
### 设计过程

通过闪电的位置将整体划分成七个部分(七个部分的主要原因是最初设计了一个中部放大的透镜效果,但是没能实现),从右上角进入,在中心点附近运动,直到停在中心点.闪电本身可以看做一个中心对称的图形,整理就简化成了现将canvas旋转一定角度,然后绘制中心对称的闪电形状,最后在x轴上运动就可以了.

![黄线是旋转后的坐标](http://ooymoxvz4.bkt.clouddn.com/18-1-16/47003102.jpg)

黄线是旋转后的坐标,可以看出简化后实现起来很简单.

### 代码实现
* 背景部分
```java
//绘制闪电背景
private fun  drawBaseButton(canvas: Canvas , index: Float) {
    //设置画笔
    val paint = Paint()

    //添加闪电移动到指定位置时的背景颜色设置
    if ((index <= 0.45F && index >= 0.35F) || (index >= 0.65F && index <= 0.75F)) {
        paint.color = Color.parseColor("#ACADAC")
    }else{
        paint.color = Color.parseColor("#595A59")
    }

    paint.style = Paint.Style.FILL

    //绘制闪电背景
    canvas.drawArc(RectF(-baseR, -baseR, baseR, baseR), 0F , 360F,true , paint)

}
```

* 闪电部分
```java
private fun  drawLighting(canvas: Canvas , index: Float) {
    val baseR = baseR * coefficient
    var index = index
    var changeR = 0F

    //将整个闪电的运动拆成七个部分
    if (index <= 0.25){
        changeR  = this.baseR + baseR
        changeR = (changeR * (1 - index / 0.25)).toFloat()
    }else if (index <= 0.4){
        index = index - 0.25F
        changeR  = this.baseR
        changeR = -(changeR * (index / (0.4F - 0.25F)))
    }else if (index <= 0.6F){
        index = index - 0.4F
        changeR = this.baseR
        changeR = -changeR *  (1 - index / 0.2F)
    }else if (index <= 0.7F){
        index = index - 0.6F
        changeR = baseR
        changeR = changeR * index / 0.1F
    }else if (index <= 0.8F){
        index = index - 0.7F
        changeR = baseR
        changeR = baseR - changeR * index / 0.1F
    }else if (index <= 0.9F){
        index = index - 0.8F
        changeR = baseR
        changeR = -changeR * index / 0.1F
    }else if (index <= 1F){
        index = index - 0.9F
        changeR = baseR
        changeR = -changeR + changeR * (index / 0.1F)
    }


    //设置画笔
    val path = Path()
    val paint = Paint()
    paint.strokeWidth = 5F
    paint.style = Paint.Style.FILL
    paint.color = viewBackgroundColor

    val points :MutableList<Point> = ArrayList()
    //设置绘制闪电的路径点
    points.add(pointFactory(60 , baseR))
    points.add(pointFactory(-45 , baseR / 2F))
    points.add(pointFactory(-45 - 90 , baseR / 5F))
    points.add(pointFactory(-30 - 90 , baseR))
    points.add(pointFactory(45 + 90 , baseR / 2F))
    points.add(pointFactory(45 , baseR / 5F))
    points.add(pointFactory(60 , baseR))


    //设置闪电的偏移量(模拟运动情况)
    //原本还想实现一个中心放大的透镜效果,但是效果很僵硬,只能移除了
    for (i in 0..points.size - 1){
        points.set(i , Point(points[i].x + changeR , points[i].y))
    }


    path.moveTo(points[0].x , points[0].y)

    for (index in 1..points.size - 1){
        path.lineTo(points[index].x , points[index].y)
    }

    canvas.drawPath(path , paint)

    //闪电绘制辅助坐标系
//        val paint2 = Paint()
//        paint2.strokeWidth = 5F
//        paint2.color = Color.YELLOW
//        canvas.drawLine(1000F , 0F ,-1000F , 0F , paint2)
//        canvas.drawLine( 0F ,-1000F , 0F , 1000F , paint2)
}
```

闪电的实现还是很简单的,因为不涉及到图形的变化,只有一个简单的位移效果

## 霜(水)之哀伤篇
### 设计思路
水滴的实现相对对于闪电来说麻烦一些,一是水滴本身不是很好绘制,又因为水滴在下落的过程中存在变化,最后选择通过贝塞尔曲线实现.二是水滴和背景之间的交互,在水滴未完全下落到背景中的时候,水滴背景的上部有个向下凹陷的过程,这个不是闪电背景的简单变化可能做到的.最后也是使用贝塞尔曲线绘制的一个圆弧的区域遮盖来实现.

整理需要变化的元素是水滴及顶部的遮盖.都是使用贝塞尔曲线实现的.顶部的凹陷随着水滴的下落不断凹陷,直至水滴脱离顶部后再渐渐回落.主要是找到水滴完全脱离的时间当做顶部凹陷的关键点就好.水滴下落的过程中是需要变化,最开始可能稍微瘦长一些,然后相对变扁.

### 代码实现
* 水滴背景的实现
```java
//绘制水滴背景
    private fun  drawBaseButton(canvas: Canvas , index: Float) {
        //计算水滴半进入区间(确定水滴背景上部变化范围)
        val waterRand = (baseR * 1.25 * coefficient) / ((baseR * 1.25 * coefficient) + baseR)

        //设置画笔
        val paint = Paint()
        paint.color = Color.parseColor("#45AAE1")
        paint.style = Paint.Style.FILL

        //绘制水滴背景下半部分的(此部分不需要变换)
        canvas.drawArc(RectF(-baseR, -baseR, baseR, baseR), 180F , 180F,true , paint)

        //设置点list 顺序存储相关路径及关键点
        val points : MutableList<Point> = ArrayList()
        points.add(Point(-baseR , 0F))
        points.add(Point(-baseR , baseR * C))
        points.add(Point(-baseR * C , baseR ))

        var baseButtonTop : Float
        //根据index判断上部的形态
        if (index <= waterRand){
            baseButtonTop = baseR - (baseR * coefficient * index) * 2
        }else{
            baseButtonTop = baseR - (baseR * coefficient) * 2 + (baseR * coefficient * index) * 2
            if (baseButtonTop > baseR){
                baseButtonTop = baseR
            }
        }

        points.add(Point(0F, baseButtonTop))

        points.add(Point(baseR * C , baseR))
        points.add(Point(baseR , baseR * C ))
        points.add(Point(baseR , 0F))

        val path = Path()
        //画笔移动到指定位置(不移动的话通过贝塞尔绘制的图形会有误差)
        path.moveTo(points[0].x , points[0].y)
        //设置贝塞尔曲线
        path.cubicTo(
                points[1].x , points[1].y ,
                points[2].x , points[2].y ,
                points[3].x , points[3].y)

        path.cubicTo(
                points[4].x , points[4].y ,
                points[5].x , points[5].y ,
                points[6].x , points[6].y)
        //绘制
        canvas.drawPath(path, paint)
    }
```

* 水滴的实现
```java
private fun  drawDrops(canvas: Canvas , index: Float) {
        //设置水滴半径
        val baseR = baseR * coefficient
        val index = 1 - index

        //根据index将画布中心移动到对应位置
        canvas.translate( 0F , (this.baseR * 1.125F + baseR)* index - this.baseR / 8)

        //设置画笔
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = viewBackgroundColor
        //存储关键点坐标
        val points : MutableList<Point> = ArrayList()
        points.add(Point(-baseR , 0F))

        //水滴顶部变换系数
        val topCoefficient = 1.5F

        points.add(Point(-baseR , baseR * C))
        points.add(Point(-baseR * C , baseR ))
        points.add(Point(0F, (1.5 * baseR + baseR * topCoefficient * index).toFloat()))

        points.add(Point(baseR * C , baseR))
        points.add(Point(baseR , baseR * C ))
        points.add(Point(baseR , 0F))
        //水滴底部变换系数
        //这两个变换系数使得水滴在下落的过程中渐渐变扁
        val bottomCoefficient = 0.3F
        val tempBaseR = (baseR - baseR * bottomCoefficient * index)
        points.add(Point(baseR , -tempBaseR * C))
        points.add(Point(baseR * C , -tempBaseR ))
        points.add(Point(0F, -tempBaseR))

        points.add(Point(-baseR * C , -tempBaseR))
        points.add(Point(-baseR , -tempBaseR * C ))
        points.add(Point(-baseR , 0F))



        //设置四个部分(90°一个部分)的贝塞尔曲线
        //关于贝塞尔曲线的事情...  感觉可以再做点记录
        val path = Path()
        path.moveTo(points[0].x , points[0].y)
        path.cubicTo(
                points[1].x , points[1].y ,
                points[2].x , points[2].y ,
                points[3].x , points[3].y)

        path.cubicTo(
                points[4].x , points[4].y ,
                points[5].x , points[5].y ,
                points[6].x , points[6].y)

        path.cubicTo(
                points[7].x , points[7].y ,
                points[8].x , points[8].y ,
                points[9].x , points[9].y)

        path.cubicTo(
                points[10].x , points[10].y ,
                points[11].x , points[11].y ,
                points[12].x , points[12].y)

        //绘制图形
        canvas.drawPath(path, paint)
    }
```

偷懒的原因所以直接使用背景色做的一个简单的遮盖,没有使用遮罩(其实闪电的部分也是).

相对来说水滴的实现最为满意,主要的预期效果都成功的实现出来了,整体看来效果还是可以的

## 火之高兴篇
### 设计思路
虽然整体看来,应该是一个难度中等的动画,但是在设计的过程中经历了空手用贝塞尔画火焰(最开始的想法本是火焰本身也是会动的),火焰扰动效果的实现(这个最为艰难,主要是不知道怎么控制火焰扰动的效果,其次是遮罩层的使用,具体的坑会另开文字来讲解),最后只能简单做了个底部向上的遮罩层来当做火焰的扰动情况

所以其实就是绘制一个火焰的形状,然后再用个遮罩层来遮盖实现火焰的扰动

### 代码实现
因为背景没有什么特效,就不贴背景的代码了



* 整体火焰效果控制

因为火焰需要展示绘制完成的火焰和遮罩层中相交的部分,要使用PorterDuffXfermode相关的方法,所以在绘制中将原图层和遮罩层分开设计

```java
private fun  drawFires(canvas: Canvas , index: Float) {
    //设置火焰半径

    //设置原图层(火焰绘制)
    val srcB = makeSrc(2 * baseR.toInt(), 2 * baseR.toInt(), index)
    //设置遮罩层
    val dstB = makeDst(2 * baseR.toInt(), 2 * baseR.toInt(), index)


    val paint = Paint()
    canvas.saveLayer(-baseR, -baseR, baseR , baseR, null, Canvas.ALL_SAVE_FLAG)

    //绘制遮罩层
    canvas.drawBitmap(dstB,  -baseR/2,  -baseR/2, paint)
    //设置遮罩模式为SRC_IN显示原图层中原图层与遮罩层相交部分
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(srcB, -baseR/2, -baseR/2, paint)
    paint.xfermode = null

}
```

* 绘制原图层(火焰本身的绘制)
```java
fun makeSrc(w: Int, h: Int , index :Float): Bitmap {
       val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
       val canvas = Canvas(bm)

       canvas.translate(baseR / 2F, baseR / 2F)   // 将坐标系移动到画布中央

       val index = index * 0.5F + 0.5F
       val baseR = baseR * coefficient * index

       //设置画笔
       val paint = Paint()
       paint.style = Paint.Style.FILL
       paint.color = viewBackgroundColor
       paint.strokeWidth = 10F
       //存储关键点坐标
       val points : MutableList<Point> = ArrayList()

       //整体火焰是由六个贝塞尔曲线绘制成的
       points.add(pointFactory( 190F , baseR))
       points.add(pointFactory( 280F , baseR / 3F * 4))
       points.add(pointFactory( 320F ,  baseR / 6F))
       points.add(pointFactory( 350F , baseR))

       points.add(pointFactory( 10F , baseR))
       points.add(pointFactory( 30F , baseR / 3F* 2))
       points.add(pointFactory( 50F , baseR / 3F ))

       points.add(pointFactory( 60F , baseR / 6F * 3))
       points.add(pointFactory( 60F , baseR / 6F * 4))
       points.add(pointFactory( 50F , baseR / 6F * 5))


       points.add(pointFactory( 85F , baseR / 6F * 5))
       points.add(pointFactory( 120F , baseR / 6F * 5))
       points.add(pointFactory( 150F , baseR ))

       points.add(pointFactory( 160F , baseR / 9F * 7))
       points.add(pointFactory( 170F , baseR / 9F * 5))
       points.add(pointFactory( 180F , baseR / 9F * 3))

       points.add(pointFactory( 200F , baseR / 3F))
       points.add(pointFactory( 195F , baseR / 3F * 2))
       points.add(pointFactory( 190F , baseR ))


       val path = Path()
       path.moveTo(points[0].x , points[0].y)

       for (index in 0..((points.size - 1) / 3 - 1) ){
           path.cubicTo(
                   points[3 * index + 1].x , points[3 * index + 1].y ,
                   points[3 * index + 2].x , points[3 * index + 2].y ,
                   points[3 * index + 3].x , points[3 * index + 3].y)
       }

       //绘制图形
       canvas.drawPath(path, paint)

       return bm
   }
```

* 绘制遮罩层(火焰的扰动效果)
```java
fun makeDst(w: Int, h: Int, index :Float): Bitmap {
    val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bm)
    canvas.translate(baseR / 2F, 0F)


    val paint = Paint()
    paint.color = Color.YELLOW

    val dstLength = baseR * coefficient * index * 2

    val rectf = RectF(-dstLength, -dstLength, dstLength, dstLength)

    //没找到合适的扰动效果,只能简单实现一个遮罩效果
    canvas.drawArc(rectf , 0F , 360F , true, paint)

    return bm
}
```

火焰来说,虽然需要的效果代码都实现了,但是缺少设计,整体的效果到时不尽如人意.针对效果来说还有很多的优化空间
