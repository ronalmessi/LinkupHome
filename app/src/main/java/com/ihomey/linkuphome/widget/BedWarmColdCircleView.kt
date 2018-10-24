package com.ihomey.linkuphome.widget

import android.content.Context
import android.graphics.*
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.widget.dashboardview.DashboardView

class BedWarmColdCircleView : View {

    // Dimension
    private var mCircleWidth: Float = 0f
    private var mArrowGap: Float = 0f
    // Color
    private var mTextSize: Float = 0f

    // Color
    private var mTextColor: Int = 0

    // Paint
    private var mCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    // Paint
    private var mTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    // Paint
    private var mValuePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mRectF: RectF = RectF()

    private val mRect: Rect = Rect()

    // Parameters
    private var mCx: Float = 0f
    private var mCy: Float = 0f
    private var mCurrentRadian: Float = 0f
    private var mPreRadian: Float = 0f

    private lateinit var logoBitmap: Bitmap
    private lateinit var arrowBitmap: Bitmap

    // Color
    private var mCurrentTemperature: Int = 0


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
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BedWarmColdCircleView)
            mCircleWidth = typedArray.getDimension(R.styleable.BedWarmColdCircleView_circle_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getContext().resources.displayMetrics))
            mArrowGap = typedArray.getDimension(R.styleable.BedWarmColdCircleView_arrow_gap, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, getContext().resources.displayMetrics))
            mTextColor = typedArray.getColor(R.styleable.BedWarmColdCircleView_mTextColor, Color.parseColor("#959595"))
            mTextSize = typedArray.getDimension(R.styleable.BedWarmColdCircleView_text_size, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8.4f, getContext().resources.displayMetrics))
            typedArray.recycle()
        }
        initElements()
    }

    private fun initElements() {
        mCirclePaint.style = Paint.Style.STROKE
        mCirclePaint.strokeWidth = mCircleWidth

        mTextPaint.color = mTextColor
        mTextPaint.isAntiAlias = true
        mTextPaint.typeface = Typeface.MONOSPACE
        mTextPaint.textSize = mTextSize

        mValuePaint.isAntiAlias = true
        mValuePaint.textSize = mTextSize * 2
        mValuePaint.color = Color.WHITE
        mValuePaint.style = Paint.Style.FILL_AND_STROKE//实心画笔
        mValuePaint.typeface = Typeface.createFromAsset(context.assets, "VARSITY_REGULAR.TTF")
        mValuePaint.isDither = true
        logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_splash_logo)
        arrowBitmap = BitmapFactory.decodeResource(resources, R.mipmap.control_icon_arrow)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mRectF.left = mCircleWidth / 2
        mRectF.right = width - mCircleWidth / 2
        mRectF.top = mCircleWidth / 2
        mRectF.bottom = height - mCircleWidth / 2

        var startAngle = -225f//经过试验，-90这个值就是12点方向的位置

        val lifeSweepAngle = 89f//圆弧弧度
        mCirclePaint.color = Color.parseColor("#FEC244")//设置画笔颜色
        canvas.drawArc(mRectF, startAngle, lifeSweepAngle, false, mCirclePaint)//这里就是真正绘制圆弧的地方，从12点方向开始顺时针绘制150度弧度的圆弧

        startAngle += lifeSweepAngle + 1.5f
        val communicateSweep = 89f
        mCirclePaint.color = Color.parseColor("#F6F190")
        canvas.drawArc(mRectF, startAngle, communicateSweep, false, mCirclePaint)

        startAngle += communicateSweep + 1.5f
        val trafficSweep = 89f
        mCirclePaint.color = Color.parseColor("#FFFFFF")
        canvas.drawArc(mRectF, startAngle, trafficSweep, false, mCirclePaint)

        mTextPaint.getTextBounds("3000K", 0, 4, mRect)
        canvas.drawText("3000K", mCircleWidth / 2 - mTextPaint.measureText("3000K") / 2, mCy + mRect.height() / 2, mTextPaint)

        mTextPaint.getTextBounds("4000K", 0, 4, mRect)
        canvas.drawText("4000K", mCx - mTextPaint.measureText("4000K") / 2, mCircleWidth / 2 + mRect.height(), mTextPaint)

        mTextPaint.getTextBounds("6500K", 0, 4, mRect)
        canvas.drawText("6500K", width - mCircleWidth / 2 - mTextPaint.measureText("6500K") / 2, mCy + mRect.height() / 2, mTextPaint)

        val colorTemperatureStr = "" + getColorTemperature() + "K"
        mTextPaint.getTextBounds(colorTemperatureStr, 0, 4, mRect)
        canvas.drawText(colorTemperatureStr, mCx - mTextPaint.measureText(colorTemperatureStr), height.toFloat() - mCircleWidth / 2 + mRect.height() / 2, mValuePaint)

        canvas.drawBitmap(logoBitmap, (width / 2 - logoBitmap.width / 2).toFloat(), (height / 2 - logoBitmap.height / 2).toFloat(), mCirclePaint)
        canvas.save()
        canvas.rotate(Math.toDegrees(mCurrentRadian.toDouble()).toFloat(), mCx, mCy)
        canvas.drawBitmap(arrowBitmap, (width / 2 - arrowBitmap.width / 2).toFloat(), mCircleWidth + mArrowGap, mCirclePaint)
        canvas.restore()

    }


    private fun getColorTemperature(): Int {
        return if (mCurrentRadian <= (Math.PI / 4).toFloat() || mCurrentRadian > (3 * Math.PI / 4).toFloat()) {
            if ((mCurrentRadian <= (Math.PI / 4).toFloat() && mCurrentRadian >= 0f) || (mCurrentRadian <= (Math.PI * 2).toFloat() && mCurrentRadian >= (7 * Math.PI / 4).toFloat())) {
                4000
            } else if (mCurrentRadian < (Math.PI * 7 / 4).toFloat() && mCurrentRadian >= (5 * Math.PI / 4).toFloat()) {
                3000
            } else 4000
        } else {
            6500
        }
    }

    fun setColorTemperature(value: Int) {
        if (value == 4000) {
            mCurrentRadian = 0f
        } else if (value == 3000) {
            mCurrentRadian = (Math.PI * 3 / 2).toFloat()
        } else if (value == 6500) {
            mCurrentRadian = (Math.PI / 2).toFloat()
        }
        mCurrentTemperature=value
        invalidate()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        this.mCx = (width / 2).toFloat()
        this.mCy = (width / 2).toFloat()
        setMeasuredDimension(width, width)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and event.actionMasked) {
            MotionEvent.ACTION_DOWN -> mPreRadian = getRadian(event.x, event.y)
            MotionEvent.ACTION_MOVE -> {
                val temp = getRadian(event.x, event.y)
                if (Math.abs(temp - mPreRadian) > 0.01) {
                    mCurrentRadian += temp - mPreRadian
                    if (mCurrentRadian >= 2 * Math.PI) {
                        mCurrentRadian -= (2 * Math.PI).toFloat()
                    } else if (mCurrentRadian <= 0) {
                        mCurrentRadian += (2 * Math.PI).toFloat()
                    }
                    val isClockwise = temp - mPreRadian >= 0f
                    if (mCurrentRadian >= 3 * Math.PI / 4 && mCurrentRadian <= 5 * Math.PI / 4) {
                        mCurrentRadian = if (isClockwise) {
                            (3 * Math.PI / 4).toFloat()
                        } else {
                            (5 * Math.PI / 4).toFloat()
                        }
                    }
                    mPreRadian = temp
                    if ((mCurrentTemperature==0||mCurrentTemperature!=getColorTemperature())&&mColorTemperatureValueListener != null){
                        mCurrentTemperature=getColorTemperature()
                        mColorTemperatureValueListener?.onColorTemperatureValueChanged(mCurrentTemperature)
                    }

                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mCurrentRadian > (Math.PI / 4).toFloat() && mCurrentRadian <= (3 * Math.PI / 4).toFloat()) {
                    mCurrentRadian = (Math.PI / 2).toFloat()
                    invalidate()
                } else if ((mCurrentRadian <= (Math.PI / 4).toFloat() && mCurrentRadian >= 0f) || (mCurrentRadian <= (Math.PI * 2).toFloat() && mCurrentRadian >= (7 * Math.PI / 4).toFloat())) {
                    mCurrentRadian = 0f
                    invalidate()
                } else if (mCurrentRadian < (Math.PI * 7 / 4).toFloat() && mCurrentRadian >= (5 * Math.PI / 4).toFloat()) {
                    mCurrentRadian = (Math.PI * 3 / 2).toFloat()
                    invalidate()
                }
            }
        }
        return true
    }

    private fun getRadian(x: Float, y: Float): Float {
        var radian = Math.atan(((x - mCx) / (mCy - y)).toDouble()).toFloat()
        if (x > mCx && y > mCy) {
            radian += Math.PI.toFloat()
        } else if (x < mCx && y > mCy) {
            radian += Math.PI.toFloat()
        } else if (x < mCx && y < mCy) {
            radian = (2 * Math.PI + radian).toFloat()
        }
        return radian
    }

    private var mColorTemperatureValueListener: DashboardView.ColorTemperatureListener? = null

    fun setColorTemperatureListener(mCircleTemperatureListener:DashboardView.ColorTemperatureListener?) {
        this.mColorTemperatureValueListener = mCircleTemperatureListener
    }
}