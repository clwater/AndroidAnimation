package clwater.androidanimation04_weather

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator


class SunView : View{
    constructor(context: Context, radius: Float) : super(context) {
        this.radius = radius
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    var radius :Float = 0.0f
    var perIndex :Float = 0.0f
    val sacle = 0.6f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        radius = width * sacle

        canvas.translate(width.toFloat() , 0f)
        canvas.rotate(-45f)

        canvas.translate(-300f , 0f)



        drawBaseSun(canvas)
        drawLightArc(canvas)

//
//        val testPaint = Paint()
//        testPaint.strokeWidth = 20f
//        testPaint.color = Color.RED
//
//        canvas.drawPoint(offsetX , offsetY , testPaint)
    }

    private fun drawLightArc(canvas: Canvas) {

        canvas.translate(-radius   , 0f)

        val paint = Paint()
//        paint.color = 0x60FEF5B1.toInt()
        paint.style = Paint.Style.FILL

        val r1 = 0.1f * radius
        val rectF = RectF(-r1 , -r1 , r1 ,r1)


        val colors = intArrayOf(0xffFEF5B1.toInt(), 0x30ffffff.toInt() )
//        val colors = intArrayOf(0xff000000.toInt(), 0xffffffff.toInt() )
        val stops = floatArrayOf(0f , 1f)

        val radialGradient = RadialGradient( 0f, r1 * 0.3f,  r1, colors , stops , Shader.TileMode.MIRROR)
        paint.shader = radialGradient



        canvas.drawArc(rectF , 0f ,360f , true , paint )
    }

    private fun drawBaseSun(canvas: Canvas) {



        val rect = RectF(-radius , -radius , radius , radius)
        val paint = Paint()


        val colors = intArrayOf(Color.parseColor("#F19733") , Color.parseColor("#FBD650") )
        val stops = floatArrayOf(0f , 1f)

        val offsetX : Float
        val offsetY : Float

        if (perIndex <= 0.5){
            offsetX = -radius * perIndex * 2 * 2
        }else{
            offsetX = -radius * ( 1 - perIndex ) * 2 * 2
        }


        offsetY = -Math.sin(Math.PI * perIndex * 2).toFloat() * radius / 4

        val radialGradient = RadialGradient(offsetX, offsetY,  2 * radius, colors , stops , Shader.TileMode.MIRROR)
        paint.shader = radialGradient


        canvas.drawArc(rect,
                135f,
                90f,
                true,
                paint)

    }


    fun satrtView() {
        val va = ValueAnimator.ofFloat(0F, 50F)
        va.duration = 50000 * 4
        va.interpolator = OvershootInterpolator()
        va.addUpdateListener { animation ->
            perIndex = animation.animatedValue as Float
            perIndex = perIndex * 10 / 10f % 10 / 10F
            invalidate()
        }
        va.start()
    }
}