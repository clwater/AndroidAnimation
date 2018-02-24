package clwater.androidanimation03

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
 * Created by gengzhibo on 2018/2/6.
 */
class AnimationPlanet : View {

    var viewWidth : Float = 0F  //背景宽度
    var viewHeight : Float = 0F //背景高度
    var perIndex : Float = 0F   //当前坐标
    var perIndexInAll : Float = 0F   //当前坐标
    var baseR = 200F
    var viewBackgroundColor = 0xFFF9FAF9.toInt()   //背景颜色
    val C = 0.552284749831f

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


    override fun onDraw(canvas: Canvas) {
        canvas.translate(width / 2F, height / 2F)   // 将坐标系移动到画布中央

        //绘制背景的星星
        drawStarts(canvas , perIndexInAll)
        //绘制星球外部气层
        drawGas(canvas , perIndex)
        //绘制星球
        drawPlanet(canvas, perIndex)
    }

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
        drawLines(0F, maxGasLength, canvas, paint)
        drawWhite( maxGasLength * index, gasWidth , gsaL * 2 , canvas)
        drawWhite( maxGasLength * (index - 1 ) * 1.1F, gasWidth , gsaL * 2 , canvas)
        drawWhite( maxGasLength * (index + 1 ) * 1.1F, gasWidth , gsaL * 2 , canvas)
        canvas.restore()

        index = index + 0.3F
        canvas.save()
        canvas.translate(-baseR , baseR * 1.2F)
        canvas.translate(0F , absBaseR)
        drawLines(0F, maxGasLength, canvas, paint)
        drawWhite( maxGasLength * index, gasWidth , gsaL * 2 , canvas)
        drawWhite( maxGasLength * (index - 1 ), gasWidth , gsaL * 2 , canvas)
        drawWhite( maxGasLength * (index + 1 ), gasWidth , gsaL * 2 , canvas)
        canvas.restore()

        index = index + 0.3F
        canvas.save()
        canvas.translate(0F, baseR * 1.2F)
        canvas.translate(0F , -absBaseR)
        maxGasLength = 2 *   absBaseR + maxGasLength
        drawLines(0F, maxGasLength, canvas, paint)
        drawWhite( maxGasLength * index, gasWidth , gsaL * 2 , canvas)
        drawWhite( maxGasLength * (index - 1 ), gasWidth , gsaL * 2 , canvas)
        drawWhite( maxGasLength * (index + 1 ), gasWidth , gsaL * 2 , canvas)
        canvas.restore()

        index = index + 0.3F
        canvas.save()
        canvas.translate(baseR / 2F, baseR * 1.2F)
        canvas.translate(0F , -absBaseR)
        drawLines(0F, maxGasLength, canvas, paint)
        drawWhite( maxGasLength * index, gasWidth , gsaL * 2 , canvas)
        drawWhite( maxGasLength * (index - 1 ), gasWidth , gsaL * 2 , canvas)
        drawWhite( maxGasLength * (index + 1 ), gasWidth , gsaL * 2 , canvas)
        canvas.restore()

        index = index + 0.3F
        canvas.save()
        canvas.translate(- baseR / 2F, baseR * 1.2F)
        canvas.translate(0F , -absBaseR)
        drawLines(0F, maxGasLength, canvas, paint)
        drawWhite( maxGasLength * index, gasWidth , gsaL * 2 , canvas)
        drawWhite( maxGasLength * (index - 1 ), gasWidth , gsaL * 2 , canvas)
        drawWhite( maxGasLength * (index + 1 ), gasWidth , gsaL * 2 , canvas)
        canvas.restore()
        canvas.restore()

        val rectArc = RectF(-gasWidth / 2F , -gasWidth / 2F , gasWidth/ 2F ,gasWidth/ 2F)
        canvas.save()
        canvas.translate(baseR , -baseR)
        canvas.drawArc(rectArc , 0F , 360F , false , paintArc)
        canvas.translate(2 * -baseR , 0F)
        canvas.drawArc(rectArc , 0F , 360F , false , paintArc)
        canvas.restore()

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

    private fun drawLines(index0: Float, index1: Float, canvas: Canvas, paint: Paint) {
        canvas.save()
        val paintArc = Paint()
        paintArc.color = 0xff2F3768.toInt()

        val gasWidth = 18F

        val paint = Paint()
        paint.strokeWidth = gasWidth
        paint.style = Paint.Style.STROKE
        paint.color = 0xff2F3768.toInt()

        val rectArc = RectF(-gasWidth / 2F , -gasWidth / 2F , gasWidth/ 2F ,gasWidth/ 2F)
        canvas.translate(0F , index0)
        canvas.drawArc(rectArc , 0F , 360F  , true, paintArc)
        canvas.restore()
        canvas.save()
        canvas.translate(0F , index1)
        canvas.drawArc(rectArc , 0F , 360F  , true, paintArc)
        canvas.restore()

        canvas.drawLine(0F , index0 , 0F , index1 , paint)

    }

    private fun drawPlanet(canvas: Canvas , index : Float) {
        //设置原图层
        val srcB = makeSrc(index)
        //设置遮罩层
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

    //设置遮罩层
    //设置一个星球样式的遮罩层
    fun makeDst(index :Float): Bitmap {
        val bm = Bitmap.createBitmap(baseR.toInt(), baseR.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        canvas.translate(baseR / 2F , baseR / 2F)

        val paint = Paint()
        paint.strokeWidth = 100F
        paint.color = Color.YELLOW

        val rectf = RectF(-baseR / 2F, -baseR / 2F, baseR / 2F, baseR / 2F)
        canvas.drawArc(rectf , 0F , 360F , true, paint)
        return bm
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


    //开始动画
    fun changeView() {
        val va = ValueAnimator.ofFloat(0F, 50F)
        va.duration = 50000
//        va.interpolator = OvershootInterpolator()
        va.addUpdateListener { animation ->
            perIndex = animation.animatedValue as Float
            perIndexInAll = perIndex
            //循环的过程中取小数点后数据
            perIndex = perIndex * 10 % 10 / 10F
            invalidate()
        }
        va.start()
    }

}