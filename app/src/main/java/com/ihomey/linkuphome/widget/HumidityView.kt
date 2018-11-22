package com.ihomey.linkuphome.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

import android.view.MotionEvent
import com.umeng.analytics.pro.y
import com.umeng.analytics.pro.x
import android.graphics.RectF
import android.R.attr.centerX
import android.graphics.PointF









class HumidityView : View {


//    // Dimension
//    private var mOuterCircleRadius: Float = 0f
//    private var mInnerCircleRadius: Float = 0f
//    private var mScaleWidth: Float = 0f
//    private var mScaleLength: Float = 0f
//
//    // Color
//    private var mInnerCircleColor: Int = 0
//    private var mOuterCircleColor: Int = 0
//    private var mUnReachedLineColor: Int = 0
//
//
    // Paint
    private val mBigWaterDropPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mSmallWaterDropPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val mUnReachedLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val mReachedLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val mScaleLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
//
    // Parameters
    private var mCx: Float = 0f
    private var mCY: Float = 0f
//    private val MAX_VALUE = 80f//最大温度值
//    private val MIN_VALUE = -40f//最小温度值
//
//    private var mOuterRectF: RectF = RectF()
//    private var mUnReachedLineRectF: RectF = RectF()
//
//    private var currentTemperature = -40f//当前温度


    /**
     * 进度范围为0-360
     */
    val PROGRESS_PROPERTY = "progress"
    /**
     * 画笔
     */
    private val paint = Paint()
    /**
     * 范围为210 - 0
     */
    private val progress = 210f
    /**
     * 动画时间
     */
    private val DURATION = 2000
    /**
     * 颜色的数组，外圈直接根据顺序生成
     */
    private val colors: IntArray? = null
    /**
     * 指针的颜色
     */
    private val pointerColor: Int = 0
    /**
     * 平均的普通角度，不是弧度
     */
    private val averageAngle: Float = 0.toFloat()


