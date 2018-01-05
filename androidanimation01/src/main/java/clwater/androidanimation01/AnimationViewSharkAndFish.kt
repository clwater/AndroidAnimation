package clwater.androidanimation01

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by gengzhibo on 17/12/22.
 */
class AnimationViewSharkAndFish : View {

    var viewWidth : Float = 0F  //view宽度
    var viewHeight : Float = 0F //view高度
    var perIndex : Float = 0F   //当前坐标
    var oldIndex : Float = 0F   //上一次坐标
    var isMirror = false        //是否需要镜像(从左向右展示)
    var isRight = true          //鲨鱼鳍是否向右
    var isRunning = true        //是否运行
    var viewBackgroundColor = Color.WHITE   //背景颜色


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

        //设置起始index位置
        perIndex =  viewWidth - viewWidth / 5
        oldIndex = perIndex

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
        //将画布中心移动到中心的位置
        canvas.translate(viewWidth/2F , viewHeight / 2F)

        //一个整体的训循环中两部分是左右对称的
        //当完成右到左的部分之后想x轴镜像一下就可完成从左向右的部分
        if(isMirror){
            canvas.scale( -1F ,1F)
        }

        val p = Paint()
        p.color = Color.BLACK
        p.strokeWidth = 5F
        //绘制水面的线
        canvas.drawLine(-viewWidth / 2, 0F , viewWidth/2, 0F , p)

