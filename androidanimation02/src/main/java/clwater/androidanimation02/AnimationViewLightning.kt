package clwater.androidanimation02

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.RectF
import android.util.Log
import android.view.animation.OvershootInterpolator
import java.util.*
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Bitmap




/**
 * Created by gengzhibo on 2018/1/4.
 */
class AnimationViewLightning : View {

    var viewWidth : Float = 0F  //背景宽度
    var viewHeight : Float = 0F //背景高度
    var perIndex : Float = 0F   //当前坐标
    val baseR = 100F            //展示view的半径
    val coefficient = 0.5F     //内部闪电占整体的比例
    val C = 0.552284749831f     //利用贝塞尔绘制圆的常数
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
        canvas.scale(1F , -1F)

        //绘制闪电背景
        drawBaseButton(canvas , perIndex)
        //绘制闪电
        drawDrops(canvas , perIndex)

    }


    private fun  drawDrops(canvas: Canvas , index: Float) {
        //设置闪电半径

    }


    //绘制闪电背景
    private fun  drawBaseButton(canvas: Canvas , index: Float) {
        //设置画笔
        val paint = Paint()
        paint.color = Color.parseColor("#595A59")
        paint.style = Paint.Style.FILL

        //绘制闪电背景
        canvas.drawArc(RectF(-baseR, -baseR, baseR, baseR), 0F , 360F,true , paint)

    }
    

    //开始动画
    fun changeView() {
        val va = ValueAnimator.ofFloat(0F, 1F)
        va.duration = 1500
        va.interpolator = OvershootInterpolator()
        va.addUpdateListener { animation ->
            perIndex = animation.animatedValue as Float
            invalidate()
        }
        va.start()
    }


}