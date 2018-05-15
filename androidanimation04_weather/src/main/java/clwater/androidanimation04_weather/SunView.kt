package clwater.androidanimation04_weather

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class SunView : View {
    constructor(context: Context, radius: Float) : super(context) {
        this.radius = radius
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    var radius: Float = 0.0f
    var perIndex: Float = 0.0f
    val sacle = 0.6f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        radius = width * sacle

        canvas.translate(width.toFloat(), 0f)
        canvas.rotate(-45f)

//        canvas.translate(-300f, 0f)

        drawBaseSun(canvas)
        drawLightArc(canvas)

    }

    private fun drawLightArc(canvas: Canvas) {
        val offsetIndex = Math.sin(Math.PI * perIndex).toFloat()
        val offsetRadius = -(perIndex - 0.5f) * (perIndex - 0.5f) + 1

        canvas.rotate(-60f)
        canvas.rotate((offsetIndex * 60f))


        val r1 = (0.1f * radius * offsetRadius)
        val rectF1 = RectF(-r1, -r1, r1, r1)
        val r2 = (0.05f * radius * offsetRadius)
        val rectF2 = RectF(-r2, -r2, r2, r2)


        val paint1 = Paint()
        paint1.style = Paint.Style.FILL

        val paint2 = Paint()
        paint2.style = Paint.Style.FILL


        val colors = intArrayOf(0xffFFF5AF.toInt(), 0x30FFF5AF.toInt())
        val stops = floatArrayOf(0f, 1f)

        val radialGradient1 = RadialGradient(-r1 * 0.6f, 0f, 1.4f * r1, colors, stops, Shader.TileMode.MIRROR)
        paint1.shader = radialGradient1
        val radialGradient2 = RadialGradient(-r2 * 0.6f, 0f, 1.4f * r2, colors, stops, Shader.TileMode.MIRROR)
        paint2.shader = radialGradient2

        canvas.translate(-radius / 4f * 3 * offsetRadius, 0f)
        canvas.drawArc(rectF2, 0f, 360f, true, paint2)

        canvas.translate(-radius / 4f * offsetRadius, 0f)
        canvas.drawArc(rectF1, 0f, 360f, true, paint1)
    }

    private fun drawBaseSun(canvas: Canvas) {
        val rect = RectF(-radius, -radius, radius, radius)
        val paint = Paint()

        val colors = intArrayOf(Color.parseColor("#F19733"), Color.parseColor("#FBD650"))
        val stops = floatArrayOf(0f, 1f)

        val offsetX: Float
        val offsetY: Float

        if (perIndex <= 0.5) {
            offsetX = -radius * perIndex * 2 * 2
        } else {
            offsetX = -radius * (1 - perIndex) * 2 * 2
        }


        offsetY = -Math.sin(Math.PI * perIndex * 2).toFloat() * radius / 4

        val radialGradient = RadialGradient(offsetX, offsetY, 2 * radius, colors, stops, Shader.TileMode.MIRROR)
        paint.shader = radialGradient

        canvas.drawArc(rect,
                135f,
                90f,
                true,
                paint)

    }


    fun satrtView() {
        val timing = 1000 * 15
        doAsync {
            kotlin.run {
                while (true) {
                    //动画的策略
                    uiThread {
                        startAnimator(timing.toLong())
                    }
                    Thread.sleep(timing.toLong())
                }
            }
        }
    }

    fun startAnimator(timing : Long){
        val va = ValueAnimator.ofFloat(0F, 1F)
        va.duration = timing
        va.addUpdateListener { animation ->
            perIndex = animation.animatedValue as Float
            perIndex = perIndex
            invalidate()
        }
        va.start()
    }
}