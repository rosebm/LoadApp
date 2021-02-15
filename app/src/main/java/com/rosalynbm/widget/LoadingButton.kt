package com.rosalynbm.widget

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import com.rosalynbm.ButtonState
import com.rosalynbm.R
import kotlinx.android.synthetic.main.content_main.view.*
import timber.log.Timber
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
 ) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var valueAnimator = ValueAnimator()
    private var progress = 0f
    private var buttonText: String = ""
    private var bgColor: Int = Color.BLACK
    private var textColor: Int = Color.BLACK

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) {
        p, old, new ->
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 36f
        typeface = Typeface.create("", Typeface.ITALIC)
    }

    init {
        isClickable = true

        valueAnimator = AnimatorInflater.loadAnimator(context, R.animator.loading) as ValueAnimator
        valueAnimator.addUpdateListener {
            progress = (it.animatedValue as Float)
            Log.d("ROS", "init progress $progress ")
            invalidate()

            if (progress == 100f) {
                buttonState = ButtonState.Completed

                Toast.makeText(context, "File downloaded!", Toast.LENGTH_LONG)
            }
        }

        val attr = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.LoadingButton,
                0,
                0
        )
        try {
            bgColor = R.color.colorPrimary
            textColor = R.color.colorPrimaryDark
        } finally {
            attr.recycle()
        }

    }

    override fun performClick(): Boolean {
        super.performClick()

        Log.d("ROS", "cliiiick")

        when (buttonState) {
            ButtonState.Completed -> {
                buttonState = ButtonState.Loading
            }

            else -> {
                Timber.d("ROS No action")
            }
        }
        valueAnimator.start()
        return true
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
        paint.color = resources.getColor(R.color.colorPrimary)
        paint.strokeWidth = 2F
        paint.textAlign = Paint.Align.CENTER
        canvas?.drawRect(20F,30F, width.toFloat(),height.toFloat(), paint)

        when (buttonState) {
            ButtonState.Loading -> {
                paint.color = resources.getColor(R.color.colorPrimaryDark)
                val widthProgressed = (progress / 100) * width
                canvas?.drawRect(20F,30F, widthProgressed.toFloat(), height.toFloat(), paint)

                buttonText = resources.getString(R.string.button_loading)
                paint.color = Color.WHITE
                canvas?.drawText(buttonText, 450F, 110F, paint)
            }
            else -> {
                paint.color = Color.WHITE
                paint.textSize = 60F
                buttonText = resources.getString(R.string.main_download_button)
                canvas?.drawText(buttonText, 450F, 110F, paint)
            }
        }
    }

    fun downloadCompleted() {
        valueAnimator.cancel()
        requestLayout()
    }

}