package clwater.androidanimation02

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.RectF
import android.view.animation.OvershootInterpolator
import kotlin.collections.ArrayList


/**
 * Created by gengzhibo on 2018/1/9.setLatestEventInfo
 */
class AnimationViewLightning : View {

    var viewWidth : Float = 0F  //背景宽度
    var viewHeight : Float = 0F //背景高度
    var perIndex : Float = 0F   //当前坐标
    val baseR = 100F            //展示view的半径
    val coefficient = 0.5F     //内部闪电占整体的比例
    var viewBackgroundColor = 0xFFF9FAF9.toInt()   //背景颜色

    data class Point(val x: Float , val y:Float)   //坐标点的数据类


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val heitht = View.MeasureSpec.getSize(heightMeasureSpec)
        //设置当前的背景高和宽为整个组件
        if (viewWidth == 0F) {
            viewWidth = width.toFloat()
        }
        if (viewHeight== 0F) {
            viewHeight = heitht.toFloat()
        }
        setMeasuredDimension(width, heitht)
    }



    //初始化相关参数,非0的宽及高和背景颜色
    fun initView(width: Float , height : Float , bgColor: Int) {
        if (width != 0F) {
            viewWidth = width
        }
        if (height != 0F) {
            viewHeight = height
        }
        viewBackgroundColor = bgColor
    }



    override fun onDraw(canvas: Canvas) {
        canvas.translate(width / 2F, height / 2F)   // 将坐标系移动到画布中央
        canvas.scale(1F , -1F)      //将画布y轴翻转
        canvas.rotate(45F)          //旋转画布 方便计算

        //绘制闪电背景
        drawBaseButton(canvas , perIndex)
        //绘制闪电
        drawLighting(canvas , perIndex)

    }


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


    //开始动画
    fun changeView() {
        val va = ValueAnimator.ofFloat(0F, 1F)
        va.duration = 2000
        va.interpolator = OvershootInterpolator()
        va.addUpdateListener { animation ->
            perIndex = animation.animatedValue as Float
            invalidate()
        }
        va.start()
    }


    fun pointFactory(angle: Int , length: Float): Point {
        val _angle = angle / 180F * Math.PI
        return Point((length * Math.sin(_angle)).toFloat(), (length * Math.cos(_angle)).toFloat())
    }
}