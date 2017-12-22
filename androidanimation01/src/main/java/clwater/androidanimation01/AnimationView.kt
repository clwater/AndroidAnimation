package clwater.androidanimation01

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.Path
import android.graphics.RectF
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator


/**
 * Created by gengzhibo on 17/12/22.
 */
class AnimationView : View {

    var viewWidth : Float = 0F
    var viewHeight : Float = 0F
    var perIndex : Float = 0F


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context , width: Float , height : Float) : super(context) {
        init()
        viewWidth = width
        viewHeight = height
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val heitht = View.MeasureSpec.getSize(heightMeasureSpec)
        if (viewWidth == 0F) {
            viewWidth = width.toFloat()
        }
        if (viewHeight== 0F) {
            viewHeight = heitht.toFloat()
        }
        setMeasuredDimension(width, heitht)
    }


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
    }


    override fun onDraw(canvas: Canvas) {
        val p = Paint()
        p.color = Color.BLACK
        p.strokeWidth = 5F



        Log.d("clwater" , "width: " + width)
        Log.d("clwater" , "height: " + height)

        canvas.drawLine(0F, viewHeight / 2 , viewWidth, viewHeight / 2, p)

        drawShark(perIndex , canvas)

    }

    private fun  drawShark(index: Float, canvas: Canvas) {
        val p = Paint()
        p.strokeWidth = 5F


        p.color = Color.BLACK
        p.style = Paint.Style.STROKE

        var r1 = 100
        var xofset = 0 //向右为正
        var yofset = 0 //向下为正
        var rectf = RectF()
        rectf.set(index - r1 + xofset, viewHeight / 2 - r1 + yofset, index + r1 + xofset, viewHeight / 2 + r1 + yofset)
        canvas.drawArc(rectf, -95F , 95F, false, p)

        val r2 = (1.5 * r1).toInt()
        xofset = (- (r2 - ( r2 - r1) * 0.6)).toInt()
        rectf.set(index - r2 + xofset, viewHeight / 2 - r2 + yofset, index + r2 + xofset, viewHeight / 2 + r2 + yofset)
        canvas.drawArc(rectf, -42F , 42F, false, p)

        p.color = Color.WHITE
        canvas.drawLine(index + r1, viewHeight / 2 , index + r2 + xofset , viewHeight / 2, p)


    }

    fun changeView(index: Float , ) {
        val va = ValueAnimator.ofFloat(0F, index)
        va.duration = 1000
//        va.interpolator = OvershootInterpolator()
        va.addUpdateListener { animation ->
            perIndex = animation.animatedValue as Float
            invalidate()
        }
        va.start()

    }

}