    /**
     * 供给属性动画调用的方法
     *
     * @param progress 进度值从210开始0结束，此区间内随意取值
     */


    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
//        if (attrs != null) {
//            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThermometerView)
//            mOuterCircleRadius = typedArray.getDimension(R.styleable.ThermometerView_outer_circle_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getContext().resources.displayMetrics))
//            mInnerCircleRadius = typedArray.getDimension(R.styleable.ThermometerView_inner_circle_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getContext().resources.displayMetrics))
//            mScaleWidth = typedArray.getDimension(R.styleable.ThermometerView_scale_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getContext().resources.displayMetrics))
//            mScaleLength = typedArray.getDimension(R.styleable.ThermometerView_scale_length, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, getContext().resources.displayMetrics))
//            mInnerCircleColor = typedArray.getColor(R.styleable.ThermometerView_inner_circle_color, Color.BLUE)
//            mOuterCircleColor = typedArray.getColor(R.styleable.ThermometerView_outer_circle_color, Color.WHITE)
//            mUnReachedLineColor = typedArray.getColor(R.styleable.ThermometerView_unreached_line_color, Color.BLUE)
//            typedArray.recycle()
//        }
        initElements()
    }

    private fun initElements() {
//        mInnerCirclePaint.color = mInnerCircleColor
//        mInnerCirclePaint.style = Paint.Style.FILL
//        mOuterCirclePaint.color = mOuterCircleColor
//        mOuterCirclePaint.style = Paint.Style.FILL
//
//        mUnReachedLinePaint.color = mUnReachedLineColor
//        mUnReachedLinePaint.style = Paint.Style.FILL
//
//        mReachedLinePaint.color = mInnerCircleColor
//        mReachedLinePaint.style = Paint.Style.FILL
//
//        mScaleLinePaint.color = mOuterCircleColor
//        mScaleLinePaint.strokeWidth = mScaleWidth
//        mUnReachedLinePaint.style = Paint.Style.FILL

        mBigWaterDropPaint.style = Paint.Style.STROKE
        mBigWaterDropPaint.color = Color.BLACK
        mBigWaterDropPaint.strokeWidth = 6f
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        this.mCx = (width / 2).toFloat()
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        this.mCY = (height / 2).toFloat()
        setMeasuredDimension(width, height)
    }

    private val controlPoint = Point(200, 200)


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.save()
//
//        mOuterRectF.left = mCx - mInnerCircleRadius
//        mOuterRectF.top = 0f
//        mOuterRectF.right = mCx + mInnerCircleRadius
//        mOuterRectF.bottom = height - mOuterCircleRadius
//        canvas.drawRoundRect(mOuterRectF, mInnerCircleRadius, mInnerCircleRadius, mOuterCirclePaint)
//        canvas.drawCircle(mCx, height - mOuterCircleRadius, mOuterCircleRadius, mOuterCirclePaint)
//
//        mUnReachedLineRectF.left = mCx - mInnerCircleRadius / 3
//        mUnReachedLineRectF.top = mInnerCircleRadius
//        mUnReachedLineRectF.right = mCx + mInnerCircleRadius / 3
//        mUnReachedLineRectF.bottom = height - mInnerCircleRadius * 2
//        canvas.drawRoundRect(mUnReachedLineRectF, mInnerCircleRadius / 3, mInnerCircleRadius / 3, mUnReachedLinePaint)
//        canvas.drawCircle(mCx, height - mOuterCircleRadius, mInnerCircleRadius, mInnerCirclePaint)
//
//        val scaleEndY = height - mOuterCircleRadius * 2
//        val scaleStartY = mInnerCircleRadius - context.dip2px(2f)
//        val scaleCurrentY = mInnerCircleRadius + context.dip2px(2f)
//
//        val rangY = (scaleEndY - scaleStartY) / 12
//        for (i in 0 until 13) {
//            if (i % 2 == 0) {
//                canvas.drawLine(mCx - mInnerCircleRadius * 3 / 2 - mScaleLength * 5 / 3, scaleCurrentY + rangY * i, mCx - mInnerCircleRadius * 3 / 2, scaleCurrentY + rangY * i, mScaleLinePaint)
//            } else {
//                canvas.drawLine(mCx - mInnerCircleRadius * 3 / 2 - mScaleLength, scaleCurrentY + rangY * i, mCx - mInnerCircleRadius * 3 / 2, scaleCurrentY + rangY * i, mScaleLinePaint)
//            }
//        }
//        val currentY = scaleCurrentY + rangY * (12 - (currentTemperature - MIN_VALUE) / 10)
//        canvas.drawRect(mCx - mInnerCircleRadius / 3, currentY, mCx + mInnerCircleRadius / 3, height - mInnerCircleRadius * 2, mReachedLinePaint)


//        val path = Path()
//        path.moveTo(mCx, 0f)
//        path.quadTo(controlPoint.x.toFloat(), controlPoint.y.toFloat(), mCx-180, mCY/4)
//
//        //绘制路径
//        canvas.drawPath(path, mBigWaterDropPaint)
////
////
////        path.moveTo(mCx, 0f)
////        path.quadTo(width-controlPoint.x.toFloat(), controlPoint.y.toFloat(), mCx, mCY/2)
////
////        //绘制路径
////        canvas.drawPath(path, mBigWaterDropPaint)
//
//
//        val rect = RectF(mCx-180,mCY/4-180,mCx+180,mCY/4+180)
//
//        canvas.drawArc(rect,0f,180f,false,mBigWaterDropPaint);
//
//
//        //绘制辅助点
//        canvas.drawPoint(controlPoint.x.toFloat(), controlPoint.y.toFloat(), mBigWaterDropPaint)
//        canvas.restore()
//        paint.isAntiAlias = true
        /**
         * 初始化圆的位置
         */
