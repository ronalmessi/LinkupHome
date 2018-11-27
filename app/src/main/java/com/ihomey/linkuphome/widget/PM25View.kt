package com.ihomey.linkuphome.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import com.ihomey.linkuphome.R

class PM25View : View {


    // Status
    private val INSTANCE_STATUS = "instance_status"
    private val STATUS_VALUE = "status_value"

    // Dimension
    private var mScaleLineWidth: Float = 0f
    private var mScaleLineLength: Float = 0f
    private var mValueTextSize: Float = 0f
    private var mUnitTextSize: Float = 0f
    private var mLevelTextSize: Float = 0f

    // Color
    private var mScaleLineHightColor: Int = 0
    private var mScaleLineColor: Int = 0

    // Paint
    private val mScaleLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mScaleHighLightLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mValueTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mUnitTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mLevelTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Parameters
    private var mCx: Float = 0f
    private var mCy: Float = 0f
    private var mCurrentPM25Value: Int = 0

    private lateinit var logoBitmap: Bitmap


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
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PM25View)
            mValueTextSize = typedArray.getDimension(R.styleable.PM25View_value_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 54f, getContext().resources.displayMetrics))
            mLevelTextSize = typedArray.getDimension(R.styleable.PM25View_level_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, getContext().resources.displayMetrics))
            mUnitTextSize = typedArray.getDimension(R.styleable.PM25View_unit_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15f, getContext().resources.displayMetrics))
            mScaleLineWidth = typedArray.getDimension(R.styleable.PM25View_scale_line_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getContext().resources.displayMetrics))
            mScaleLineLength = typedArray.getDimension(R.styleable.PM25View_scale_line_length, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27f, getContext().resources.displayMetrics))
            mScaleLineHightColor = typedArray.getColor(R.styleable.PM25View_scale_line_highlight_color, Color.BLUE)
            mScaleLineColor = typedArray.getColor(R.styleable.PM25View_scale_line_color, Color.WHITE)
            typedArray.recycle()
        }
        initElements()
    }


    private fun initElements() {

        mValueTextPaint.textSize = mValueTextSize
        mValueTextPaint.color = mScaleLineHightColor
        mValueTextPaint.style = Paint.Style.FILL
        mValueTextPaint.typeface = Typeface.DEFAULT_BOLD

        mUnitTextPaint.color = mScaleLineHightColor
        mUnitTextPaint.textSize = mUnitTextSize
        mUnitTextPaint.style = Paint.Style.FILL
        mUnitTextPaint.isAntiAlias = true

        mLevelTextPaint.textSize = mLevelTextSize
        mLevelTextPaint.style = Paint.Style.FILL
        mLevelTextPaint.isAntiAlias = true

        mScaleHighLightLinePaint.color = mScaleLineHightColor
        mScaleHighLightLinePaint.strokeWidth = mScaleLineWidth
        mScaleHighLightLinePaint.isAntiAlias = true
        mScaleHighLightLinePaint.style = Paint.Style.FILL

        mScaleLinePaint.color = mScaleLineColor
        mScaleLinePaint.isAntiAlias = true
        mScaleLinePaint.strokeWidth = mScaleLineWidth
        mScaleLinePaint.style = Paint.Style.FILL

        logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_pm25)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        this.mCx = (width / 2).toFloat()
        this.mCy = (width / 2).toFloat()
        setMeasuredDimension(width, width)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawScaleLine(canvas)
        drawPM25Value(canvas)

        val scaleBitmap = scaleBitmap(logoBitmap)
        canvas.drawBitmap(scaleBitmap, mCx - scaleBitmap.width / 2, mCy * 3 / 2 - scaleBitmap.height / 3, mLevelTextPaint)
    }

    private fun drawPM25Value(canvas: Canvas) {
        canvas.drawText(mCurrentPM25Value.toString(), mCx - mValueTextPaint.measureText(mCurrentPM25Value.toString()) / 2, mCy + getFontHeight(mValueTextPaint, mCurrentPM25Value.toString()) / 5, mValueTextPaint)
        canvas.drawText("μg/m³", mCx - mUnitTextPaint.measureText("μg/m³") / 2, mCy + getFontHeight(mValueTextPaint, mCurrentPM25Value.toString()) * 7 / 24 + getFontHeight(mUnitTextPaint, "μg/m³"), mUnitTextPaint)
        val levelText = getLevel()
        canvas.drawText(levelText, mCx - mLevelTextPaint.measureText(levelText) / 2, mCy * 2 - getFontHeight(mLevelTextPaint, levelText) / 2, mLevelTextPaint)
    }

    private fun scaleBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        val scale = (mUnitTextPaint.measureText("μg/m³") * 2 / 3) / bitmap.width
        matrix.postScale(scale, scale)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun drawScaleLine(canvas: Canvas) {
        val mCurrentRadian = Math.toRadians(mCurrentPM25Value * 300 / 500.00)
        for (i in 0..60) {
            canvas.save()
            canvas.rotate((360 / 72 * i - 150).toFloat(), mCx, mCy)
            if (5 * i <= Math.toDegrees(mCurrentRadian) && Math.toDegrees(mCurrentRadian) < 301 && Math.toDegrees(mCurrentRadian) > 0) {
                canvas.drawLine(mCx, 0f, mCx, mScaleLineLength, mScaleHighLightLinePaint)
            } else {
                canvas.drawLine(mCx, 0f, mCx, mScaleLineLength, mScaleLinePaint)
            }
            canvas.restore()
        }
    }

    private fun getFontHeight(paint: Paint, text: String): Float {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height().toFloat()
    }

    private fun getLevel(): String {
        var levelText = "优"
        when (mCurrentPM25Value) {
            in 0..50 -> {
                levelText = "优"
                mLevelTextPaint.color = Color.parseColor("#00A750")
            }
            in 51..100 -> {
                levelText = "良"
                mLevelTextPaint.color = Color.parseColor("#C3D92E")
            }
            in 101..150 -> {
                levelText = "轻度污染"
                mLevelTextPaint.color = Color.parseColor("#5E844F")
            }
            in 151..200 -> {
                levelText = "中度污染"
                mLevelTextPaint.color = Color.parseColor("#7B6B39")
            }
            in 201..300 -> {
                levelText = "重度污染"
                mLevelTextPaint.color = Color.parseColor("#8A4D30")
            }
            in 301..500 -> {
                levelText = "严重污染"
                mLevelTextPaint.color = Color.parseColor("#6C2424")
            }
        }
        return levelText
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState())
        bundle.putInt(STATUS_VALUE, mCurrentPM25Value)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATUS))
            mCurrentPM25Value = state.getInt(STATUS_VALUE)
            return
        }
        super.onRestoreInstanceState(state)
    }

    fun setValue(value: Int) {
        val pm25Value = when {
            value <= 0 -> 0
            value >= 500 -> 500
            else -> value
        }
        val animator = ValueAnimator.ofInt(0, pm25Value)
        animator.addUpdateListener { animation ->
            mCurrentPM25Value = animation?.animatedValue as Int
            invalidate()
        }
        animator.duration = 800
        animator.interpolator = LinearInterpolator()
        animator.start()
    }
}