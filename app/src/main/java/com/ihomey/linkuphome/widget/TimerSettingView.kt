package com.ihomey.linkuphome.widget

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.dip2px

class TimerSettingView : View {

    // Dimension
    private var mHourWheelUnReachedWidth: Float = 0f
    private var mHourWheelReachedWidth: Float = 0f
    private var mMinuteLineWidth: Float = 0f
    private var mMinuteLineLength: Float = 0f
    private var mMinuteLongLineLength: Float = 0f
    private var mSliderRadius: Float = 0f
    private var mSliderInnerRadius: Float = 0f
    private var mCircleGap: Float = 0f
    private var mTextSize: Float = 16f

    // Color
    private var mReachedColor: Int = 0
    private var mUnReachedColor: Int = 0
    private var mSliderBgColor: Int = 0
    private var mSliderInnerBgColor: Int = 0


    // Paint
    private val mTextPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val mAMPMPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val mHourWheelUnReachedPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val mHourWheelReachedPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val mLinePaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val mReachedLinePaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val mSliderPaint: Paint = Paint(ANTI_ALIAS_FLAG)
    private val mSliderInnerPaint: Paint = Paint(ANTI_ALIAS_FLAG)

    // Parameters
    private var mCx: Float = 0f
    private var mCy: Float = 0f
    private var mMinuteCircleRadius: Float = 0f
    private var mHourCircleRadius: Float = 0f
    private var mCurrentMinuteRadian: Float = 0f
    private var mPreMinuteRadian: Float = 0f
    private var mCurrentHourRadian: Float = 0f
    private var mPreHourRadian: Float = 0f
    private var mInMinuteSliderButton: Boolean = false
    private var mInHourSliderButton: Boolean = false

    private val mHourRectF: RectF = RectF()