//        val bounds = getBounds()
        //计算不同的空间的size取最大，最好宽高一样
        val size = Math.min(height, width)
        val strokeWidth = size / 13f
        val radius = size / (480f / 252f)
        val center = PointF((width/2).toFloat(), size / (480f / 288f))
        val left = center.x - radius
        val top = center.y - radius
        val right = center.x + radius
        val bottom = center.y + radius
        val arcBounds = RectF(left, top, right, bottom)

        /**
         * 画指针背景,颜色不能少于3种
         */
        //获取平均的角度
        paint.style = Paint.Style.STROKE
        //宽度是36,比例为17
        paint.strokeWidth = strokeWidth

        paint.strokeCap = Paint.Cap.ROUND

        paint.color = Color.BLACK

        canvas.drawArc(arcBounds, 150 + 60f * (4 - 2), 60f, false, paint)

//        for (i in 1..colors.length) {
//            //整个的原型下半部分开口是120度
//            //从150度开始从240度结束
//            if (i == 1 || i == colors.length - 1)
//                paint.strokeCap = Paint.Cap.ROUND
//            else
//                paint.strokeCap = Paint.Cap.BUTT
//            if (i == colors.length - 1) {
//                paint.color = colors[i]
//                canvas.drawArc(arcBounds, 150 + averageAngle * i, averageAngle, false, paint)
//            } else if (i == colors.length) {
//                paint.color = colors[i - 2]
//                canvas.drawArc(arcBounds, 150 + averageAngle * (i - 2), averageAngle, false, paint)
//            } else {
//                paint.color = colors[i - 1]
//                canvas.drawArc(arcBounds, 150 + averageAngle * (i - 1), averageAngle, false, paint)
//            }
//        }

        /**
         * 画指针不变的圆
         */
        //中心圆的半径为240直径与圆半径的比例为5.2
        paint.style = Paint.Style.FILL
        val innerRadius = size / 4f
        canvas.drawCircle(center.x, center.y, innerRadius, paint)

//        /**
//         * 变动的针尖
//         */
//        val path = Path()
//        path.moveTo(center.x, center.y)
//        val degrees1 = 1 / 4f * Math.PI//1/2内角度，此处决定了针尖的长度，4f处越大，针越小
//        val radians = Math.PI / 180f * progress
//        val aX = Math.cos(degrees1 + radians).toFloat() * innerRadius
//        val aY = Math.sin(degrees1 + radians).toFloat() * innerRadius
//        path.lineTo(center.x + aX, center.y - aY)
//        val bX = (Math.cos(radians) * innerRadius / Math.cos(degrees1)).toFloat()
//        val bY = (Math.sin(radians) * innerRadius / Math.cos(degrees1)).toFloat()
//        path.lineTo(center.x + bX, center.y - bY)
//        val cX = Math.cos(radians - degrees1).toFloat() * innerRadius
//        val cY = Math.sin(radians - degrees1).toFloat() * innerRadius
//        path.lineTo(center.x + cX, center.y - cY)
//        canvas.drawPath(path, paint)
//        /**
//         * 写字
//         */
//        if (!TextUtils.isEmpty(text)) {
//            val textSize = size / 8f
//            paint.textSize = textSize
//            paint.color = Color.WHITE
//            val textPath = Path()
//            paint.getTextPath(text, 0, text.length(), center.x, center.y, textPath)
//            val textBounds = RectF()
//            textPath.computeBounds(textBounds, true)
//            val textX = textBounds.right - textBounds.left
//            val textY = textBounds.bottom - textBounds.top
//            canvas.drawText(text, center.x - textX / 2, center.y + textY / 2, paint)
//        }

    }


//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        when (event.action) {
//            MotionEvent.ACTION_MOVE -> {
//                controlPoint.x = event.x.toInt()
//                controlPoint.y = event.y.toInt()
//                invalidate()
//            }
//        }
//        return true
//    }


//    fun setTemperature(temperature: Float) {
//        val animator = ValueAnimator.ofFloat(-40f, temperature)
//        animator.addUpdateListener { animation ->
//            currentTemperature = animation?.animatedValue as Float
//            invalidate()
//        }
//        animator.duration = 800
//        animator.interpolator = LinearInterpolator()
//        animator.start()
//    }
}