package com.rosalynbm.widget

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.rosalynbm.ButtonState
import com.rosalynbm.R
import timber.log.Timber
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var valueAnimator = ValueAnimator()
    private val ovalSpace = RectF()
    private var progress = 0f
    private var buttonText: String = ""
    private var bgColor: Int = Color.BLACK
    private var textColor: Int = Color.BLACK

    private var buttonState: ButtonState by Delegates.observable(ButtonState.COMPLETED) { p, old, new ->
    }

    companion object {
        const val TOTAL_PROGRESS = 100f
        const val PERCENTAGE_DIVIDER = 100
        const val ARC_FULL_ROTATION_DEGREE = 360
    }

    init {
        isClickable = true

        valueAnimator = AnimatorInflater.loadAnimator(context, R.animator.loading) as ValueAnimator
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.RESTART
        valueAnimator.addUpdateListener {
            progress = (it.animatedValue as Float)
            invalidate()
        }

        val attr = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.LoadingButton,
                0,
                0)

        with(attr) {
            bgColor = getColor(R.styleable.LoadingButton_backgroundColor, bgColor)
            textColor = getColor(R.styleable.LoadingButton_textColor, textColor)
        }

        attr.recycle()
    }

    fun setProgress(value: Int) {
        progress = value as Float
        invalidate()

        if (progress == TOTAL_PROGRESS) {
            buttonState = ButtonState.COMPLETED
        }
    }

    override fun performClick(): Boolean {
        super.performClick()

        when (buttonState) {
            ButtonState.COMPLETED -> {
                buttonState = ButtonState.LOADING
            }
            else -> {
                Timber.d("No action")
            }
        }
        return true
    }

    fun startAnimation() {
        valueAnimator.start()
    }

    //Animate, Measure, Layout, Draw -> cycle
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h

        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //set the color depending on the progress
        buttonTextPaint.color = resources.getColor(R.color.colorPrimary)
        buttonTextPaint.strokeWidth = 2F
        buttonTextPaint.textAlign = Paint.Align.CENTER
        canvas?.drawRect(20F, 30F, width.toFloat(), height.toFloat(), buttonTextPaint)

        when (buttonState) {
            ButtonState.LOADING -> {
                buttonTextPaint.color = resources.getColor(R.color.colorPrimaryDark)
                val widthProgressed = (progress / 100) * width
                canvas?.drawRect(20F, 30F, widthProgressed.toFloat(), height.toFloat(), buttonTextPaint)

                buttonText = resources.getString(R.string.button_loading)
                buttonTextPaint.color = Color.WHITE
                canvas?.drawText(buttonText, 450F, 110F, buttonTextPaint)
            }
            else -> {
                buttonTextPaint.color = Color.WHITE
                buttonTextPaint.textSize = resources.getDimension(R.dimen.large_text_size)
                buttonText = resources.getString(R.string.main_download_button)

                // Centering based on the height occupied by the text
                val bounds = Rect()
                buttonTextPaint.getTextBounds(buttonText, 0, buttonText.length, bounds)
                val verticalCenter = (height).minus((bounds.height() / 2)).toFloat()

                canvas?.drawText(buttonText, (width / 2).toFloat(), verticalCenter, buttonTextPaint)
            }
        }

        setSpace()
        canvas?.let {
            it.drawArc(ovalSpace, 0f, 360f, false, mainCirclePaint)
            drawBackgroundArc(it)
            drawInnerArc(it)
        }
    }

    private val buttonTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.medium_text_size)
        typeface = Typeface.create("", Typeface.ITALIC)
    }

    private fun setSpace() {
        val horizontalCenter = (width - width.div(6)).toFloat()
        val verticalCenter = (height.div(2)).toFloat()
        val ovalSize = 40
        // Bounds of oval used to define the shape and size of the arc
        ovalSpace.set(
                horizontalCenter - ovalSize,
                verticalCenter - ovalSize,
                horizontalCenter + ovalSize,
                verticalCenter + ovalSize
        )
    }

    private fun drawBackgroundArc(it: Canvas) {
        it.drawArc(ovalSpace, 0f, 360f, false, mainCirclePaint)
    }

    /**
     * Draws the progression
     */
    private fun drawInnerArc(canvas: Canvas) {
        val percentageToFill = getCurrentPercentageToFill()
        canvas.drawArc(ovalSpace, 270f, percentageToFill, true, fillArcPaint)
    }

    private val mainCircleColor = context.resources?.getColor(R.color.colorAccent, null)
            ?: Color.GRAY
    private val fillArcColor = resources.getColor(R.color.colorAccent, null)

    private val mainCirclePaint = Paint().apply {
        style = Paint.Style.STROKE
        // To ensure the drawing has smooth edge
        isAntiAlias = true
        color = mainCircleColor
        strokeWidth = 10f

    }

    private val fillArcPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        isAntiAlias = true
        color = fillArcColor
        strokeWidth = 10f
        // Makes the stroke edge round
        strokeCap = Paint.Cap.ROUND
    }

    private fun getCurrentPercentageToFill() =
            (ARC_FULL_ROTATION_DEGREE * (progress / PERCENTAGE_DIVIDER))

    fun downloadCompleted() {
        valueAnimator.cancel()
        requestLayout()
    }

    fun setButtonInitialState() {
        buttonState = ButtonState.COMPLETED
        invalidate()
    }

}
