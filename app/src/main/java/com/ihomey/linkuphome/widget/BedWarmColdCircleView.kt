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


    private val mFirstPath: Path = Path()

    private val mSecondPath: Path = Path()

    private val mThirdPath: Path = Path()

    // Parameters
    private var mCx: Float = 0f
    private var mCy: Float = 0f
    private var mCurrentRadian: Float = 0f
    private var mPreRadian: Float = 0f

    private lateinit var logoBitmap: Bitmap
    private lateinit var arrowBitmap: Bitmap

    private var startTime: Long = 0
    private var endTime: Long = 0

    private var downX: Float = 0f
    private var downY: Float = 0f

    private var moveX: Float = 0f
    private var moveY: Float = 0f

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
            mTextColor = typedArray.getColor(R.styleable.BedWarmColdCircleView_mTextColor, Color.parseColor("#848484"))
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
        mValuePaint.isDither = true
        mValuePaint.style = Paint.Style.FILL
        mTextPaint.typeface =Typeface.createFromAsset(context.assets, "PingFang_Bold.ttf")
        mTextPaint.textSize =  mTextSize * 2f

        mValuePaint.isAntiAlias = true
        mValuePaint.textSize = mTextSize * 2.2f
        mValuePaint.color = Color.WHITE
        mValuePaint.style = Paint.Style.FILL//实心画笔
        mValuePaint.typeface = Typeface.createFromAsset(context.assets, "PingFang_Bold.ttf")
        mValuePaint.isDither = true

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.logo)
        logoBitmap = scaleBitmap(bitmap, resources.getDimension(R.dimen._114sdp) / bitmap.width)
        arrowBitmap = BitmapFactory.decodeResource(resources, R.mipmap.control_icon_arrow)
    }


    //按比例缩放
    fun scaleBitmap(origin: Bitmap, scale: Float): Bitmap {
        val width = origin.width
        val height = origin.height
        val matrix = Matrix()
        matrix.preScale(scale, scale)
        val newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false)
        if (newBM == origin) {
            return newBM
        }
        origin.recycle()
        return newBM
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mRectF.left = mCircleWidth / 2
        mRectF.right = width - mCircleWidth / 2
        mRectF.top = mCircleWidth / 2
        mRectF.bottom = height - mCircleWidth / 2

        var startAngle = -225f//经过试验，-90这个值就是12点方向的位置

        val lifeSweepAngle = 89f//圆弧弧度
        mCirclePaint.color = Color.parseColor("#FCC930")//设置画笔颜色
        canvas.drawArc(mRectF, startAngle, lifeSweepAngle, false, mCirclePaint)//这里就是真正绘制圆弧的地方，从12点方向开始顺时针绘制150度弧度的圆弧
        mFirstPath.addArc(mRectF,startAngle+27,lifeSweepAngle)
        canvas.drawTextOnPath("3000K",mFirstPath, 0f, 18f, mTextPaint)

        startAngle += lifeSweepAngle + 1.5f
        val communicateSweep = 89f
        mCirclePaint.color = Color.parseColor("#F8F28D")
        canvas.drawArc(mRectF, startAngle, communicateSweep, false, mCirclePaint)
        mSecondPath.addArc(mRectF,startAngle+27,lifeSweepAngle)
        canvas.drawTextOnPath("4000K",mSecondPath, 0f, 18f, mTextPaint)


        startAngle += communicateSweep + 1.5f
        val trafficSweep = 89f
        mCirclePaint.color = Color.parseColor("#FFFFFF")
        canvas.drawArc(mRectF, startAngle, trafficSweep, false, mCirclePaint)
        mThirdPath.addArc(mRectF,startAngle+27,lifeSweepAngle)
        canvas.drawTextOnPath("6500K",mThirdPath, 0f, 18f, mTextPaint)


        val colorTemperatureStr = "" + getColorTemperature() + "K"
        mValuePaint.getTextBounds(colorTemperatureStr, 0, colorTemperatureStr.length, mRect)
        canvas.drawText(colorTemperatureStr, mCx - mValuePaint.measureText(colorTemperatureStr)/2, height.toFloat() - mCircleWidth / 2 + mRect.height() / 2, mValuePaint)

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
        mCurrentTemperature = value
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
            MotionEvent.ACTION_DOWN -> {
                startTime = System.currentTimeMillis()
                downX = event.x//float DownX
                downY = event.y//float DownY
                moveX = 0f
                moveY = 0f
                mPreRadian = getRadian(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                moveX += Math.abs(event.x - downX)//X轴距离
                moveY += Math.abs(event.y - downY)//y轴距离
                downX = event.x
                downX = event.y
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
                    if ((mCurrentTemperature == 0 || mCurrentTemperature != getColorTemperature()) && mColorTemperatureValueListener != null) {
                        mCurrentTemperature = getColorTemperature()
                        mColorTemperatureValueListener?.onColorTemperatureValueChanged(mCurrentTemperature)
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                endTime = System.currentTimeMillis()
                val isClick = endTime - startTime <= 200 || (moveX <= 20 && moveY <= 20)
                if (isClick) mCurrentRadian = getRadian(event.x, event.y)
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
                if (isClick) {
                    if ((mCurrentTemperature == 0 || mCurrentTemperature != getColorTemperature()) && mColorTemperatureValueListener != null) {
                        mCurrentTemperature = getColorTemperature()
                        mColorTemperatureValueListener?.onColorTemperatureValueChanged(mCurrentTemperature)
                    }
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

    fun setColorTemperatureListener(mCircleTemperatureListener: DashboardView.ColorTemperatureListener?) {
        this.mColorTemperatureValueListener = mCircleTemperatureListener
    }
}