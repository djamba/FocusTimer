package ru.ischenko.roman.focustimer.ui.common

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import ru.ischenko.roman.focustimer.R
import java.lang.Math.*

/**
 * User: roman
 * Date: 21.11.17
 * Time: 19:08
 */
class TimerView: View {

    companion object {
        const val TIMEOUT: Long = 1000
        const val CIRCLE_ANGLE: Float = 360F
        const val START_ANGLE: Float = -90f
    }

    var onTimeViewListener: OnTimeViewListener? = null

    private val activePaint: Paint
	private val inactivePaint: Paint
	private val markerPaint: Paint
	private val textPaint: Paint

	private val lineWidth: Int
	private val markerDiameter: Int

    private val drawRect = RectF()

	private var timerTotalSecondCount: Long = 0L
	private var timerSecondsPassed: Long = 0
	private var progressAngle: Float = 0f
    private var stepAngle: Float = 0f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrSet: AttributeSet?) : this(context, attrSet, 0)

    constructor(context: Context, attrSet: AttributeSet?, defStyleAttr: Int) : super(context, attrSet, defStyleAttr) {

        val typedArray : TypedArray = context.theme.obtainStyledAttributes(attrSet, R.styleable.TimerView, 0, 0)

		try {
			activePaint = createPaint(typedArray.getColor(R.styleable.TimerView_activeColor, 0))
			inactivePaint = createPaint(typedArray.getColor(R.styleable.TimerView_inactiveColor, 0))
            markerPaint = createPaint(typedArray.getColor(R.styleable.TimerView_markerColor, 0))
            markerPaint.style = Paint.Style.FILL

            textPaint = createPaint(typedArray.getColor(R.styleable.TimerView_textColor, 0))
            textPaint.textSize = typedArray.getDimension(R.styleable.TimerView_timerFontSize, 0F)
            textPaint.style = Paint.Style.FILL

			lineWidth = typedArray.getDimensionPixelSize(R.styleable.TimerView_lineWidth, 0)
            activePaint.strokeWidth = lineWidth.toFloat()
            inactivePaint.strokeWidth = lineWidth.toFloat()

			markerDiameter = typedArray.getDimensionPixelSize(R.styleable.TimerView_markerDiameter, 0)
		} finally {
			typedArray.recycle()
		}
    }

    private fun createPaint(@ColorInt clr: Int): Paint {
        return Paint().apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
            color = clr
        }
    }

    fun startTimer(timerSecondsCount: Long) {
        stopTimer()
        timerTotalSecondCount = timerSecondsCount
        stepAngle = 360F / timerTotalSecondCount
    }

    fun stopTimer() {
        reset()
        postInvalidate()
    }

    private fun reset() {
        timerSecondsPassed = 0L
        timerTotalSecondCount = 0L
        progressAngle = 0f
        stepAngle = 0f
    }

    fun updateTime(timerSecondsPassed: Long) {
        if (progressAngle < CIRCLE_ANGLE) {
            progressAngle += stepAngle * (timerSecondsPassed - this.timerSecondsPassed)
            this.timerSecondsPassed = timerSecondsPassed
            postInvalidate()
        } else {
            onTimeViewListener?.onComplete()
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()

        val savedState = SavedState(superState)
        savedState.savedStep = stepAngle
        savedState.progressAngle = progressAngle
        savedState.timerTotalSecondCount = timerTotalSecondCount
        savedState.timerSecondsPassed = timerSecondsPassed

        return savedState
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        if (parcelable !is SavedState) {
            super.onRestoreInstanceState(parcelable)
            return
        }

        super.onRestoreInstanceState(parcelable.superState)

        stepAngle = parcelable.savedStep
        progressAngle = parcelable.progressAngle
        timerTotalSecondCount = parcelable.timerTotalSecondCount
        timerSecondsPassed = parcelable.timerSecondsPassed
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val height = height - (paddingTop + paddingBottom)
        val width = width - (paddingLeft + paddingRight)

        val cx = width / 2F + paddingLeft
        val cy = height / 2F + paddingTop

        var radius = if (height > width) width / 2 else height / 2
        radius -= markerDiameter

        val progressAngleInRad = (START_ANGLE + progressAngle) / 180.0 * PI
        val markerX = cx + radius * cos(progressAngleInRad)
        val markerY = cy + radius * sin(progressAngleInRad)

        canvas.drawCircle(cx, cy, radius.toFloat(), inactivePaint)

        drawRect.set(cx - radius, cy - radius, cx + radius, cy + radius)
        canvas.drawArc(drawRect, START_ANGLE, progressAngle, false, activePaint)

        canvas.drawCircle(markerX.toFloat(), markerY.toFloat(), markerDiameter.toFloat(), markerPaint)

        val secondsLeft = timerTotalSecondCount - timerSecondsPassed
        val min = secondsLeft / 60
        val sec = secondsLeft % 60
        val time = (if (min > 9) min.toString() else "0$min") + ":" +
                   (if (sec > 9) sec.toString() else "0$sec")
        val textSize = textPaint.measureText(time)
        canvas.drawText(time, cx - textSize / 2, cy, textPaint)
    }

    interface OnTimeViewListener {

        fun onComplete()
    }

    private class SavedState : View.BaseSavedState {

        var savedStep: Float = 0f
        var progressAngle: Float = 0f
        var timerTotalSecondCount: Long = 0L
        var timerSecondsPassed: Long = 0L

        constructor(superState: Parcelable?) : super(superState) {}

        private constructor(inParcel: Parcel) : super(inParcel) {
            savedStep = inParcel.readFloat()
            progressAngle = inParcel.readFloat()
            timerTotalSecondCount = inParcel.readLong()
            timerSecondsPassed = inParcel.readLong()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(savedStep)
            out.writeFloat(progressAngle)
            out.writeLong(timerTotalSecondCount)
            out.writeLong(timerSecondsPassed)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(inParcel: Parcel): SavedState = SavedState(inParcel)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }
}