package clwater.androidanimation02

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import android.graphics.RectF
import android.view.animation.OvershootInterpolator





/**
 * Created by gengzhibo on 2018/1/4.
 */
class AnimationViewWater : View {

    var viewWidth : Float = 0F  //背景宽度
    var viewHeight : Float = 0F //背景高度
    var perIndex : Float = 0F   //当前坐标
    var oldIndex : Float = 0F   //上一次坐标
    val baseR = 100F            //展示view的半径
    val coefficient = 0.35F     //内部水滴占整体的比例
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
        canvas.scale(1F,-1F)                  // 翻转Y轴

        //绘制水滴背景
        drawBaseButton(canvas , perIndex)
        //绘制水滴
        drawDrops(canvas , perIndex)
    }


    private fun  drawDrops(canvas: Canvas , index: Float) {
        //设置水滴半径
        val baseR = (baseR * 0.5).toFloat()
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
        val bottomCoefficient = 0.3F
        val tempBaseR = (baseR - baseR * bottomCoefficient * index)
        points.add(Point(baseR , -tempBaseR * C))
        points.add(Point(baseR * C , -tempBaseR ))
        points.add(Point(0F, -tempBaseR))

        points.add(Point(-baseR * C , -tempBaseR))
        points.add(Point(-baseR , -tempBaseR * C ))
        points.add(Point(-baseR , 0F))



        //设置四个部分(90°一个部分)的贝塞尔曲线
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
            baseButtonTop = baseR - baseR * coefficient * index
        }else{
            baseButtonTop = baseR - baseR * coefficient + baseR * coefficient * index
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