    private var mTimeChangeListener: TimeChangeListener? = null

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
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimerSettingView)
            mHourWheelUnReachedWidth = typedArray.getDimension(R.styleable.TimerSettingView_hour_unreached_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getContext().resources.displayMetrics))
            mHourWheelReachedWidth = typedArray.getDimension(R.styleable.TimerSettingView_hour_reached_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, getContext().resources.displayMetrics))
            mTextSize = typedArray.getDimension(R.styleable.TimerSettingView_mTextSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, getContext().resources.displayMetrics))
            mMinuteLineWidth = typedArray.getDimension(R.styleable.TimerSettingView_minute_line_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getContext().resources.displayMetrics))
            mMinuteLineLength = typedArray.getDimension(R.styleable.TimerSettingView_minute_line_length, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, context.resources.displayMetrics))
            mMinuteLongLineLength = typedArray.getDimension(R.styleable.TimerSettingView_minute_long_line_length, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 23f, context.resources.displayMetrics))
            mSliderRadius = typedArray.getDimension(R.styleable.TimerSettingView_slider_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15.5f, context.resources.displayMetrics))
            mSliderInnerRadius = typedArray.getDimension(R.styleable.TimerSettingView_slider_inner_radius, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.resources.displayMetrics))
            mCircleGap = typedArray.getDimension(R.styleable.TimerSettingView_circle_gap, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, context.resources.displayMetrics))
            mReachedColor = typedArray.getColor(R.styleable.TimerSettingView_reached_color, Color.BLUE)
            mUnReachedColor = typedArray.getColor(R.styleable.TimerSettingView_unreached_color, Color.WHITE)
            mSliderBgColor = typedArray.getColor(R.styleable.TimerSettingView_slider_bg_color, Color.BLUE)
            mSliderInnerBgColor = typedArray.getColor(R.styleable.TimerSettingView_slider_inner_bg_color, Color.WHITE)
            typedArray.recycle()
        }
        initElements()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        this.mCx = (width / 2).toFloat()
        this.mCy = (width / 2).toFloat()
        this.mMinuteCircleRadius = (width - mMinuteLongLineLength) / 2
        this.mHourCircleRadius = mMinuteCircleRadius - mCircleGap - mMinuteLongLineLength
        setMeasuredDimension(width, width)
    }


    override fun onDraw(canvas: Canvas) {
        canvas.save()
        drawMinuteDials(canvas)
        drawHourWheel(canvas)
        drawSlider(canvas)
        drawTimerText(canvas)
        canvas.restore()

        super.onDraw(canvas)
    }

    private fun drawTimerText(canvas: Canvas) {
        val rect = Rect()
        mTextPaint.getTextBounds("24", 0, 2, rect)

        val mAMPMRect = Rect()
        mAMPMPaint.getTextBounds(context.getString(R.string.title_am), 0, 2, mAMPMRect)

        var hour = getCurrentHour()
        val hourStr: String
        if(hour<=12){
            hourStr = if (hour < 10) "0$hour" else hour.toString()
            mAMPMPaint.color=mReachedColor
            canvas.drawText(context.getString(R.string.title_am), mCx- mAMPMPaint.measureText(context.getString(R.string.title_am)) - context.dip2px(4f), mCy + rect.height()+ mAMPMRect.height()*5/6, mAMPMPaint)
            mAMPMPaint.color=mUnReachedColor
            canvas.drawText(context.getString(R.string.title_pm), mCx + context.dip2px(4f), mCy + rect.height()+ mAMPMRect.height()*5/6, mAMPMPaint)
        }else{
            hour -= 12
            hourStr = if (hour < 10) "0$hour" else hour.toString()
            mAMPMPaint.color=mUnReachedColor
            canvas.drawText(context.getString(R.string.title_am), mCx- mAMPMPaint.measureText(context.getString(R.string.title_am)) - context.dip2px(4f), mCy + rect.height()+ mAMPMRect.height()*5/6, mAMPMPaint)
            mAMPMPaint.color=mReachedColor
            canvas.drawText(context.getString(R.string.title_pm), mCx + context.dip2px(4f), mCy + rect.height()+ mAMPMRect.height()*5/6, mAMPMPaint)
        }

        mTextPaint.getTextBounds(hourStr, 0, hourStr.length, rect)
        canvas.drawText(hourStr, mCx - mTextPaint.measureText(hourStr) - context.dip2px(6f), mCy+ rect.height()/4, mTextPaint)

        val minute = getCurrentMinute()
        val minuteStr = if (minute < 10) "0$minute" else minute.toString()
        mTextPaint.getTextBounds(minuteStr, 0, minuteStr.length, rect)
        canvas.drawText(minuteStr, mCx + context.dip2px(6f), mCy+ rect.height()/4 , mTextPaint)

        mTextPaint.getTextBounds(":", 0, 1, rect)
        canvas.drawText(":", mCx - mTextPaint.measureText(":") / 2, mCy+ rect.height()/4 , mTextPaint)
    }

    private fun drawHourWheel(canvas: Canvas) {
        canvas.save()
        canvas.drawCircle(mCx, mCy, mHourCircleRadius, mHourWheelUnReachedPaint)
        mHourRectF.left = mCx - mHourCircleRadius - mHourWheelUnReachedWidth / 2
        mHourRectF.top = mCy - mHourCircleRadius - mHourWheelUnReachedWidth / 2
        mHourRectF.right = mCx + mHourCircleRadius + mHourWheelUnReachedWidth / 2
        mHourRectF.bottom = mCy + mHourCircleRadius + mHourWheelUnReachedWidth / 2
        canvas.drawArc(mHourRectF, -90f, (mCurrentHourRadian * 180 / Math.PI).toFloat(), false, mHourWheelReachedPaint)
        canvas.restore()
    }


    private fun drawMinuteDials(canvas: Canvas) {
        for (i in 0..179) {
            canvas.save()
            canvas.rotate((360 / 180 * i).toFloat(), mCx, mCy)
            val isLongLine = i % 45 == 0
            val isReached = 360 / 180 * i <= Math.toDegrees(mCurrentMinuteRadian.toDouble())
            canvas.drawLine(mCx, measuredHeight / 2 - mMinuteCircleRadius, mCx, mCy - mMinuteCircleRadius + if (isLongLine) mMinuteLongLineLength else mMinuteLineLength, if (isReached) mReachedLinePaint else mLinePaint)
            canvas.restore()
        }
    }

    private fun drawSlider(canvas: Canvas) {
        canvas.save()
        canvas.rotate(Math.toDegrees(mCurrentMinuteRadian.toDouble()).toFloat(), mCx, mCy)
        canvas.drawCircle(mCx, mCy - mMinuteCircleRadius + (mMinuteLineLength / 2), mSliderRadius, mSliderPaint)
        canvas.drawCircle(mCx, mCy - mMinuteCircleRadius + (mMinuteLineLength / 2), mSliderInnerRadius, mSliderInnerPaint)
        canvas.restore()

        canvas.save()
        canvas.rotate(Math.toDegrees(mCurrentHourRadian.toDouble()).toFloat(), mCx, mCy)
        canvas.drawCircle(mCx, mCy - mHourCircleRadius, mSliderRadius, mSliderPaint)
        canvas.drawCircle(mCx, mCy - mHourCircleRadius, mSliderInnerRadius, mSliderInnerPaint)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and event.actionMasked) {
            MotionEvent.ACTION_DOWN -> updatePreParams(event)
            MotionEvent.ACTION_MOVE -> updateView(event)
            MotionEvent.ACTION_UP -> {
                mInMinuteSliderButton = false
                mInHourSliderButton = false
            }
        }
        return true
    }


    private fun updatePreParams(event: MotionEvent) {
        if (isInMinuteSliderButton(event.x, event.y) && isEnabled && getCurrentHour() < 24) {
            mInMinuteSliderButton = true
            mPreMinuteRadian = getRadian(event.x, event.y)
        } else if (isInHourSliderButton(event.x, event.y) && isEnabled) {
            mInHourSliderButton = true
            mPreHourRadian = getRadian(event.x, event.y)
        }
    }

    private fun updateView(event: MotionEvent) {
        if(mInMinuteSliderButton||mInHourSliderButton){
            val currentRadian = getRadian(event.x, event.y)
            var mPreRadian = if (mInMinuteSliderButton) mPreMinuteRadian else mPreHourRadian
            var mCurrentRadian = if (mInMinuteSliderButton) mCurrentMinuteRadian else mCurrentHourRadian
            if (mPreRadian > Math.toRadians(270.0) && currentRadian < Math.toRadians(90.0)) {
                mPreRadian -= (2 * Math.PI).toFloat()
            } else if (mPreRadian < Math.toRadians(90.0) && currentRadian > Math.toRadians(270.0)) {
                mPreRadian = (currentRadian + (currentRadian - 2 * Math.PI) - mPreRadian).toFloat()
            }
            mCurrentRadian += currentRadian - mPreRadian
            mPreRadian = currentRadian
            if (mCurrentRadian > 2 * Math.PI) {
                mCurrentRadian = (2 * Math.PI).toFloat()
            } else if (mCurrentRadian < 0) {
                mCurrentRadian = 0f
            }
            if (mInMinuteSliderButton) {
                mPreMinuteRadian = mPreRadian
                mCurrentMinuteRadian = mCurrentRadian
            } else {
                mPreHourRadian = mPreRadian
                mCurrentHourRadian = mCurrentRadian
                if (getCurrentHour() > 23) {
                    mPreMinuteRadian = 0f
                    mCurrentMinuteRadian = 0f
                }
            }
            invalidate()
        }
    }

    fun getCurrentHour(): Int {
        var hour = Math.ceil(mCurrentHourRadian / (2 * Math.PI) * 23).toInt()
        if (hour == 24) hour = 23
        return hour
    }

    fun getCurrentMinute(): Int {
        var minute = Math.ceil(mCurrentMinuteRadian / (2 * Math.PI) * 59).toInt()
        if (minute == 60) minute = 59
        return minute
    }


    private fun initElements() {
        setLinePaint()
        setReachedLinePaint()
        setSliderPaint()
        setSliderInnerPaint()
        setHourWheelUnReachedPaint()
        setHourWheelReachedPaint()
        setTimerTextPaint()
    }


    private fun setTimerTextPaint() {
        mTextPaint.color = mReachedColor
        mTextPaint.isAntiAlias = true
        mTextPaint.typeface = Typeface.MONOSPACE
        mTextPaint.textSize = mTextSize

        mAMPMPaint.color = mReachedColor
        mAMPMPaint.isAntiAlias = true
        mAMPMPaint.typeface = Typeface.MONOSPACE
        mAMPMPaint.textSize = mTextSize*5/12
    }

    private fun setHourWheelUnReachedPaint() {
        mHourWheelUnReachedPaint.color = mUnReachedColor
        mHourWheelUnReachedPaint.style = Paint.Style.STROKE
        mHourWheelUnReachedPaint.strokeWidth = mHourWheelUnReachedWidth
    }

    private fun setHourWheelReachedPaint() {
        mHourWheelReachedPaint.color = mReachedColor
        mHourWheelReachedPaint.style = Paint.Style.STROKE
        mHourWheelReachedPaint.strokeWidth = mHourWheelReachedWidth
        mHourWheelReachedPaint.strokeCap = Paint.Cap.ROUND
    }

    private fun setSliderInnerPaint() {
        mSliderInnerPaint.color = mReachedColor
        mSliderInnerPaint.isAntiAlias = true
        mSliderInnerPaint.style = Paint.Style.FILL
    }

    private fun setSliderPaint() {
        mSliderPaint.color = mSliderBgColor
        mSliderPaint.isAntiAlias = true
        mSliderPaint.style = Paint.Style.FILL
    }

    private fun setReachedLinePaint() {
        mReachedLinePaint.color = mReachedColor
        mReachedLinePaint.strokeWidth = mMinuteLineWidth
    }

    private fun setLinePaint() {
        mLinePaint.color = mUnReachedColor
        mLinePaint.strokeWidth = mMinuteLineWidth
    }

    private fun isInMinuteSliderButton(x: Float, y: Float): Boolean {
        val distance=Math.sqrt(((x - mCx) * (x - mCx) + (y - mCy) * (y - mCy)).toDouble())
        return distance<=mMinuteCircleRadius&& distance>= mMinuteCircleRadius - mMinuteLineLength
    }

    private fun isInHourSliderButton(x: Float, y: Float): Boolean {
        val distance=Math.sqrt(((x - mCx) * (x - mCx) + (y - mCy) * (y - mCy)).toDouble())
        return  distance<= mSliderRadius+mHourCircleRadius&& distance>=mHourCircleRadius- mSliderRadius
    }

    private fun getRadian(x: Float, y: Float): Float {
        var alpha = Math.atan(((x - mCx) / (mCy - y)).toDouble()).toFloat()
        if (x > mCx && y > mCy) {
            alpha += Math.PI.toFloat()
        } else if (x < mCx && y > mCy) {
            alpha += Math.PI.toFloat()
        } else if (x < mCx && y < mCy) {
            alpha = (2 * Math.PI + alpha).toFloat()
        }
        return alpha
    }


    fun setTimeChangeListener(listener: TimeChangeListener) {
        mTimeChangeListener = listener
    }

    interface TimeChangeListener {
        fun onTimerChange(hour: Int, minute: Int)
    }

    fun setTime(hour: Int, minute: Int) {
        if (getCurrentHour() != hour) mCurrentHourRadian = (hour.toFloat() / 24.0 * 2 * Math.PI).toFloat()
        if (getCurrentMinute() != minute) mCurrentMinuteRadian = (minute.toFloat() / 60.0 * 2 * Math.PI).toFloat()
        invalidate()
    }
}