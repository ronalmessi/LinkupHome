package com.ihomey.linkuphome.widget

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import com.ihomey.linkuphome.R

class HCHOView : View {


    // Status
    private val INSTANCE_STATUS = "instance_status"
    private val STATUS_VALUE = "status_value"

    // Dimension
//    private var mGap: Float = 0f
    private var mLineWidth: Float = 0f
    private var mLineLength: Float = 0f
    private var mValueTextSize: Float = 0f
    private var mUnitTextSize: Float = 0f
    private var mLevelTextSize: Float = 0f

    // Color
    private var mLineColor: Int = 0

    // Paint
    private val mLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mArcPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mReachedArcPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mValueTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mUnitTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mLevelTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    private var mRect: RectF = RectF()

    // Parameters
    private var mCx: Float = 0f
    private var mCy: Float = 0f
    private var mCurrentHCHOValue: Int = 0


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
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HCHOView)
            mValueTextSize = typedArray.getDimension(R.styleable.HCHOView_h_value_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 54f, getContext().resources.displayMetrics))
            mLevelTextSize = typedArray.getDimension(R.styleable.HCHOView_h_level_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18f, getContext().resources.displayMetrics))
            mUnitTextSize = typedArray.getDimension(R.styleable.HCHOView_h_unit_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, getContext().resources.displayMetrics))
            mLineWidth = typedArray.getDimension(R.styleable.HCHOView_line_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getContext().resources.displayMetrics))
            mLineLength = typedArray.getDimension(R.styleable.HCHOView_line_length, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, getContext().resources.displayMetrics))
            mLineColor = typedArray.getColor(R.styleable.HCHOView_line_color, Color.WHITE)
            typedArray.recycle()
        }
        initElements()
    }


    private fun initElements() {

        mValueTextPaint.textSize = mValueTextSize
        mValueTextPaint.color = mLineColor
        mValueTextPaint.style = Paint.Style.FILL
        mValueTextPaint.typeface = Typeface.DEFAULT_BOLD

        mUnitTextPaint.color = mLineColor
        mUnitTextPaint.textSize = mUnitTextSize
        mUnitTextPaint.style = Paint.Style.FILL
        mUnitTextPaint.isAntiAlias = true

        mLevelTextPaint.textSize = mLevelTextSize
        mLevelTextPaint.style = Paint.Style.FILL
        mLevelTextPaint.isAntiAlias = true
        mLevelTextPaint.typeface = Typeface.DEFAULT_BOLD

        mArcPaint.style = Paint.Style.STROKE
        mArcPaint.color = Color.WHITE
        mArcPaint.isAntiAlias = true

        mReachedArcPaint.style = Paint.Style.STROKE
        mReachedArcPaint.isAntiAlias = true

        mLinePaint.color = mLineColor
        mLinePaint.strokeWidth = mLineWidth
        mLinePaint.isAntiAlias = true
        mLinePaint.style = Paint.Style.FILL
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

        mRect.left = mCx * 12.7f / 96
        mRect.top = mCx * 12.7f / 96
        mRect.bottom = mCy * 2 - mCx * 12.7f / 96
        mRect.right = mCy * 2 - mCx * 12.7f / 96

        mArcPaint.strokeWidth = mCx / 12
        mReachedArcPaint.strokeWidth = mCx / 12
        canvas.drawArc(mRect, 135f, 270f, false, mArcPaint)

        val levelText = getLevel()
        canvas.drawCircle(mCx, mCy, mCx * 3 / 4, mLevelTextPaint)
        val sweepDegree = mCurrentHCHOValue * 270f / 10000f
        canvas.drawArc(mRect, 135f, sweepDegree, false, mReachedArcPaint)
        canvas.drawText(mCurrentHCHOValue.toString(), mCx - mValueTextPaint.measureText(mCurrentHCHOValue.toString()) / 2, mCy + getFontHeight(mValueTextPaint, mCurrentHCHOValue.toString()) / 5, mValueTextPaint)
        canvas.drawText("μg/m³", mCx - mUnitTextPaint.measureText("μg/m³") / 2, mCy + getFontHeight(mValueTextPaint, mCurrentHCHOValue.toString()) * 7 / 30 + getFontHeight(mUnitTextPaint, "μg/m³"), mUnitTextPaint)
        canvas.drawText(levelText, mCx - mLevelTextPaint.measureText(levelText) / 2, mCy * 15 / 8 + getFontHeight(mLevelTextPaint, levelText)*1.5f/ 7, mLevelTextPaint)
    }

    private fun drawScaleLine(canvas: Canvas) {
        for (i in 0..119) {
            canvas.save()
            canvas.rotate((360 / 120 * i).toFloat(), mCx, mCy)
            canvas.drawLine(mCx, 0f, mCx, mLineLength, mLinePaint)
            canvas.restore()
        }
    }

    private fun getFontHeight(paint: Paint, text: String): Float {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height().toFloat()
    }

    private fun getLevel(): String {
        var levelText = "正常"
        when (mCurrentHCHOValue) {
            in 0..1000 -> {
                levelText = "正常"
                mLevelTextPaint.color = Color.parseColor("#00A750")
                mReachedArcPaint.color = Color.parseColor("#00A750")
            }
            in 1001..2000 -> {
                levelText = "轻度污染"
                mLevelTextPaint.color = Color.parseColor("#5E844F")
                mReachedArcPaint.color = Color.parseColor("#5E844F")
            }
            in 2001..5000 -> {
                levelText = "重度污染"
                mLevelTextPaint.color = Color.parseColor("#8A4D30")
                mReachedArcPaint.color = Color.parseColor("#8A4D30")
            }
            in 5001..10000 -> {
                levelText = "严重污染"
                mLevelTextPaint.color = Color.parseColor("#6C2424")
                mReachedArcPaint.color = Color.parseColor("#6C2424")
            }
        }
        return levelText
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState())
        bundle.putInt(STATUS_VALUE, mCurrentHCHOValue)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATUS))
            mCurrentHCHOValue = state.getInt(STATUS_VALUE)
            return
        }
        super.onRestoreInstanceState(state)
    }

    fun setValue(value: Int) {
        mCurrentHCHOValue = when {
            value <= 0 -> 0
            value >= 10000 -> 10000
            else -> value
        }
        invalidate()
    }
}