package com.ihomey.linkuphome.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.dip2px

class ThermometerView : View {


    // Dimension
    private var mOuterCircleRadius: Float = 0f
    private var mInnerCircleRadius: Float = 0f
    private var mScaleWidth: Float = 0f
    private var mScaleLength: Float = 0f

    // Color
    private var mInnerCircleColor: Int = 0
    private var mOuterCircleColor: Int = 0
    private var mUnReachedLineColor: Int = 0


    // Paint
    private val mInnerCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mOuterCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mUnReachedLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mReachedLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mScaleLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Parameters
    private var mCx: Float = 0f
    private val MAX_VALUE = 80f//最大温度值
    private val MIN_VALUE = -40f//最小温度值

    private var mOuterRectF: RectF = RectF()
    private var mUnReachedLineRectF: RectF = RectF()

    private var currentTemperature = -40f//当前温度


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
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThermometerView)
            mOuterCircleRadius = typedArray.getDimension(R.styleable.ThermometerView_outer_circle_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getContext().resources.displayMetrics))
            mInnerCircleRadius = typedArray.getDimension(R.styleable.ThermometerView_inner_circle_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getContext().resources.displayMetrics))
            mScaleWidth = typedArray.getDimension(R.styleable.ThermometerView_scale_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getContext().resources.displayMetrics))
            mScaleLength = typedArray.getDimension(R.styleable.ThermometerView_scale_length, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, getContext().resources.displayMetrics))
            mInnerCircleColor = typedArray.getColor(R.styleable.ThermometerView_inner_circle_color, Color.BLUE)
            mOuterCircleColor = typedArray.getColor(R.styleable.ThermometerView_outer_circle_color, Color.WHITE)
            mUnReachedLineColor = typedArray.getColor(R.styleable.ThermometerView_unreached_line_color, Color.BLUE)
            typedArray.recycle()
        }
        initElements()
    }

    private fun initElements() {
        mInnerCirclePaint.color = mInnerCircleColor
        mInnerCirclePaint.style = Paint.Style.FILL
        mOuterCirclePaint.color = mOuterCircleColor
        mOuterCirclePaint.style = Paint.Style.FILL

        mUnReachedLinePaint.color = mUnReachedLineColor
        mUnReachedLinePaint.style = Paint.Style.FILL

        mReachedLinePaint.color = mInnerCircleColor
        mReachedLinePaint.style = Paint.Style.FILL

        mScaleLinePaint.color = mOuterCircleColor
        mScaleLinePaint.strokeWidth = mScaleWidth
        mUnReachedLinePaint.style = Paint.Style.FILL
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        this.mCx = (width / 2).toFloat()
        setMeasuredDimension(width, width * 2 / 5)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()

        mOuterRectF.left = mCx - mInnerCircleRadius
        mOuterRectF.top = 0f
        mOuterRectF.right = mCx + mInnerCircleRadius
        mOuterRectF.bottom = height - mOuterCircleRadius
        canvas.drawRoundRect(mOuterRectF, mInnerCircleRadius, mInnerCircleRadius, mOuterCirclePaint)
        canvas.drawCircle(mCx, height - mOuterCircleRadius, mOuterCircleRadius, mOuterCirclePaint)

        mUnReachedLineRectF.left = mCx - mInnerCircleRadius / 3
        mUnReachedLineRectF.top = mInnerCircleRadius
        mUnReachedLineRectF.right = mCx + mInnerCircleRadius / 3
        mUnReachedLineRectF.bottom = height - mInnerCircleRadius * 2
        canvas.drawRoundRect(mUnReachedLineRectF, mInnerCircleRadius / 3, mInnerCircleRadius / 3, mUnReachedLinePaint)
        canvas.drawCircle(mCx, height - mOuterCircleRadius, mInnerCircleRadius, mInnerCirclePaint)


        val scaleEndY = height - mOuterCircleRadius * 2
        val scaleStartY = mInnerCircleRadius - context.dip2px(2f)
        val scaleCurrentY = mInnerCircleRadius + context.dip2px(2f)

        val rangY = (scaleEndY - scaleStartY) / 12
        for (i in 0 until 13) {
            if (i % 2 == 0) {
                canvas.drawLine(mCx - mInnerCircleRadius * 3 / 2 - mScaleLength * 5 / 3, scaleCurrentY + rangY * i, mCx - mInnerCircleRadius * 3 / 2, scaleCurrentY + rangY * i, mScaleLinePaint)
            } else {
                canvas.drawLine(mCx - mInnerCircleRadius * 3 / 2 - mScaleLength, scaleCurrentY + rangY * i, mCx - mInnerCircleRadius * 3 / 2, scaleCurrentY + rangY * i, mScaleLinePaint)
            }
        }

        val currentY = scaleCurrentY + rangY * (12 - (currentTemperature - MIN_VALUE) / 10)
        canvas.drawRect(mCx - mInnerCircleRadius / 3, currentY, mCx + mInnerCircleRadius / 3, height - mInnerCircleRadius * 2, mReachedLinePaint)
        canvas.restore()
    }


    fun setTemperature(temperature: Float) {
        when {
            temperature >= MAX_VALUE -> this.currentTemperature = MAX_VALUE
            temperature <= MIN_VALUE -> this.currentTemperature = MIN_VALUE
            else -> this.currentTemperature = temperature
        }
        invalidate()
    }

}