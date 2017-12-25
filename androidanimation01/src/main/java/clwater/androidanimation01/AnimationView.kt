package clwater.androidanimation01

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread




/**
 * Created by gengzhibo on 17/12/22.
 */
class AnimationView : View {

    var viewWidth : Float = 0F
    var viewHeight : Float = 0F
    var perIndex : Float = 0F
    var oldIndex : Float = 0F
    var isMirror = false
    var isRight = true
    var isFishLeft = true


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

        perIndex =  viewWidth - viewWidth / 5
        oldIndex = perIndex

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

        canvas.translate(width/2F , 0F)

        if(isMirror){
            canvas.scale( -1F ,1F)
        }


        canvas.drawLine(-viewWidth / 2, viewHeight / 2 , viewWidth/2, viewHeight / 2, p)

        canvas.save()
        drawShark(perIndex , canvas)
        canvas.restore()
        drawBottom(canvas)

    }

    private fun  drawBottom(canvas: Canvas) {
        val p = Paint()
        p.color = Color.WHITE
        canvas.drawRect(Rect(-width / 2,  height /2 + 3, width / 2, height /2 + height / 10), p)
    }

    private fun  drawShark(index: Float, canvas: Canvas) {
        val p = Paint()
        p.strokeWidth = 5F


        p.color = Color.BLACK
        p.style = Paint.Style.STROKE

        var r1 = 100
        var xofset = 0 //向右为正
        var yofset = 0 //向下为正

        drawSharkOut(canvas , index , r1 , -65 , yofset , isRight , p)
        drawSharkIn(canvas , index , r1 , -65 , yofset , isRight , p)

        p.color = Color.WHITE
        canvas.drawLine(index - r1 + 65 - viewWidth/2, viewHeight / 2 , index +  r1 - 65  - viewWidth/2, viewHeight / 2, p)

        p.color = Color.BLACK
        drawFish(canvas , index , p)

    }

    private fun  drawFish(canvas: Canvas, index: Float, p: Paint) {

        var xofsetFish = 0F

        if(isFishLeft){
            xofsetFish = viewWidth / 5 * 2
        }else{
            xofsetFish = viewWidth / 5 * 3
        }


        var yofsetFish = viewHeight / 2
        val widthFish =  20F
        val heightFish = 12F
        val widthFishTail =  20F
        val heightFishTail = 10F



        canvas.translate(xofsetFish - viewWidth/2 , yofsetFish)
        canvas.rotate(90F)
        val per = (index - viewWidth / 5 * 2) / (viewWidth / 5 * 2)
        val degreesi =per * 360 + 10
        canvas.rotate(degreesi)

        if (per > 0.4) {

            xofsetFish = 0F
            yofsetFish = -viewWidth / 5


            val rectF = RectF(xofsetFish - widthFish, yofsetFish - heightFish, xofsetFish + widthFish, yofsetFish + heightFish)
            canvas.drawArc(rectF, 0F, 360F, false, p)

            canvas.drawLine(xofsetFish + widthFish, yofsetFish, xofsetFish + widthFish + widthFishTail, yofsetFish + heightFishTail, p)
            canvas.drawLine(xofsetFish + widthFish + widthFishTail, yofsetFish + heightFishTail, xofsetFish + widthFish + widthFishTail, yofsetFish - heightFishTail, p)
            canvas.drawLine(xofsetFish + widthFish + widthFishTail, yofsetFish - heightFishTail, xofsetFish + widthFish, yofsetFish, p)

            canvas.drawPoint(xofsetFish - widthFish / 3, yofsetFish, p)
        }
    }

    fun  drawSharkOut(canvas: Canvas , index: Float, r: Int, xofset: Int, yofset: Int, directionRight: Boolean, p: Paint) {
        if (directionRight){
            val xofset =  xofset - viewWidth/2
            val rectf = RectF(index - r + xofset, viewHeight / 2 - r , index + r + xofset , viewHeight / 2 + r )
            canvas.drawArc(rectf, -95F , 95F, false, p)
        }else{
            val xofset = 30 + xofset - viewWidth/2
            val rectf = RectF(index + xofset  , viewHeight / 2 - r , index + 2 * r + xofset, viewHeight / 2 + r )
            canvas.drawArc(rectf, -180F , 95F, false, p)
        }


    }

    fun  drawSharkIn(canvas: Canvas , index: Float, r1: Int, xofset: Int, yofset: Int, directionRight: Boolean, p: Paint) {
        val r = (1.5 * r1).toInt()
        if (directionRight) {
            val xofset = (- (r - ( r - r1) * 0.6)).toInt() + xofset - viewWidth/2
            val rectf = RectF(index - r + xofset, viewHeight / 2 - r + yofset, index + r + xofset, viewHeight / 2 + r + yofset)
            canvas.drawArc(rectf, -42F, 42F, false, p)
        }else{
            val xofset = -45 + xofset - viewWidth/2
            val rectf = RectF(index + r + xofset, viewHeight / 2 - r + yofset, index + 3 * r + xofset, viewHeight / 2 + r + yofset)
            canvas.drawArc(rectf, -180F, 42F, false, p)
        }

    }


    fun changeView(index: Float ,  directionRight : Boolean , time : Int ) {

        isRight = directionRight
        val va = ValueAnimator.ofFloat(oldIndex, index)
        va.duration = time.toLong()
        oldIndex = index
        va.interpolator = AccelerateDecelerateInterpolator()
        va.addUpdateListener { animation ->
            perIndex = animation.animatedValue as Float
            invalidate()
        }
        va.start()
    }

    fun startView(){



        doAsync {
            kotlin.run {
                while (true) {

                    var baseIndex = viewWidth / 5
                    baseIndex = viewWidth - baseIndex
                    uiThread {
                        changeView(baseIndex, false, 0)
                    }
                    Thread.sleep(300)
                    uiThread {
                        changeView(baseIndex, true, 0)
                    }
                    Thread.sleep(300)
                    uiThread {
                        changeView(baseIndex, false, 0)
                    }
                    Thread.sleep(500)
                    baseIndex = viewWidth - baseIndex
                    uiThread {
                        changeView(baseIndex, false, 2000)
                    }

                    Thread.sleep(2200)

                    isMirror = !isMirror

                }
            }
        }
    }

}