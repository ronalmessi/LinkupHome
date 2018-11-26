package com.ihomey.linkuphome.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.RectF
import android.util.Log
import android.util.TypedValue
import android.view.animation.LinearInterpolator


class HumidityView : View {

    // Paint
    private val mBigWaterDropPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mSmallWaterDropPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    // Paint
    private val mFirstWavePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mSecondWavePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mThirdWavePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    private var mInnerRadius = 0f
    private val mCenterPointF: PointF = PointF()
    private val mRectF: RectF = RectF()

    private var mBorderWidth = 0f

    private var humidityValue = 0f

    private var ω: Double = 0.toDouble()

    private var A: Int = 0


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
        mBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.2f, resources.displayMetrics)

        A = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3.75f, resources.displayMetrics).toInt()

        mBigWaterDropPaint.style = Paint.Style.FILL
        mBigWaterDropPaint.color = Color.parseColor("#FEFFFF")

        mSmallWaterDropPaint.style = Paint.Style.FILL
        mSmallWaterDropPaint.color = Color.parseColor("#C5E5EF")

        mFirstWavePaint.style = Paint.Style.FILL
        mFirstWavePaint.color = Color.parseColor("#74bcd6")

        mSecondWavePaint.style = Paint.Style.FILL
        mSecondWavePaint.color = Color.parseColor("#81cfe3")

        mThirdWavePaint.style = Paint.Style.FILL
        mThirdWavePaint.color = Color.parseColor("#97d9ed")

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        mCenterPointF.x = (width / 2).toFloat()
        mCenterPointF.y = Math.min(height, width).toFloat() * 17.5f / 40
        mInnerRadius = Math.min(height, width).toFloat() / 4

        mRectF.left = mCenterPointF.x - mInnerRadius + mBorderWidth
        mRectF.right = mCenterPointF.x + mInnerRadius - mBorderWidth
        mRectF.top = mCenterPointF.y - mInnerRadius + mBorderWidth
        mRectF.bottom = mCenterPointF.y + mInnerRadius - mBorderWidth

        ω = 18 * Math.PI / width
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBigWaterDrop(canvas)

