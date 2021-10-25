package com.backbase.assignment.ui.custom


import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.itcomca.testfactum.R


class RatingView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(context, attrs, defStyleAttr){

    private var mBackPaint: Paint? = null
    private var mProgPaint: Paint? = null
    private var mTextPaint: Paint? = null
    private var mPercentPaint: Paint? = null
    private var mRectF : RectF? = null
    private var mColorArray : IntArray? = null

    private var mCenterTextSize : Float = 0F
    private var mTextPercentSize : Float = 0F
    private var mCenterTextColor : Int = 0

    private var mProgress = 0

    private var textBoundRect : Rect? = null

    private var startColor : Int = 0
    private var firstColor : Int = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingView)
        mBackPaint = Paint()

        // Brush
        mBackPaint!!.style = Paint.Style.STROKE
        mBackPaint!!.strokeCap = Paint.Cap.ROUND
        mBackPaint!!.isAntiAlias = true
        mBackPaint!!.isDither = true
        mBackPaint!!.strokeWidth = typedArray.getDimension(R.styleable.RatingView_backWidth, 10f)
        mBackPaint!!.color = typedArray.getColor(R.styleable.RatingView_backColor, Color.LTGRAY)

        // Pen
        mProgPaint = Paint()
        mProgPaint!!.style = Paint.Style.STROKE
        mProgPaint!!.strokeCap = Paint.Cap.ROUND
        mProgPaint!!.isAntiAlias = true
        mProgPaint!!.isDither = true
        mProgPaint!!.strokeWidth = typedArray.getDimension(R.styleable.RatingView_progWidth, 10f)
        mProgPaint!!.color = typedArray.getColor(R.styleable.RatingView_progColor, Color.BLUE)

        startColor  = typedArray.getColor(R.styleable.RatingView_progStartColor, -1)
        firstColor  = typedArray.getColor(R.styleable.RatingView_progFirstColor, -1)

        mCenterTextSize = typedArray.getFloat(R.styleable.RatingView_centerTextSize, 37F)
        mTextPercentSize = 20F
        mCenterTextColor = typedArray.getColor(R.styleable.RatingView_centerTextColor, Color.parseColor("#FFFFFF"))

        // Text
        mTextPaint = Paint()
        mTextPaint!!.style = Paint.Style.FILL
        mTextPaint!!.isAntiAlias = true
        mTextPaint!!.color = mCenterTextColor
        mTextPaint!!.textSize = mCenterTextSize

        // Percent
        mPercentPaint = Paint()
        mPercentPaint!!.style = Paint.Style.FILL
        mPercentPaint!!.isAntiAlias = true
        mPercentPaint!!.color = mCenterTextColor
        mPercentPaint!!.textSize = mTextPercentSize

        textBoundRect = Rect()

        if(startColor != -1 && firstColor != -1) mColorArray = intArrayOf(startColor, firstColor)
        else mColorArray = null


        mProgress = typedArray.getInteger(R.styleable.RatingView_progress, 0)

        typedArray.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewWide = measuredWidth - paddingLeft - paddingRight
        val viewHigh = measuredHeight - paddingTop - paddingBottom
        val mRectLength = ((if (viewWide > viewHigh) viewHigh else viewWide) -
                if (mBackPaint!!.strokeWidth > mProgPaint!!.strokeWidth) mBackPaint!!.strokeWidth else mProgPaint!!.strokeWidth).toInt()
        val mRectL = paddingLeft + (viewWide - mRectLength) / 2
        val mRectT = paddingTop + (viewHigh - mRectLength) / 2
        mRectF = RectF(mRectL.toFloat(), mRectT.toFloat(), (mRectL + mRectLength).toFloat(), (mRectT + mRectLength).toFloat())
    }

    fun getProgress() = mProgress

    fun setProgress(progress: Int){
        mProgress = progress
        invalidate()
    }

    fun setProgress(progress: Int, animTime: Long) {
        if (animTime <= 0) setProgress(progress) else {
            val animator = ValueAnimator.ofInt(mProgress, progress)
            animator.addUpdateListener { animation ->
                mProgress = animation.animatedValue as Int
                invalidate()
            }
            animator.interpolator = OvershootInterpolator()
            animator.duration = animTime
            animator.start()
        }
    }

    fun setBackWidth(width: Int) {
        mBackPaint!!.strokeWidth = width.toFloat()
        invalidate()
    }

    @SuppressLint("ResourceAsColor")
    fun setBackColor(@ColorRes color: Int) {
        mBackPaint!!.color = color
        invalidate()
    }

    fun setProgWidth(width: Int) {
        mProgPaint!!.strokeWidth = width.toFloat()
        invalidate()
    }

    fun setFirstColor(color: Int){
        firstColor = color
    }

    fun setStartColor(color: Int){
        startColor = color
    }

    @SuppressLint("ResourceAsColor")
    fun setProgColor(@ColorRes color: Int) {
        mProgPaint!!.color = color
        mProgPaint!!.shader = null
        invalidate()
    }

    fun setProgColor(@ColorRes startColor: Int, @ColorRes firstColor: Int) {
        mColorArray = intArrayOf(
            ContextCompat.getColor(context, startColor), ContextCompat.getColor(
                context, firstColor
            )
        )
        mProgPaint!!.shader = LinearGradient(
            0f, 0f, 0f,
            measuredWidth.toFloat(), mColorArray!!, null, Shader.TileMode.MIRROR
        )
        invalidate()
    }

    fun setProgColor(@ColorRes colorArray: IntArray?) {
        if (colorArray == null || colorArray.size < 2) return
        mColorArray = IntArray(colorArray.size)
        for (index in colorArray.indices) mColorArray!![index] = ContextCompat.getColor(
            context,
            colorArray[index]
        )
        mProgPaint!!.shader = LinearGradient(
            0f, 0f, 0f,
            measuredWidth.toFloat(), mColorArray!!, null, Shader.TileMode.MIRROR
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawArc(mRectF!!, 0f, 360f, false, mBackPaint!!)
        canvas.drawArc(mRectF!!, 275f, (360 * mProgress / 100).toFloat(), false, mProgPaint!!)

        val data  = mProgress.toString()
        mTextPaint?.getTextBounds(data, 0, data.length, textBoundRect)
        mTextPaint?.let {
            canvas.drawText(data,
                (width.div(2) - textBoundRect?.width()?.div(2)!!).toFloat()-5,
                (height.div(2) + textBoundRect?.height()?.div(2)!!).toFloat(), it
            )
        }
        mPercentPaint?.getTextBounds("%", 0, "%".length, textBoundRect)
        mPercentPaint?.let {
            canvas.drawText("%",
                (width.div(2)+25 - textBoundRect?.width()?.div(2)!!).toFloat(),
                (height.div(2)-6 + textBoundRect?.height()?.div(2)!!).toFloat(), it
            )
        }
    }
}