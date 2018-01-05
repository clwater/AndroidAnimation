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



/**
 * Created by gengzhibo on 2018/1/4.
 */
class AnimationViewButtonWater : View {

    var viewWidth : Float = 0F  //view宽度
    var viewHeight : Float = 0F //view高度
    var oldIndex : Float = 0F
    var perIndex : Float = 0F
    var isRunning: Boolean = true
    var baseR = 200F
    val baseC = 0.552284749831f
    val C = 0.552284749831f
    data class Point(val x: Float , val y:Float)



    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val heitht = View.MeasureSpec.getSize(heightMeasureSpec)
        //设置当前的view高和宽为整个屏幕
        if (viewWidth == 0F) {
            viewWidth = width.toFloat()
        }
        if (viewHeight== 0F) {
            viewHeight = heitht.toFloat()
        }
        setMeasuredDimension(width, heitht)
    }



    //初始化相关参数,非0的宽及高和背景颜色
    fun initView(width: Float , height : Float ) {
        if (width != 0F) {
            viewWidth = width
        }
        if (height != 0F) {
            viewHeight = height
        }
    }





    override fun onDraw(canvas: Canvas) {
        canvas.translate(width / 2F, height / 2F)   // 将坐标系移动到画布中央
        canvas.scale(1F,-1F)                  // 翻转Y轴

        drawBaseButton(canvas)
        drawDrops(canvas , perIndex)

//        drawAuxiliaryLine(canvas)

//        drawBSR(canvas)

    }

    private fun  drawDrops(canvas: Canvas , index: Float) {
        val baseR = (baseR * 0.35).toFloat()
        val index = 1 - index

        canvas.translate( 0F ,  (this.baseR + baseR)* index )


        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE

        val points : MutableList<Point> = ArrayList()
        points.add(Point(-baseR , 0F))

        val topCoefficient = 0.5F

        points.add(Point(-baseR , baseR * C))
        points.add(Point(-baseR * C , baseR ))
        points.add(Point(0F, (1.5 * baseR + baseR * topCoefficient * index).toFloat()))

        points.add(Point(baseR * C , baseR))
        points.add(Point(baseR , baseR * C ))
        points.add(Point(baseR , 0F))

        val bottomCoefficient = 0.2F
        val tempBaseR = (baseR - baseR * bottomCoefficient * index)
        points.add(Point(baseR , -tempBaseR * C))
        points.add(Point(baseR * C , -tempBaseR ))
        points.add(Point(0F, -tempBaseR))

        points.add(Point(-baseR * C , -tempBaseR))
        points.add(Point(-baseR , -tempBaseR * C ))
        points.add(Point(-baseR , 0F))




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

        canvas.drawPath(path, paint)

        paint.strokeWidth = 5F
        paint.color = Color.BLACK
    }



    private fun  drawBaseButton(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.parseColor("#45AAE1")
        paint.style = Paint.Style.FILL
        canvas.drawArc(RectF(-baseR, -baseR, baseR, baseR), 0F , 360F,true , paint)
    }


    //更换当前的坐标,更新绘制的画面样式
    fun changeView(index: Float , time : Int ) {

        val va = ValueAnimator.ofFloat(oldIndex, index)
        va.duration = time.toLong()
        oldIndex = index
        va.addUpdateListener { animation ->
            perIndex = animation.animatedValue as Float
            invalidate()
        }
        va.start()

    }

    //开始动画
    fun startView(){
        changeView(0F, 0)
        changeView(1F, 10000)
//        isRunning = true
//        doAsync {
//            kotlin.run {
//                while (isRunning) {
//                    //动画的策略
//                    uiThread {
//                        changeView(1000F, 1000)
//                    }
//                }
//            }
//        }
    }



}