# Android自定义View之鲨鱼和小鱼的等待动画

> 之前在看文章的时候发现了一个gif动画,感觉很有意思,适逢最近学习kotlin,就打算用kotlin在android中实现一次,部分角度及参数设置的比较随意,如有需要可以可随意修改

先看效果图

![效果图](http://ooymoxvz4.bkt.clouddn.com/17-12-26/1711733.jpg)

部分角度及速率还有优化的空间,不过主要的效果都已经实现出来了

## 设计过程
因为是个动画,刚看的时候感觉无从下手,仔细分析过后,发现...其实很简单的

首先是一个循环的动画,先要找到某个循环的开始或者结束,我截取的某个循环部分是鲨鱼鳍在右侧,本地左右变化几下,然后向左追逐小鱼到左侧部分.向右的部分,通过将向左的部分x镜像一下就可以得到.

由此可以将整个画面拆成三个部分:
* 其一是水面的部分,水面以下及水面和鱼接触的部分的留白处理都是在后面鲨鱼和小鱼处进行的,所以最后绘制的是一个水面以下部分的遮挡,这个部分也是一个比较好绘制的部分
* 其二是鲨鱼鳍的部分, 这个部分难度中等,主要的难度是两个圆找交点的计算过程...如果有需要可以根据代码设计相关参数进行调整
* 其三是小鱼的部分,这个是画面中最难实现的部分,主要涉及小鱼的绘制(椭圆及三角形),小鱼的水面的接触时水面的留白,小鱼入水的时候溅起的水花

## 代码实现
### 水面部分
代码就不放了,因为只是单纯的绘制个线和下部的遮盖,可以看看效果图

水面

![水面](http://ooymoxvz4.bkt.clouddn.com/17-12-26/43545250.jpg)

为加遮盖的水面(可以看到水面之下的小鱼)
![为加遮盖的水面(可以看到水面之下的小鱼)](http://ooymoxvz4.bkt.clouddn.com/17-12-26/48788417.jpg)

### 鲨鱼部分

鲨鱼鳍的主要设计思路就是先绘制两个圆,再根据两个圆的交点决定绘制的圆弧范围,注意
绘制的时候遮盖下部的水面


效果图

![鲨鱼鳍效果图](http://ooymoxvz4.bkt.clouddn.com/17-12-26/57511199.jpg)


```java
//绘制鲨鱼鳍
//鲨鱼鳍由两个圆弧组成
private fun  drawShark(index: Float, canvas: Canvas) {
    //初始化画笔相关
    val p = Paint()
    p.strokeWidth = 5F
    p.color = Color.BLACK
    p.style = Paint.Style.STROKE

    //设置外面鱼鳍的半径
    val r = 100F
    //设置偏移量
    var xofset = -65 //向右为正
    var yofset = 0 //向下为正

    //绘制外面鱼鳍
    drawSharkOut(canvas , index , r , xofset , yofset , isRight , p)
    //绘制内部鱼鳍
    drawSharkIn(canvas , index , r , xofset , yofset , isRight , p)

    p.color = viewBackgroundColor
    //将鱼鳍出现的水面部分设置为空
    canvas.drawLine(index - r - xofset - viewWidth/2, 0F , index +  r + xofset  - viewWidth/2, 0F, p)

    // p.color = Color.BLACK
    // //绘制前面的小鱼
    // drawFish(canvas , index , p)

}

//区分鱼鳍的方向绘制外部鱼鳍
fun  drawSharkOut(canvas: Canvas , index: Float, r: Float, xofset: Int, yofset: Int, directionRight: Boolean, p: Paint) {
    if (directionRight){
        val xofset =  xofset - viewWidth/2
        val rectf = RectF(index - r + xofset, -r + yofset , index + r + xofset , r  + yofset)
        canvas.drawArc(rectf, -95F , 95F, false, p)
    }else{
        val xofset = 30 + xofset - viewWidth/2
        val rectf = RectF(index + xofset + yofset  ,  -r , index + 2 * r + xofset, r + yofset )
        canvas.drawArc(rectf, -180F , 95F, false, p)
    }


}
//区分鱼鳍方向绘制内部鱼鳍
fun  drawSharkIn(canvas: Canvas , index: Float, r1: Float, xofset: Int, yofset: Int, directionRight: Boolean, p: Paint) {
    val r = (1.5 * r1).toInt()
    if (directionRight) {
        val xofset = (- (r - ( r - r1) * 0.6)).toInt() + xofset - viewWidth/2
        val rectf = RectF(index - r + xofset, (- r + yofset).toFloat(), index + r + xofset, (+ r + yofset).toFloat())
        canvas.drawArc(rectf, -42F, 42F, false, p)
    }else{
        val xofset = -45 + xofset - viewWidth/2
        val rectf = RectF(index + r + xofset, (- r + yofset).toFloat(), index + 3 * r + xofset, (+ r + yofset).toFloat())
        canvas.drawArc(rectf, -180F, 42F, false, p)
    }

}
```



### 小鱼部分
小鱼部分设计三个部分,鱼身(本体,眼睛,鱼尾),鱼的运动,鱼入水时的水花.

小鱼的运动轨迹可以看出来是一个圆弧,涉及的部分就是要根据鲨鱼鳍的位置调整在运动圆环上的位置,简单的canvas变换就可以做到

鱼身,相对来说比较简单,一个椭圆,一个三角形,一个点就可以完成了,再实现的时候先绘制了一个和背景色相同的填充内部颜色的鱼,用来实现鱼的水面接触的部分留白.

小鱼入水的水花实际上是在鱼的两侧没测画两个圆弧,当小鱼和入水越来越深(canvas的旋转的角度越来越大的时候,同一侧的两个水花之间的间隔也会变大,用来模拟水花的下落)

小鱼还未出水的部分(关闭水面下发的遮盖)

![小鱼还未出水的部分(关闭水面下发的遮盖)](http://ooymoxvz4.bkt.clouddn.com/17-12-26/48788417.jpg)

小鱼和水面接触的部分(遮盖水面的绘制)

![小鱼和水面接触的部分(遮盖水面的绘制)](http://ooymoxvz4.bkt.clouddn.com/17-12-26/49127860.jpg)

小鱼入水时的水花效果

![小鱼入水时的水花效果](http://ooymoxvz4.bkt.clouddn.com/17-12-26/95033000.jpg)

代码实现

```java
//绘制前面逃跑的小鱼
private fun  drawFish(canvas: Canvas, index: Float, p: Paint) {

    //设置鱼运动轨迹的圆心
    var xofsetFish = viewWidth / 5 * 2
    var yofsetFish = 0F
    //设置鱼的宽度及高度
    val widthFish =  20F
    val heightFish = 12F
    //设置鱼鳍的宽度及高度
    val widthFishTail =  20F
    val heightFishTail = 10F

    //将画布中心移动到鱼轨迹的中心位置
    canvas.translate(xofsetFish - viewWidth/2 , yofsetFish)
    //旋转画布旋转到起始角度
    canvas.rotate(90F)
    //根据当前的坐标位置计算鱼的偏移量及偏移角度
    val per = (index - viewWidth / 5 * 2) / (viewWidth / 5 * 2)
    val degreesi =per * 360  + 10
    canvas.rotate(degreesi)


    //如果当前per小于0.4说明整体在水面下,则不进行绘制
    if (per > 0.4) {

        xofsetFish = 0F
        yofsetFish = -viewWidth / 5

        //绘制鱼的身体部分并通过背景颜色填充
        //通过这种方法来覆盖水面和鱼接触的部分
        val rectF = RectF(xofsetFish - widthFish, yofsetFish - heightFish, xofsetFish + widthFish, yofsetFish + heightFish)
        p.color = viewBackgroundColor
        p.style = Paint.Style.FILL
        canvas.drawArc(rectF, 0F, 360F, true, p)

        //绘制鱼的身体部分
        p.color = Color.BLACK
        p.style = Paint.Style.STROKE
        canvas.drawArc(rectF, 0F, 360F, true, p)

        //绘制鱼尾的部分
        canvas.drawLine(xofsetFish + widthFish, yofsetFish, xofsetFish + widthFish + widthFishTail, yofsetFish + heightFishTail, p)
        canvas.drawLine(xofsetFish + widthFish + widthFishTail, yofsetFish + heightFishTail, xofsetFish + widthFish + widthFishTail, yofsetFish - heightFishTail, p)
        canvas.drawLine(xofsetFish + widthFish + widthFishTail, yofsetFish - heightFishTail, xofsetFish + widthFish, yofsetFish, p)

        //绘制鱼的眼睛
        canvas.drawPoint(xofsetFish - widthFish / 3, yofsetFish , p)


        //当per在[0.45,0.48]区间中绘制水面的水花
        if (per <= 0.48&& per >= 0.45){

            val water = 50F
            val waterR = 30F
            canvas.translate(0F ,yofsetFish + water)
            p.color = Color.BLACK
            p.style = Paint.Style.STROKE
            val waterRectF = RectF(-waterR , -waterR , waterR , waterR)
            canvas.drawArc(waterRectF, -90F , 30F , false, p)
            //计算区间的跨度,设置统一方向下两半水花的分散程度
            val changeWater =   ( 1 - (per - 0.45) / 0.03) * 45
            canvas.drawArc(waterRectF, (-60 + changeWater).toFloat(), 30F , false, p)

            canvas.translate(0F , - 2 * water)
            canvas.drawArc(waterRectF, 90F , -30F , false, p)
            canvas.drawArc(waterRectF, (90 - changeWater).toFloat(), -30F , false, p)
        }
    }
}
```

至此这个小动画就可以实现了,具体的代码可以查看[我的github](https://github.com/clwater/AndroidAnimation/tree/master/androidanimation01)
