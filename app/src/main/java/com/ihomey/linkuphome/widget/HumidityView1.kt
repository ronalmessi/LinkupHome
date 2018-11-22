package com.ihomey.linkuphome.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.graphics.RectF
import android.util.TypedValue
import com.umeng.analytics.pro.cx
import android.R.attr.centerY
import com.ihomey.linkuphome.R.attr.waveFillType
import com.umeng.analytics.pro.x.K
import com.ihomey.linkuphome.R.attr.waveSpeed
import android.graphics.Paint.ANTI_ALIAS_FLAG










class HumidityView1 : View {


    /**
     * 指针的颜色
     */
    private var pointerColor: Int = 0


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
    private var mAmplitude = 0f

    private var humidityValue = 60f


    /**
     * 初相
     */
    private var φ: Float = 0.toFloat()

    /**
     * 波形移动的速度
     */
    private var waveSpeed = 3f

    /**
     * 角速度
     */
    private var ω: Double = 0.toDouble()

    private var A: Int = 0
    /**
     * 偏距
     */
    private var K: Int=0


    /**
     * 开始位置相差多少个周期
     */
    private val startPeriod: Double = 0.toDouble()



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

        mBigWaterDropPaint.color = Color.parseColor("#FEFFFF")
        mBigWaterDropPaint.style = Paint.Style.FILL

        mFirstWavePaint.color = Color.parseColor("#96DAEE")
        mFirstWavePaint.style = Paint.Style.STROKE
        mSecondWavePaint.strokeWidth = 3f


//        mSecondWavePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
        mSecondWavePaint.color = Color.BLACK
        mSecondWavePaint.style = Paint.Style.STROKE
        mSecondWavePaint.strokeWidth = 3f


//        mThirdWavePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        mThirdWavePaint.color = Color.parseColor("#72BCD6")
        mThirdWavePaint.style = Paint.Style.STROKE
        mThirdWavePaint.strokeWidth = 3f


//        Log.d("aa","---"+waterWidth)
        mAmplitude = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
        mBorderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.5f, resources.displayMetrics)


        A=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()

        K = A




    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        mCenterPointF.x = (width / 2).toFloat()
        mCenterPointF.y = Math.min(height, width).toFloat() * 17.5f / 40
        mInnerRadius = Math.min(height, width).toFloat() / 4


        ω = 8 * Math.PI / width
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBigWaterDrop(canvas)

        drawSin(canvas,0f,0f, width.toFloat(),0f)
//        drawFirstWave(canvas)
//        drawSecondWave(canvas)
//        drawThirdWave(canvas)

    }


