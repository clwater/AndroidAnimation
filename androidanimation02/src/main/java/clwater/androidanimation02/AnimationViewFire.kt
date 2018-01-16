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

        //绘制火焰背景
        drawBaseButton(canvas , perIndex)
        //绘制火焰
        drawFires(canvas , perIndex)


        //绘制辅助线
//        drawAuxiliary(canvas)
    }



    private fun  drawAuxiliary(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.parseColor("#000000")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3F

        val baseR = baseR * coefficient
        canvas.drawArc(RectF(-baseR, -baseR, baseR, baseR), 0F , 360F,true , paint)
    }


    private fun  drawFires(canvas: Canvas , index: Float) {
        //设置火焰半径

        //设置原图层(火焰绘制)
        val srcB = makeSrc(2 * baseR.toInt(), 2 * baseR.toInt(), index)
        //设置遮罩层
        val dstB = makeDst(2 * baseR.toInt(), 2 * baseR.toInt(), index)


        val paint = Paint()

        val x = 0
        val y = 0

        canvas.saveLayer(x - baseR, y - baseR, x + baseR , y + baseR, null, Canvas.ALL_SAVE_FLAG)

        //绘制遮罩层
        canvas.drawBitmap(dstB,  -baseR/2,  -baseR/2, paint)
        //设置遮罩模式为SRC_IN显示原图层与遮罩层相交部分
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(srcB, -baseR/2, -baseR/2, paint)
        paint.xfermode = null

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



    fun makeDst(w: Int, h: Int, index :Float): Bitmap {
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        canvas.translate(baseR / 2F, 0F)


        val paint = Paint()
        paint.color = Color.YELLOW

        val dstLength = baseR * coefficient * index * 2

        val rectf = RectF(-dstLength, -dstLength, dstLength, dstLength)
        canvas.drawArc(rectf , 0F , 360F , true, paint)

        return bm
    }

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

    fun pointFactory(angle: Float , length: Float): Point{
        val _angle = angle / 180F * Math.PI
        return Point((length * Math.sin(_angle)).toFloat(), (length * Math.cos(_angle)).toFloat())
    }

}