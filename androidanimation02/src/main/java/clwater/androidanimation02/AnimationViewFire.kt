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
class AnimationViewFire : View {

    var viewWidth : Float = 0F  //背景宽度
    var viewHeight : Float = 0F //背景高度
    var perIndex : Float = 0F   //当前坐标
    val baseR = 100F            //展示view的半径
    val coefficient = 0.5F     //内部火焰占整体的比例
    val C = 0.552284749831f     //利用贝塞尔绘制圆的常数
    var viewBackgroundColor = 0xFFF9FAF9.toInt()   //背景颜色

    data class Point(val x: Float , val y:Float)   //坐标点的数据类

    var r0 = 0
    var r1 = 0
    var r2 = 0
    var r3 = 0


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

        //绘制火焰背景
        drawBaseButton(canvas , perIndex)
        //绘制火焰
        drawDrops(canvas , perIndex)

        deawCover(canvas , perIndex)

        //绘制辅助线
//        drawAuxiliary(canvas)
    }

    private fun  deawCover(canvas: Canvas, perIndex: Float) {
        val baseR = baseR* coefficient
        canvas.translate(0F , -baseR)

        //设置画笔
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#E84368")
//        paint.color = Color.RED
        paint.strokeWidth = 10F
        //存储关键点坐标
        val points : MutableList<Point> = ArrayList()



    }


    private fun  drawAuxiliary(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.parseColor("#000000")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3F

        val baseR = baseR * coefficient
        canvas.drawArc(RectF(-baseR, -baseR, baseR, baseR), 0F , 360F,true , paint)
    }


    private fun  drawDrops(canvas: Canvas , index: Float) {
        //设置火焰半径


        val index = index * 0.5F + 0.5F
        val baseR = baseR * coefficient * index

        //设置画笔
        val paint = Paint()
        paint.style = Paint.Style.FILL
//        paint.style = Paint.Style.STROKE
        paint.color = viewBackgroundColor
        paint.strokeWidth = 10F
        //存储关键点坐标
        val points : MutableList<Point> = ArrayList()


        points.add(pointFactory( 190 , baseR))
        points.add(pointFactory( 280 , baseR / 3F * 4))
        points.add(pointFactory( 320 ,  baseR / 6F))
        points.add(pointFactory( 350 , baseR))

        points.add(pointFactory( 10 , baseR))
        points.add(pointFactory( 30 , baseR / 3F* 2))
        points.add(pointFactory( 50 , baseR / 3F ))

        points.add(pointFactory( 60 , baseR / 6F * 3))
        points.add(pointFactory( 60 , baseR / 6F * 4))
        points.add(pointFactory( 50 , baseR / 6F * 5))


        points.add(pointFactory( 85  , baseR / 6F * 5))
        points.add(pointFactory( 120 , baseR / 6F * 5))
        points.add(pointFactory( 150 , baseR ))

        points.add(pointFactory( 160 , baseR / 9F * 7))
        points.add(pointFactory( 170 , baseR / 9F * 5))
        points.add(pointFactory( 180 , baseR / 9F * 3))

        points.add(pointFactory( 200 , baseR / 3F))
        points.add(pointFactory( 195 , baseR / 3F * 2))
        points.add(pointFactory( 190 , baseR ))


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



        paint.color = Color.BLUE
        paint.textSize = 30F

        for( (index,point) in points.withIndex()) {
            paint.strokeWidth = 1F
            canvas.drawPoint(point.x , point.y , paint)
        }

    }


    //绘制火焰背景
    private fun  drawBaseButton(canvas: Canvas , index: Float) {
        //设置画笔
        val paint = Paint()
        paint.color = Color.parseColor("#E84368")
        paint.style = Paint.Style.FILL

        //绘制火焰背景
        canvas.drawArc(RectF(-baseR, -baseR, baseR, baseR), 0F , 360F,true , paint)

    }

    fun changeTest(r0: Int , r1: Int ,r2: Int ,r3: Int){
        this.r0 = r0
        this.r1 = r1
        this.r2 = r2
        this.r3 = r3

        invalidate()
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

    fun pointFactory(angle: Int , length: Float): Point{
        val _angle = angle / 180F * Math.PI
//        Log.d("view02" , "sin($angle)="+Math.sin(_angle))
        return Point((length * Math.sin(_angle)).toFloat(), (length * Math.cos(_angle)).toFloat())
    }
}