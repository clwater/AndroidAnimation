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
import android.view.View
import android.view.animation.OvershootInterpolator


/**
 * Created by gengzhibo on 17/12/22.
 */
class DashBoard : View {


    private var paint: Paint? = null
    private var tmpPaint: Paint? = null
    private var textPaint: Paint? = null
    private var strokePain: Paint? = null
    private var rect: RectF? = null
    private var backGroundColor: Int = 0    //背景色
    private var pointLength: Float = 0.toFloat()      //指针长度
    private var per: Float = 0.toFloat()             //指数百分比
    private var perPoint: Float = 0.toFloat()        //缓存(变化中)指针百分比
    private var perOld: Float = 0.toFloat()          //变化前指针百分比
    private var length: Float = 0.toFloat()          //仪表盘半径
    private var r: Float = 0.toFloat()

    constructor(context: Context) : super(context) {
        init()
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val heitht = width / 2 / 4 * 5
        initIndex(width / 2)
        //优化组件高度
        setMeasuredDimension(width, heitht)
    }


    private fun initIndex(specSize: Int) {
        backGroundColor = Color.WHITE
        r = specSize.toFloat()
        length = r / 4 * 3
        pointLength = -(r * 0.6).toFloat()
        per = 0f
        perOld = 0f
    }


    private fun init() {
        paint = Paint()
        rect = RectF()
        textPaint = Paint()
        tmpPaint = Paint()
        strokePain = Paint()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    fun setR(r: Float) {
        this.r = r
        this.length = r / 4 * 3
    }

    override fun onDraw(canvas: Canvas) {

        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        //颜色指示的环
        initRing(canvas)
        //刻度文字
        initScale(canvas)
        //指针
        initPointer(canvas)
        //提示内容
        initText(canvas)
    }

    private fun initText(canvas: Canvas) {
        //抗锯齿
        canvas.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.restore()
        canvas.save()
        canvas.translate((canvas.width / 2).toFloat(), r)

        val rIndex = length

        //设置文字展示的圆环
        paint!!.color = Color.parseColor("#eeeeee")
        paint!!.shader = null
        paint!!.setShadowLayer(5f, 0f, 0f, 0x54000000)
        rect = RectF(-(rIndex / 3), -(rIndex / 3), rIndex / 3, rIndex / 3)
        canvas.drawArc(rect!!, 0f, 360f, true, paint!!)

        paint!!.clearShadowLayer()

        canvas.restore()
        canvas.save()
        canvas.translate(canvas.width / 2f, r)


        textPaint!!.strokeWidth = 1f
        textPaint!!.isAntiAlias = true

        textPaint!!.textSize = 60f
        textPaint!!.color = Color.parseColor("#fc6555")
        textPaint!!.textAlign = Paint.Align.RIGHT


        //判断指数变化及颜色设定

        val _per = (per * 120).toInt()

        if (_per < 60) {
            textPaint!!.color = Color.parseColor("#ff6450")
        } else if (_per < 100) {
            textPaint!!.color = Color.parseColor("#f5a623")
        } else {
            textPaint!!.color = Color.parseColor("#79d062")
        }

        var swidth = textPaint!!.measureText(_per.toString())
        //计算偏移量 是的数字和百分号整体居中显示
        swidth = swidth - (swidth + 22) / 2


        canvas.translate(swidth, 0f)
        canvas.drawText("" + _per, 0f, 0f, textPaint!!)

        textPaint!!.textSize = 30f
        textPaint!!.textAlign = Paint.Align.LEFT

        canvas.drawText("%", 0f, 0f, textPaint!!)
        textPaint!!.textAlign = Paint.Align.CENTER
        textPaint!!.color = Color.parseColor("#999999")


        canvas.restore()
        canvas.save()
        canvas.translate((canvas.width / 2).toFloat(), r + length / 3f / 2f)
        canvas.drawText("完成率", 0f, 0f, textPaint!!)


    }


    fun setBackGroundColor(color: Int) {
        this.backGroundColor = color
    }

    fun setPointLength1(pointLength1: Float) {
        this.pointLength = -length * pointLength1
    }

    private fun initScale(canvas: Canvas) {
        canvas.restore()
        canvas.save()
        canvas.translate((canvas.width / 2).toFloat(), r)
        paint!!.color = Color.parseColor("#999999")

        tmpPaint = Paint(paint) //小刻度画笔对象
        tmpPaint!!.strokeWidth = 1f
        tmpPaint!!.textSize = 35f
        tmpPaint!!.textAlign = Paint.Align.CENTER



        canvas.rotate(-90f, 0f, 0f)

        var y = length
        y = -y
        val count = 12 //总刻度数
        paint!!.color = backGroundColor

        val tempRou = 180 / 12f

        paint!!.color = Color.WHITE
        paint!!.strokeWidth = 5f

        //绘制刻度和百分比
        for (i in 0..count) {

            if (i % 2 == 0) {
                canvas.drawText((i * 10).toString(), 0f, y - 20f, tmpPaint!!)
            }

            canvas.drawLine(0f, y, 0f, y + length / 15, paint!!)


            canvas.rotate(tempRou, 0f, 0f)
        }

    }


    private fun initPointer(canvas: Canvas) {
        paint!!.color = Color.BLACK


        canvas.restore()
        canvas.save()
        canvas.translate((canvas.width / 2).toFloat(), r)
        val change: Float

        if (perPoint < 1) {
            change = perPoint * 180
        } else {
            change = 180f
        }

        //根据参数得到旋转角度
        canvas.rotate(-90 + change, 0f, 0f)

        //绘制三角形形成指针
        val path = Path()
        path.moveTo(0f, pointLength)
        path.lineTo(-10f, 0f)
        path.lineTo(10f, 0f)
        path.lineTo(0f, pointLength)
        path.close()

        canvas.drawPath(path, paint!!)

    }

    private fun initRing(canvas: Canvas) {
        paint!!.isAntiAlias = true
        paint!!.strokeWidth = 2f
        canvas.save()
        canvas.translate((canvas.width / 2).toFloat(), r)


        //前100红黄渐变圆环
        paint!!.style = Paint.Style.FILL
        val colors = intArrayOf(Color.parseColor("#F95A37"), Color.parseColor("#f9cf45"), Color.parseColor("#00ff00"))
        val positions = floatArrayOf(0.5f - 10f / 180f * 0.5f, 0.5f + 0.5f * 5f / 6f, 1.0f)
        var sweepGradient = SweepGradient(0f, 0f, colors, positions)
        paint!!.shader = sweepGradient
        rect = RectF(-length, -length, length, length)
        canvas.drawArc(rect!!, 170f, 10f + 180f / 6f * 5f, true, paint!!)


        //100之后绿色渐变圆环
        paint!!.style = Paint.Style.FILL
        canvas.rotate(10f, 0f, 0f)
        val colors2 = intArrayOf(Color.parseColor("#79D062"), Color.parseColor("#3FBF55"))
        val positions2 = floatArrayOf(0.5f + 0.5f * (144f / 180f), 1.0f)
        sweepGradient = SweepGradient(0f, 0f, colors2, positions2)
        paint!!.shader = sweepGradient
        rect = RectF(-length, -length, length, length)
        canvas.drawArc(rect!!, 180f + 180f * (140f / 180f), 180f / 6 + 10, true, paint!!)



        canvas.restore()
        canvas.save()
        canvas.translate((canvas.width / 2).toFloat(), r)

        strokePain = Paint(paint)

        strokePain!!.color = 0x3f979797
        strokePain!!.strokeWidth = 10f
        strokePain!!.shader = null
        strokePain!!.style = Paint.Style.STROKE
        canvas.drawArc(rect!!, 170f, 200f, true, strokePain!!)



        canvas.restore()
        canvas.save()
        canvas.translate((canvas.width / 2).toFloat(), r)

        //底边水平
        paint!!.shader = null
        paint!!.color = backGroundColor
        paint!!.style = Paint.Style.FILL
        canvas.drawRect(-length, (Math.sin(Math.toRadians(10.0)) * length / 3f * 2f).toFloat(), length, (Math.sin(Math.toRadians(10.0)) * length + 100).toFloat(), paint!!)
        canvas.drawRect(-length, (Math.sin(Math.toRadians(10.0)) * length / 3f * 2f).toFloat(), length, (Math.sin(Math.toRadians(10.0)) * length / 3f * 2f).toFloat(), strokePain!!)


        //内部背景色填充
        paint!!.color = backGroundColor
        paint!!.shader = null
        rect = RectF(-(length - length / 3f - 2f), -(length / 3f * 2f - 2), length - length / 3f - 2f, length / 3f * 2f - 2)
        canvas.drawArc(rect!!, 170f, 200f, true, strokePain!!)
        canvas.drawArc(rect!!, 0f, 360f, true, paint!!)


    }


    fun cgangePer(per: Float) {
        this.perOld = this.per
        this.per = per
        val va = ValueAnimator.ofFloat(perOld, per)
        va.duration = 1000
        va.interpolator = OvershootInterpolator()
        va.addUpdateListener { animation ->
            perPoint = animation.animatedValue as Float
            invalidate()
        }
        va.start()

    }
}