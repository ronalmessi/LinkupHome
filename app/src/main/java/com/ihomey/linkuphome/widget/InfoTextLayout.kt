package com.ihomey.linkuphome.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.ihomey.linkuphome.R


class InfoTextLayout : RelativeLayout {

    private var leftIcon: Drawable? = null
    private var leftText: String? = null
    private var leftTextSize: Float = 16f
    private var horizontalMargin: Float = 0f
    private var leftTextMarginLeft: Float = 0f
    private var verticalMargin: Float = 0f
    private var leftTextColor: Int = Color.LTGRAY

    private var rightIcon: Drawable? = null
    private var rightText: String? = null
    private var rightTextSize: Float = 16f
    private var rightTextColor: Int = Color.LTGRAY

    private var isShowDivider = true

    private lateinit var mLeftTextView: TextView
    private lateinit var mRightTextView: TextView
    private lateinit var mRightImageView: ImageView


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
            val attributes = getContext().obtainStyledAttributes(attrs, R.styleable.InfoTextLayout)
            verticalMargin = attributes.getDimension(R.styleable.InfoTextLayout_verticalMargin, 0f)
            leftIcon = attributes.getDrawable(R.styleable.InfoTextLayout_leftIcon)
            leftText = attributes.getString(R.styleable.InfoTextLayout_leftText)
            leftTextSize = attributes.getDimension(R.styleable.InfoTextLayout_leftTextSize, 36.0f)
            horizontalMargin = attributes.getDimension(R.styleable.InfoTextLayout_horizontalMargin, 0f)
            leftTextMarginLeft = attributes.getDimension(R.styleable.InfoTextLayout_leftTextMarginLeft, 0f)
            leftTextColor = attributes.getColor(R.styleable.InfoTextLayout_leftTextColor, Color.LTGRAY)
            rightIcon = attributes.getDrawable(R.styleable.InfoTextLayout_rightIcon)
            rightText = attributes.getString(R.styleable.InfoTextLayout_rightText)
            rightTextSize = attributes.getDimension(R.styleable.InfoTextLayout_rightTextSize, 36.0f)
            rightTextColor = attributes.getColor(R.styleable.InfoTextLayout_rightTextColor, Color.LTGRAY)
            isShowDivider = attributes.getBoolean(R.styleable.InfoTextLayout_isShowDivider, true)
            attributes.recycle()
        }

        val mView = View.inflate(context, R.layout.layout_info_text, this)
        mLeftTextView = mView.findViewById(R.id.tv_info_left)
        mRightTextView = mView.findViewById(R.id.tv_info_right)
        mRightImageView = mView.findViewById(R.id.iv_info_right)
        val mDivider = mView.findViewById<View>(R.id.v_info_divider)

        mLeftTextView.text = leftText
        mLeftTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize)
        mLeftTextView.setTextColor(leftTextColor)
        if (leftIcon != null) mLeftTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(leftIcon, null, null, null)

        val leftLayoutParams = mLeftTextView.layoutParams as MarginLayoutParams
        leftLayoutParams.leftMargin = horizontalMargin.toInt() + leftTextMarginLeft.toInt()
        leftLayoutParams.topMargin = verticalMargin.toInt()
        leftLayoutParams.bottomMargin = verticalMargin.toInt()
        mLeftTextView.layoutParams = leftLayoutParams

        val dividerLayoutParams = mDivider.layoutParams as MarginLayoutParams
        dividerLayoutParams.leftMargin = horizontalMargin.toInt()
        mDivider.layoutParams = dividerLayoutParams

        val rightLayoutParams = mRightImageView.layoutParams as MarginLayoutParams
        rightLayoutParams.rightMargin = horizontalMargin.toInt()
        mRightImageView.layoutParams = rightLayoutParams

        mRightTextView.text = rightText
        mRightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize)
        mRightTextView.setTextColor(rightTextColor)

        if (rightIcon != null) {
            mRightImageView.setImageDrawable(rightIcon)
        }
//        else{
//            val rightTextViewLayoutParams = mRightTextView.layoutParams as MarginLayoutParams
//            rightTextViewLayoutParams.rightMargin = 0
////            mRightTextView.layoutParams = rightTextViewLayoutParams
//        }
        if (isShowDivider) mDivider.visibility = View.VISIBLE else mDivider.visibility = View.GONE
    }

    fun setTextValue(textValue: String) {
        mRightTextView.text = textValue
    }

    fun setImageValue(imageDrawable: Drawable) {
        imageDrawable.setBounds(0, 0, mRightTextView.lineHeight*2, mRightTextView.lineHeight*2)
        mRightTextView.setCompoundDrawables(null, null, imageDrawable, null)
    }

    fun setLeftImageValue(imageDrawable: Drawable) {
        mRightTextView.setCompoundDrawablesWithIntrinsicBounds(imageDrawable, null,null , null)
    }
}