//    /**
//     * 根据sin函数绘制波形
//     *
//     * @param canvas
//     */
//    private fun drawSin(canvas: Canvas) {
//
//
//        val path = Path()
//
//        val A=30
//
//        φ -= waveSpeed / 100
//        var y: Float
//
//        path.reset()
//        path.moveTo(0f, 80f)
//
//        var x = 0f
//        while (x <= width) {
//            y = A * Math.sin(ω * x + φ + Math.PI * startPeriod) + K
//            path.lineTo(x, y)
//            x += 20f
//        }
//
//        //填充矩形
//        path.lineTo(width, height)
//        path.lineTo(0, height)
//        path.close()
//
//        canvas.drawPath(path, paint)
//
//    }


    /**
     * 填充波浪下面部分
     */
    private fun drawSin(canvas: Canvas,startX:Float,startY:Float,endX:Float,endY:Float) {
        val path = Path()
        φ -= waveSpeed / 100
        var y: Float

        path.reset()
        path.moveTo(startX, startY)

        var x = 0f
        while (x <= endX) {
            y = (A * Math.sin(ω * x + φ.toDouble() + Math.PI * startPeriod) + K).toFloat()
            path.lineTo(x, y)
            x += 20f
        }

//        //填充矩形
//        path.lineTo(width.toFloat(), height.toFloat())
//        path.lineTo(0f, height.toFloat())
//        path.close()

        canvas.drawPath(path, mFirstWavePaint)

    }

    private fun drawThirdWave(canvas: Canvas) {
        val waterHeight = (humidityValue + 20) / 100 * (mInnerRadius - mBorderWidth) * 2
        val waterY = mCenterPointF.y + (mInnerRadius - mBorderWidth) - waterHeight

        val waterWidth = Math.sqrt(((mInnerRadius - mBorderWidth) * (mInnerRadius - mBorderWidth) - (mInnerRadius - mBorderWidth - waterHeight) * (mInnerRadius - mBorderWidth - waterHeight)).toDouble()) * 2


        val path = Path()
        path.moveTo(mCenterPointF.x - mInnerRadius * 2, waterY)
        for (i in 1 until 9) {
            path.rQuadTo(mInnerRadius / 8f, mAmplitude, mInnerRadius / 4f, 0f)
            path.rQuadTo(mInnerRadius / 8f, -mAmplitude, mInnerRadius / 4f, 0f)
        }

        val ratio = waterWidth / (mInnerRadius * 4)
        val pathMeasure = PathMeasure(path, false)
        val dstPath = Path()
        pathMeasure.getSegment((pathMeasure.length * (1 - ratio) / 2).toFloat(), (pathMeasure.length * (1 + ratio) / 2).toFloat(), dstPath, true)


        val startPos = FloatArray(2)
        val endPos = FloatArray(2)
        pathMeasure.getPosTan((pathMeasure.length * (1 - ratio) / 2).toFloat(), startPos, null)
        pathMeasure.getPosTan((pathMeasure.length * (1 + ratio) / 2).toFloat(), endPos, null)

        mRectF.left = mCenterPointF.x - mInnerRadius + mBorderWidth
        mRectF.right = mCenterPointF.x + mInnerRadius - mBorderWidth
        mRectF.top = mCenterPointF.y - mInnerRadius + mBorderWidth
        mRectF.bottom = mCenterPointF.y + mInnerRadius - mBorderWidth

        Log.d("aa", "--" + mCenterPointF.x + "--" + mCenterPointF.y)

        Log.d("aa", "--" + getRadian(startPos[0], startPos[1]) * 180 / Math.PI)
        Log.d("aa", "--" + getRadian(endPos[0], endPos[1]) * 180 / Math.PI)


        val startAngel = getRadian(endPos[0], endPos[1]) * 180 / Math.PI - 90f
        val sweepAngel = getRadian(startPos[0], startPos[1]) * 180 / Math.PI - getRadian(endPos[0], endPos[1]) * 180 / Math.PI
        dstPath.addArc(mRectF, startAngel.toFloat(), sweepAngel.toFloat())

//        dstPath.addArc(mRectF, startAngel.toFloat(), sweepAngel.toFloat())
        canvas.drawPath(dstPath, mThirdWavePaint)
    }

    private fun drawSecondWave(canvas: Canvas) {
        val waterHeight = (humidityValue + 10)* (mInnerRadius - mBorderWidth) * 2 / 100
        val waterY = mCenterPointF.y + (mInnerRadius - mBorderWidth) - waterHeight

        val waterWidth = Math.sqrt(((mInnerRadius - mBorderWidth) * (mInnerRadius - mBorderWidth) - (mInnerRadius - mBorderWidth - waterHeight) * (mInnerRadius - mBorderWidth - waterHeight)).toDouble()) * 2

        val path = Path()
        path.moveTo(mCenterPointF.x - mInnerRadius * 2, waterY)
        for (i in 1 until 9) {
            path.rQuadTo(mInnerRadius / 8f, mAmplitude, mInnerRadius / 4f, 0f)
            path.rQuadTo(mInnerRadius / 8f, -mAmplitude, mInnerRadius / 4f, 0f)
        }

        val ratio = waterWidth / (mInnerRadius * 4)
        val pathMeasure = PathMeasure(path, false)
        val dstPath = Path()
        pathMeasure.getSegment((pathMeasure.length * (1 - ratio) / 2).toFloat(), (pathMeasure.length * (1 + ratio) / 2).toFloat(), dstPath, true)


        val startPos = FloatArray(2)
        val endPos = FloatArray(2)
        pathMeasure.getPosTan((pathMeasure.length * (1 - ratio) / 2).toFloat(), startPos, null)
        pathMeasure.getPosTan((pathMeasure.length * (1 + ratio) / 2).toFloat(), endPos, null)

        mRectF.left = mCenterPointF.x - mInnerRadius + mBorderWidth
        mRectF.right = mCenterPointF.x + mInnerRadius - mBorderWidth
        mRectF.top = mCenterPointF.y - mInnerRadius + mBorderWidth
        mRectF.bottom = mCenterPointF.y + mInnerRadius - mBorderWidth

        val startAngel = getRadian(endPos[0], endPos[1]) * 180 / Math.PI - 90f
        val sweepAngel = getRadian(startPos[0], startPos[1]) * 180 / Math.PI - getRadian(endPos[0], endPos[1]) * 180 / Math.PI

        dstPath.addArc(mRectF, 0f, 360f)

        canvas.drawPath(dstPath, mSecondWavePaint)
    }

    private fun drawFirstWave(canvas: Canvas) {

        val waterHeight = humidityValue / 100 * (mInnerRadius - mBorderWidth) * 2

        val waterY = mCenterPointF.y + (mInnerRadius - mBorderWidth) - waterHeight

        val waterWidth = Math.sqrt(((mInnerRadius - mBorderWidth) * (mInnerRadius - mBorderWidth) - (mInnerRadius - mBorderWidth - waterHeight) * (mInnerRadius - mBorderWidth - waterHeight)).toDouble()) * 2


        val path = Path()
        path.moveTo(mCenterPointF.x - mInnerRadius * 2, waterY)
        for (i in 1 until 9) {
            path.rQuadTo(mInnerRadius / 8f, mAmplitude, mInnerRadius / 4f, 0f)
            path.rQuadTo(mInnerRadius / 8f, -mAmplitude, mInnerRadius / 4f, 0f)
        }

        val ratio = waterWidth / (mInnerRadius * 4)
        val pathMeasure = PathMeasure(path, false)
        val dstPath = Path()
        pathMeasure.getSegment((pathMeasure.length * (1 - ratio) / 2).toFloat(), (pathMeasure.length * (1 + ratio) / 2).toFloat(), dstPath, true)


        val startPos = FloatArray(2)
        val endPos = FloatArray(2)
        pathMeasure.getPosTan((pathMeasure.length * (1 - ratio) / 2).toFloat(), startPos, null)
        pathMeasure.getPosTan((pathMeasure.length * (1 + ratio) / 2).toFloat(), endPos, null)

        mRectF.left = mCenterPointF.x - mInnerRadius + mBorderWidth
        mRectF.right = mCenterPointF.x + mInnerRadius - mBorderWidth
        mRectF.top = mCenterPointF.y - mInnerRadius + mBorderWidth
        mRectF.bottom = mCenterPointF.y + mInnerRadius - mBorderWidth

        Log.d("aa", "--" + mCenterPointF.x + "--" + mCenterPointF.y)

        Log.d("aa", "--" + getRadian(startPos[0], startPos[1]) * 180 / Math.PI)
        Log.d("aa", "--" + getRadian(endPos[0], endPos[1]) * 180 / Math.PI)


        val startAngel = getRadian(endPos[0], endPos[1]) * 180 / Math.PI - 90f
        val sweepAngel = getRadian(startPos[0], startPos[1]) * 180 / Math.PI - getRadian(endPos[0], endPos[1]) * 180 / Math.PI
//        dstPath.addArc(mRectF, startAngel.toFloat(), sweepAngel.toFloat())
//        dstPath.close()
//        dstPath.addArc(mRectF, startAngel.toFloat(), sweepAngel.toFloat())
        canvas.drawPath(dstPath, mFirstWavePaint)

    }

    // Use tri to cal radian
    private fun getRadian(x: Float, y: Float): Float {

        var alpha = Math.atan(((x - mCenterPointF.x) / (mCenterPointF.y - y)).toDouble()).toFloat()
        // Quadrant
        if (x > mCenterPointF.x && y > mCenterPointF.y) {
            // 2
            alpha += Math.PI.toFloat()
        } else if (x < mCenterPointF.x && y > mCenterPointF.y) {
            // 3
            alpha += Math.PI.toFloat()
        } else if (x < mCenterPointF.x && y < mCenterPointF.y) {
            // 4
            alpha = (2 * Math.PI + alpha).toFloat()
        }
        return alpha
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

}