        canvas.save()
        //绘制鲨鱼及小鱼
        drawShark(perIndex , canvas)
        canvas.restore()
        //绘制底部遮盖
        drawBottom(canvas)

    }
    //绘制底部遮盖
    private fun drawBottom(canvas: Canvas) {
        val p = Paint()
        p.color = viewBackgroundColor
        canvas.drawRect(RectF(-viewWidth / 2,  3F, viewWidth / 2, viewHeight / 10), p)
    }


    //绘制鲨鱼鳍
    //鲨鱼鳍由两个圆弧组成
    private fun  drawShark(index: Float, canvas: Canvas) {
        //初始化画笔相关
        val p = Paint()
        p.strokeWidth = 5F
        p.color = Color.BLACK
        p.style = Paint.Style.STROKE

        //设置外面鱼鳍的半径
        val r = 100F
        //设置偏移量
        var xofset = -65 //向右为正
        var yofset = 0 //向下为正

        //绘制外面鱼鳍
        drawSharkOut(canvas , index , r , xofset , yofset , isRight , p)
        //绘制内部鱼鳍
        drawSharkIn(canvas , index , r , xofset , yofset , isRight , p)

        p.color = viewBackgroundColor
        //将鱼鳍出现的水面部分设置为空
        canvas.drawLine(index - r - xofset - viewWidth/2, 0F , index +  r + xofset  - viewWidth/2, 0F, p)

        p.color = Color.BLACK
        //绘制前面的小鱼
        drawFish(canvas , index , p)

    }
    //绘制前面逃跑的小鱼
    private fun  drawFish(canvas: Canvas, index: Float, p: Paint) {

        //设置鱼运动轨迹的圆心
        var xofsetFish = viewWidth / 5 * 2
        var yofsetFish = 0F
        //设置鱼的宽度及高度
        val widthFish =  20F
        val heightFish = 12F
        //设置鱼鳍的宽度及高度
        val widthFishTail =  20F
        val heightFishTail = 10F

        //将画布中心移动到鱼轨迹的中心位置
        canvas.translate(xofsetFish - viewWidth/2 , yofsetFish)
        //旋转画布旋转到起始角度
        canvas.rotate(90F)
        //根据当前的坐标位置计算鱼的偏移量及偏移角度
        val per = (index - viewWidth / 5 * 2) / (viewWidth / 5 * 2)
        val degreesi =per * 360  + 10
        canvas.rotate(degreesi)


        //如果当前per小于0.4说明整体在水面下,则不进行绘制
        if (per > 0.4) {

            xofsetFish = 0F
            yofsetFish = -viewWidth / 5

            //绘制鱼的身体部分并通过背景颜色填充
            //通过这种方法来覆盖水面和鱼接触的部分
            val rectF = RectF(xofsetFish - widthFish, yofsetFish - heightFish, xofsetFish + widthFish, yofsetFish + heightFish)
            p.color = viewBackgroundColor
            p.style = Paint.Style.FILL
            canvas.drawArc(rectF, 0F, 360F, true, p)

            //绘制鱼的身体部分
            p.color = Color.BLACK
            p.style = Paint.Style.STROKE
            canvas.drawArc(rectF, 0F, 360F, true, p)

            //绘制鱼尾的部分
            canvas.drawLine(xofsetFish + widthFish, yofsetFish, xofsetFish + widthFish + widthFishTail, yofsetFish + heightFishTail, p)
            canvas.drawLine(xofsetFish + widthFish + widthFishTail, yofsetFish + heightFishTail, xofsetFish + widthFish + widthFishTail, yofsetFish - heightFishTail, p)
            canvas.drawLine(xofsetFish + widthFish + widthFishTail, yofsetFish - heightFishTail, xofsetFish + widthFish, yofsetFish, p)

            //绘制鱼的眼睛
            canvas.drawPoint(xofsetFish - widthFish / 3, yofsetFish , p)


            //当per在[0.45,0.48]区间中绘制水面的水花
            if (per <= 0.48&& per >= 0.45){

                val water = 50F
                val waterR = 30F
                canvas.translate(0F ,yofsetFish + water)
                p.color = Color.BLACK
                p.style = Paint.Style.STROKE
                val waterRectF = RectF(-waterR , -waterR , waterR , waterR)
                canvas.drawArc(waterRectF, -90F , 30F , false, p)
                //计算区间的跨度,设置统一方向下两半水花的分散程度
                val changeWater =   ( 1 - (per - 0.45) / 0.03) * 45
                canvas.drawArc(waterRectF, (-60 + changeWater).toFloat(), 30F , false, p)

                canvas.translate(0F , - 2 * water)
                canvas.drawArc(waterRectF, 90F , -30F , false, p)
                canvas.drawArc(waterRectF, (90 - changeWater).toFloat(), -30F , false, p)
            }
        }
    }
    //区分鱼鳍的方向绘制外部鱼鳍
    fun  drawSharkOut(canvas: Canvas , index: Float, r: Float, xofset: Int, yofset: Int, directionRight: Boolean, p: Paint) {
        if (directionRight){
            val xofset =  xofset - viewWidth/2
            val rectf = RectF(index - r + xofset, -r + yofset , index + r + xofset , r  + yofset)
            canvas.drawArc(rectf, -95F , 95F, false, p)
        }else{
            val xofset = 30 + xofset - viewWidth/2
            val rectf = RectF(index + xofset + yofset  ,  -r , index + 2 * r + xofset, r + yofset )
            canvas.drawArc(rectf, -180F , 95F, false, p)
        }


    }
    //区分鱼鳍方向绘制内部鱼鳍
    fun  drawSharkIn(canvas: Canvas , index: Float, r1: Float, xofset: Int, yofset: Int, directionRight: Boolean, p: Paint) {
        val r = (1.5 * r1).toInt()
        if (directionRight) {
            val xofset = (- (r - ( r - r1) * 0.6)).toInt() + xofset - viewWidth/2
            val rectf = RectF(index - r + xofset, (- r + yofset).toFloat(), index + r + xofset, (+ r + yofset).toFloat())
            canvas.drawArc(rectf, -42F, 42F, false, p)
        }else{
            val xofset = -45 + xofset - viewWidth/2
            val rectf = RectF(index + r + xofset, (- r + yofset).toFloat(), index + 3 * r + xofset, (+ r + yofset).toFloat())
            canvas.drawArc(rectf, -180F, 42F, false, p)
        }

    }

    //更换当前的坐标,更新绘制的画面样式
    fun changeView(index: Float ,  directionRight : Boolean , time : Int ) {

        isRight = directionRight
        val va = ValueAnimator.ofFloat(oldIndex, index)
        va.duration = time.toLong()
        oldIndex = index
        va.addUpdateListener { animation ->
            perIndex = animation.animatedValue as Float
            invalidate()
        }
        va.start()
    }

    //停止动画
    //一个完整的周期是一个原子操作,可以自行设置相关策略
    fun stopView(){
        isRunning = false
    }
    //开始动画
    fun startView(){
        isRunning = true
        doAsync {
            kotlin.run {
                while (isRunning) {
                    //动画的策略
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
                        changeView(baseIndex, false, 3000)
                    }

                    Thread.sleep(3200)
                    //设置镜像x轴信息
                    isMirror = !isMirror
                }
            }
        }
    }

}