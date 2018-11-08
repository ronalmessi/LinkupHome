package com.ihomey.linkuphome.widget

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.ihomey.linkuphome.R

class VOCView : View {


    // Status
    private val INSTANCE_STATUS = "instance_status"
    private val STATUS_VALUE = "status_value"

    // Dimension
    private var mValueTextSize: Float = 0f
    private var mLevelTextSize: Float = 0f


    // Paint
    private val mValueTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mLevelTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Parameters
    private var mCx: Float = 0f
    private var mCy: Float = 0f
    private var mCurrentVocValue: Int = 1


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
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.VOCView)
            mValueTextSize = typedArray.getDimension(R.styleable.VOCView_value_text_size, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 54f, getContext().resources.displayMetrics))
            mLevelTextSize = typedArray.getDimension(R.styleable.VOCView_level_text_size, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, getContext().resources.displayMetrics))
            typedArray.recycle()
        }
        initElements()
    }


    private fun initElements() {

        mValueTextPaint.textSize = mValueTextSize
        mValueTextPaint.style = Paint.Style.FILL
        mValueTextPaint.typeface = Typeface.DEFAULT_BOLD

        mLevelTextPaint.textSize = mLevelTextSize
        mLevelTextPaint.style = Paint.Style.FILL
        mLevelTextPaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        this.mCx = (width / 2).toFloat()
        this.mCy = (width / 2).toFloat()
        setMeasuredDimension(width, width)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val levelText = getLevel()
        canvas.drawText(mCurrentVocValue.toString(), mCx- mValueTextPaint.measureText(mCurrentVocValue.toString()) / 2, mCy, mValueTextPaint)
        canvas.drawText(levelText, mCx - mLevelTextPaint.measureText(levelText) / 2, mCy+getFontHeight(mValueTextPaint, mCurrentVocValue.toString())*1.5f/5, mLevelTextPaint)
    }

    private fun getFontHeight(paint: Paint, text: String): Float {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height().toFloat()
    }

    private fun getLevel(): String {
        var levelText = "洁净空气"
        if (mCurrentVocValue == 0) {
            levelText = "洁净空气"
            mValueTextPaint.color = Color.parseColor("#00A750")
            mLevelTextPaint.color = Color.parseColor("#00A750")
        } else if (mCurrentVocValue == 1) {
            levelText = "轻微污染"
            mValueTextPaint.color = Color.parseColor("#5E844F")
            mLevelTextPaint.color = Color.parseColor("#5E844F")
        } else if (mCurrentVocValue == 2) {
            levelText = "中度污染"
            mValueTextPaint.color = Color.parseColor("#8A4D30")
            mLevelTextPaint.color = Color.parseColor("#8A4D30")
        } else if (mCurrentVocValue == 3) {
            levelText = "重度污染"
            mValueTextPaint.color = Color.parseColor("#6C2424")
            mLevelTextPaint.color = Color.parseColor("#6C2424")
        }
        return levelText
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState())
        bundle.putInt(STATUS_VALUE, mCurrentVocValue)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATUS))
            mCurrentVocValue = state.getInt(STATUS_VALUE)
            return
        }
        super.onRestoreInstanceState(state)
    }

    fun setValue(value: Int) {
        when {
            value <= 0 -> this.mCurrentVocValue = 0
            value >= 3 -> this.mCurrentVocValue = 3
            else -> this.mCurrentVocValue = value
        }
        invalidate()
    }
}