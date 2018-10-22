package com.ihomey.linkuphome.widget

import android.content.Context
import android.graphics.*
import android.os.Bundle
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

    // Color
    private var mCircleColor: Int = 0

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

    // Runt
    private var mCircleValueListener: RGBCircleView.ColorValueListener? = null

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
        logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_splash_logo)
        arrowBitmap = BitmapFactory.decodeResource(resources, R.mipmap.control_icon_arrow)
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
                mPreRadian = getRadian(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                val temp = getRadian(event.x, event.y)
                mCurrentRadian += temp - mPreRadian
                mPreRadian = temp
                if (mCurrentRadian > 2 * Math.PI) {
                    mCurrentRadian -= (2 * Math.PI).toFloat()
                } else if (mCurrentRadian < 0) {
                    mCurrentRadian += (2 * Math.PI).toFloat()
                }
                if (mCircleValueListener != null)
                    mCircleValueListener?.onColorValueChange((mCurrentRadian * 360 / (2 * Math.PI * 15)).toInt())
                invalidate()
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

    fun setColorValue(value: Int) {
        mCurrentRadian = ((2 * Math.PI * 15) * value / 360).toFloat()
        invalidate()
    }

    fun setCurrentRadian(radian: Float) {
        mCurrentRadian = radian
        invalidate()
    }


    fun setColorValueListener(mCircleValueListener: RGBCircleView.ColorValueListener?) {
        this.mCircleValueListener = mCircleValueListener
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
}