//        drawSmallWaterDrop(canvas)

        if (humidityValue > 0) {
            drawWave(canvas, humidityValue + 12.8f, 1.0, mFirstWavePaint)
            drawWave(canvas, humidityValue + 6.4f, 2.0, mSecondWavePaint)
            drawWave(canvas, humidityValue, 1.0, mThirdWavePaint)
        }
    }

    private fun drawWave(canvas: Canvas, humidityValue: Float, startPeriod: Double, paint: Paint) {
        val waterHeight = humidityValue / 100 * (mInnerRadius - mBorderWidth) * 2
        val waterY = mCenterPointF.y + (mInnerRadius - mBorderWidth) - waterHeight
        val waterWidth = Math.sqrt(((mInnerRadius - mBorderWidth) * (mInnerRadius - mBorderWidth) - (mInnerRadius - mBorderWidth - waterHeight) * (mInnerRadius - mBorderWidth - waterHeight)).toDouble())
        drawSin(canvas, startPeriod, (mCenterPointF.x - waterWidth).toFloat(), waterY, (waterWidth * 2).toFloat(), paint)
    }


    private fun drawSin(canvas: Canvas, startPeriod: Double, startX: Float, startY: Float, width: Float, paint: Paint) {
        val path = Path()
        path.reset()
        var sinPathStartX = 0f
        var sinPathStopX = 0f
        var sinPathStartY = 0f
        var sinPathSopY = 0f
        var x = startX
        while (x <= startX + width) {
            val y = (A * Math.sin(ω * x + Math.PI * startPeriod) + A).toFloat()
            if (Math.sqrt(((mCenterPointF.x - x) * (mCenterPointF.x - x) + (mCenterPointF.y + y - startY) * (mCenterPointF.y + y - startY)).toDouble()) <= ((mInnerRadius - mBorderWidth))) {
                if (x <= mCenterPointF.x) {
                    if (sinPathStartX == 0f) {
                        sinPathStartX = x
                        sinPathStartY = startY - y
                        path.moveTo(sinPathStartX, sinPathStartY)
                    }
                } else {
                    if (x >= sinPathStopX) {
                        sinPathStopX = x
                        sinPathSopY = startY - y
                    }
                }
                path.lineTo(x, startY - y)
            }
            x += 0.5f
        }
        val startAngel = getRadian(sinPathStopX, sinPathSopY) * 180 / Math.PI - 90f
        val sweepAngel = Math.abs(getRadian(sinPathStopX, sinPathSopY) * 180 / Math.PI - getRadian(sinPathStartX, sinPathStartY) * 180 / Math.PI).toFloat()
        path.arcTo(mRectF, startAngel.toFloat(), sweepAngel)
        path.close()
        val linearGradient = LinearGradient(mCenterPointF.x, mCenterPointF.y + mInnerRadius / 2, mCenterPointF.x, mCenterPointF.y + mInnerRadius - mBorderWidth, intArrayOf(paint.color, Color.WHITE), null, Shader.TileMode.CLAMP)
        paint.shader = linearGradient
        canvas.drawPath(path, paint)
    }


    private fun getRadian(x: Float, y: Float): Float {
        var radian = Math.atan(((x - mCenterPointF.x) / (mCenterPointF.y - y)).toDouble()).toFloat()
        if (x > mCenterPointF.x && y > mCenterPointF.y) {
            radian += Math.PI.toFloat()
        } else if (x < mCenterPointF.x && y > mCenterPointF.y) {
            radian += Math.PI.toFloat()
        } else if (x < mCenterPointF.x && y < mCenterPointF.y) {
            radian = (2 * Math.PI + radian).toFloat()
        }
        return radian
    }


    private fun drawBigWaterDrop(canvas: Canvas) {
        canvas.drawCircle(mCenterPointF.x, mCenterPointF.y, mInnerRadius, mBigWaterDropPaint)
        val path = Path()
        path.moveTo(mCenterPointF.x, 0f)
        val aX = Math.cos(Math.PI * 36 / 180f).toFloat() * mInnerRadius
        val aY = Math.sin(Math.PI * 36 / 180f).toFloat() * mInnerRadius
        path.lineTo(mCenterPointF.x + aX, mCenterPointF.y - aY)
        path.lineTo(mCenterPointF.x - aX, mCenterPointF.y - aY)
        path.lineTo(mCenterPointF.x, 0f)
        path.close()
        canvas.drawPath(path, mBigWaterDropPaint)
    }

    private fun drawSmallWaterDrop(canvas: Canvas) {
        canvas.drawCircle(mCenterPointF.x - mInnerRadius, mCenterPointF.y / 2, mInnerRadius / 7, mSmallWaterDropPaint)
        val path = Path()
        path.moveTo(mCenterPointF.x - mInnerRadius, 0f)
        val aX = Math.cos(Math.PI * 36 / 180f).toFloat() * mInnerRadius
        val aY = Math.sin(Math.PI * 36 / 180f).toFloat() * mInnerRadius
        path.lineTo(mCenterPointF.x - mInnerRadius + aX, mCenterPointF.y / 7 - aY)
        path.lineTo(mCenterPointF.x - mInnerRadius - aX, mCenterPointF.y / 7 - aY)
        path.lineTo(mCenterPointF.x - mInnerRadius, 0f)
        path.close()
        canvas.drawPath(path, mSmallWaterDropPaint)
    }


    fun setHumidityValue(value: Float) {
        val animator = ValueAnimator.ofFloat(0f, value)
        animator.addUpdateListener { animation ->
            humidityValue = animation?.animatedValue as Float
            invalidate()
        }
        animator.duration = 800
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

}