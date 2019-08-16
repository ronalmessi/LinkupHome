package com.ihomey.linkuphome.widget

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import com.ihomey.linkuphome.R



class BedRGBCircleView : View {

    private val colors = intArrayOf(R.color.c2, R.color.c3, R.color.c4, R.color.c5, R.color.c6, R.color.c7, R.color.c8, R.color.c9, R.color.c10, R.color.c11, R.color.c12, R.color.c13, R.color.c14, R.color.c15, R.color.c16, R.color.c17, R.color.c18, R.color.c19, R.color.c20, R.color.c21, R.color.c22, R.color.c23, R.color.c24, R.color.c1)

    // Status
    private val INSTANCE_STATUS = "instance_status"
    private val STATUS_RADIAN = "status_radian"
    // Dimension
    private var mCircleWidth: Float = 0f
    private var mArrowGap: Float = 0f


    // Paint
    private var mCirclePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mRectF: RectF = RectF()

    // Parameters
    private var mCx: Float = 0f
    private var mCy: Float = 0f
    private var mCurrentRadian: Float = 0f
    private var mPreRadian: Float = 0f

    private lateinit var logoBitmap: Bitmap
    private lateinit var arrowBitmap: Bitmap

    private var mCurrentValue: Int = 0 // seconds

    // Runt
    private var mCircleValueListener: RGBCircleView.ColorValueListener? = null



    private var downX: Float = 0f
    private var downY: Float = 0f

    private var moveX: Float = 0f
    private var moveY: Float = 0f

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
            typedArray.recycle()
        }
        initElements()
    }

    private fun initElements() {
        mCirclePaint.style = Paint.Style.STROKE
        mCirclePaint.strokeWidth = mCircleWidth

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.logo)
        logoBitmap = scaleBitmap(bitmap, resources.getDimension(R.dimen._100sdp) / bitmap.width)

        arrowBitmap = BitmapFactory.decodeResource(resources, R.mipmap.control_icon_arrow)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(ev)
    }


    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        mRectF.left = mCircleWidth / 2
        mRectF.right = width - mCircleWidth / 2
        mRectF.top = mCircleWidth / 2
        mRectF.bottom = height - mCircleWidth / 2

        for (i in 0..23) {
            mCirclePaint.color = resources.getColor(colors[i])//设置画笔颜色
            canvas.drawArc(mRectF, -90f + 15f * i, 14f, false, mCirclePaint)//这里就是真正绘制圆弧的地方，从12点方向开始顺时针绘制150度弧度的圆弧
        }

        canvas.drawBitmap(logoBitmap, (width / 2 - logoBitmap.width / 2).toFloat(), (height / 2 - logoBitmap.height / 2).toFloat(), mCirclePaint)
        canvas.save()
        canvas.rotate(Math.toDegrees(mCurrentRadian.toDouble()).toFloat(), mCx, mCy)
        canvas.drawBitmap(arrowBitmap, (width / 2 - arrowBitmap.width / 2).toFloat(), mCircleWidth + mArrowGap, mCirclePaint)
        canvas.restore()
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
                Log.d("aa","ACTION_DOWN")
                downX = event.x//float DownX
                downY = event.y//float DownY
                moveX = 0f
                moveY = 0f
                mPreRadian = getRadian(event.x, event.y)
                handler.postDelayed(runnable, 200)
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d("aa","ACTION_MOVE")
                moveX += Math.abs(event.x - downX)//X轴距离
                moveY += Math.abs(event.y - downY)//y轴距离
                downX = event.x
                downX = event.y
                val temp = getRadian(event.x, event.y)
                mCurrentRadian += temp - mPreRadian
                mPreRadian = temp
                if (mCurrentRadian > 2 * Math.PI) {
                    mCurrentRadian -= (2 * Math.PI).toFloat()
                } else if (mCurrentRadian < 0) {
                    mCurrentRadian += (2 * Math.PI).toFloat()
                }
                mCurrentValue = getCurrentValue()
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                handler.removeCallbacks(runnable)
                mCurrentRadian = getRadian(event.x, event.y)
                if (mCurrentValue != getCurrentValue()) {
                    if (mCircleValueListener != null) {
                        mCircleValueListener?.onColorValueChanged(getCurrentValue())
                    }
                }
            }
        }
        return true
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATUS, super.onSaveInstanceState())
        bundle.putFloat(STATUS_RADIAN, mCurrentRadian)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATUS))
            mCurrentRadian = state.getFloat(STATUS_RADIAN)
            return
        }
        super.onRestoreInstanceState(state)
    }


    fun setCurrentRadian(radian: Float) {
        mCurrentRadian = radian
        invalidate()
    }

    // Use tri to cal radian
    private fun getRadian(x: Float, y: Float): Float {
        var alpha = Math.atan(((x - mCx) / (mCy - y)).toDouble()).toFloat()
        // Quadrant
        if (x > mCx && y > mCy) {
            // 2
            alpha += Math.PI.toFloat()
        } else if (x < mCx && y > mCy) {
            // 3
            alpha += Math.PI.toFloat()
        } else if (x < mCx && y < mCy) {
            // 4
            alpha = (2 * Math.PI + alpha).toFloat()
        }
        return alpha
    }


    /**
     * set timer listener
     *
     * @param mCircleTimerListener
     */
    fun setColorValueListener(mCircleTimerListener: RGBCircleView.ColorValueListener?) {
        this.mCircleValueListener = mCircleTimerListener
    }


    private fun getCurrentValue(): Int {
        return (60 / (2 * Math.PI) * mCurrentRadian.toDouble() * 60.0).toInt() / 151
    }


    internal var handler = Handler()
    private var runnable: Runnable = object : Runnable {
        override fun run() {
            if (mCircleValueListener != null) {
                mCircleValueListener?.onColorValueChange(mCurrentValue)
            }
            handler.postDelayed(this, 200)
        }
    }

}