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



        val paint2 = Paint()
        paint2.textSize = 50F
        paint2.strokeWidth = 2F
        paint2.color = Color.BLUE


        canvas.drawLine(-width.toFloat() , 0F , width.toFloat() , 0F , paint2)
        canvas.drawLine(0F , -height.toFloat() , 0F , height.toFloat() , paint2)

        drawGas(canvas , perIndex)

        drawPlanet(canvas, perIndex)

    }

    private fun drawGas(canvas: Canvas, perIndex: Float) {
        canvas.save()
        canvas.rotate(45F)
//        val pointPaint = Paint()
//        pointPaint.strokeWidth = 50F
//        canvas.drawPoint(0F , 100F , pointPaint)

        val gasWidth = 18F

        var baseR = baseR * 0.7F

        val paint = Paint()
        paint.strokeWidth = gasWidth
        paint.style = Paint.Style.STROKE
        paint.color = 0xff2F3768.toInt()

        val paintArc = Paint()
        paintArc.color = 0xff2F3768.toInt()


        val rectArc = RectF(-gasWidth / 2F , -gasWidth / 2F , gasWidth/ 2F ,gasWidth/ 2F)
        canvas.save()
        canvas.translate(-baseR , 0F)
        canvas.drawArc(rectArc , 0F , 360F , false , paintArc)
        canvas.translate(2 * baseR , 0F)
        canvas.drawArc(rectArc , 0F , 360F , false , paintArc)
        canvas.restore()


        val rectf = RectF(-baseR , -baseR , baseR ,baseR)
        canvas.drawArc(rectf , 0F , 180F , false , paint)


        canvas.restore()
    }

    private fun drawPlanet(canvas: Canvas , index : Float) {


        //设置原图层
        val srcB = makeSrc(index)
        //设置遮罩层
        val dstB = makeDst(index)


        val paint = Paint()
//        paint.color = Color.YELLOW
        canvas.saveLayer(-baseR, -baseR, baseR , baseR, null, Canvas.ALL_SAVE_FLAG)
//
        //绘制遮罩层
        canvas.drawBitmap(dstB,  -baseR / 2F, -baseR / 2F , paint)
//        设置遮罩模式为SRC_IN显示原图层中原图层与遮罩层相交部分
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//        canvas.drawBitmap(srcB, baseR / -2F, baseR / -2F , paint)
        canvas.drawBitmap(srcB, width / -2F, height / -2F , paint)
        paint.xfermode = null
    }

    fun makeDst(index :Float): Bitmap {
        val bm = Bitmap.createBitmap(baseR.toInt(), baseR.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        canvas.translate(baseR / 2F , baseR / 2F)


        val paint = Paint()
        paint.strokeWidth = 100F
        paint.color = Color.YELLOW


        val rectf = RectF(-baseR / 2F, -baseR / 2F, baseR / 2F, baseR / 2F)

        canvas.drawArc(rectf , 0F , 360F , true, paint)
//        canvas.drawCircle(0F , 0F , baseR , paint)
//        canvas.drawPoint(0F , 0F , paint)
        return bm
    }

    fun makeSrc(index :Float): Bitmap {
        val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
//        canvas.translate(baseR / 2F , baseR / 2F)
        canvas.translate(width.toFloat() / 2F , height.toFloat() / 2F)





        val paint = Paint()
        paint.color = 0xff57BEC6.toInt()
        paint.style = Paint.Style.FILL

        val rectf = RectF(-baseR / 2F, -baseR / 2F, baseR / 2F, baseR / 2F)
        canvas.drawArc(rectf , 0F , 360F , true , paint)

        canvas.save()

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


        paint.color = 0xffAAEEF2.toInt()
        paint.style = Paint.Style.FILL
        canvas.drawPath(path , paint)


//        canvas.restore()

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

        canvas.translate(-coverWidth / 2F , coverWidth * 1.5F)

        val index = index * 10 % 10 / 10F
        canvas.translate(0F , coverWidth * index )



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
        val va = ValueAnimator.ofFloat(0F, 10F)
        va.duration = 15000
//        va.interpolator = OvershootInterpolator()
        va.addUpdateListener { animation ->
            perIndex = animation.animatedValue as Float
            invalidate()
        }
        va.start()
    }

}