package ru.ischenko.roman.focustimer.ui.common

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
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

    enum class State { RESET, STARTED, PAUSED }

    private var state: State = State.RESET

    private val activePaint: Paint
	private val inactivePaint: Paint
	private val markerPaint: Paint
	private val textPaint: Paint

	private val lineWidth: Int
	private val markerDiameter: Int

    private val drawRect = RectF()
    private val progressHandler = Handler()

	private var timerTotalSecondCount: Long = 0L
	private var timerSecondsPassed: Long = 0
	private var progressAngle: Float = 0f
    private var stepAngle: Float = 0f
    private var lastTimeStamp: Long = 0L

    private val progressUpdater = object : Runnable {
        override fun run() {
            if (progressAngle < CIRCLE_ANGLE) {
                timerSecondsPassed += 1
                progressAngle += stepAngle
                postInvalidate()

                progressHandler.postDelayed(this, TIMEOUT)
            }
        }
    }

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

    fun startTimer(timerMinutesCount: Int) {
        if (state != State.RESET) {
            stopTimer()
        }

        state = State.STARTED

        timerTotalSecondCount = (timerMinutesCount * 60).toLong()
        stepAngle = 360F / timerTotalSecondCount
        progressHandler.postDelayed(progressUpdater, TIMEOUT)
    }

    fun stopTimer() {
        reset()
        progressHandler.removeCallbacks(progressUpdater)
        postInvalidate()
    }

    fun resumeTimer() {
        lastTimeStamp = System.currentTimeMillis()
        state = State.STARTED
        progressHandler.postDelayed(progressUpdater, TIMEOUT)
    }

    fun pauseTimer() {
        state = State.PAUSED
        progressHandler.removeCallbacks(progressUpdater)
    }

    fun isRunning() = state == State.STARTED

    private fun reset() {
        state = State.RESET
        timerSecondsPassed = 0L
        timerTotalSecondCount = 0L
        progressAngle = 0f
        stepAngle = 0f
        lastTimeStamp = 0L
    }

    fun onForeground() {
        if (state == State.STARTED) {

            val secondsPassedInBackground = (System.currentTimeMillis() - lastTimeStamp) / 1000
            val angle = progressAngle + secondsPassedInBackground * stepAngle
            timerSecondsPassed += secondsPassedInBackground

            if (angle <= CIRCLE_ANGLE) {
                progressAngle = angle
                progressHandler.postDelayed(progressUpdater, TIMEOUT)
            } else {
                progressAngle = CIRCLE_ANGLE
            }

            lastTimeStamp = 0L
        }
    }

    fun onBackground() {
        if (state == State.STARTED) {
            lastTimeStamp = System.currentTimeMillis()
            progressHandler.removeCallbacks(progressUpdater)
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()

        progressHandler.removeCallbacks(progressUpdater)

        val savedState = SavedState(superState)
        savedState.savedStep = stepAngle
        savedState.progressAngle = progressAngle
        savedState.currentTimeMillis = lastTimeStamp
        savedState.timerTotalSecondCount = timerTotalSecondCount
        savedState.timerSecondsPassed = timerSecondsPassed
        savedState.state = state.ordinal

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
        lastTimeStamp = parcelable.currentTimeMillis
        timerTotalSecondCount = parcelable.timerTotalSecondCount
        timerSecondsPassed = parcelable.timerSecondsPassed
        state = State.values()[parcelable.state]

        if (state == State.STARTED) {
            restoreAngle()
        }
    }

    private fun restoreAngle() {
        progressAngle += (System.currentTimeMillis() - lastTimeStamp) / 1000 * stepAngle
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

    private class SavedState : View.BaseSavedState {

        var savedStep: Float = 0f
        var progressAngle: Float = 0f
        var currentTimeMillis: Long = 0L
        var timerTotalSecondCount: Long = 0L
        var timerSecondsPassed: Long = 0L
        var state: Int = State.RESET.ordinal

        constructor(superState: Parcelable) : super(superState) {}

        private constructor(inParcel: Parcel) : super(inParcel) {
            savedStep = inParcel.readFloat()
            progressAngle = inParcel.readFloat()
            currentTimeMillis = inParcel.readLong()
            timerTotalSecondCount = inParcel.readLong()
            timerSecondsPassed = inParcel.readLong()
            state = inParcel.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(savedStep)
            out.writeFloat(progressAngle)
            out.writeLong(currentTimeMillis)
            out.writeLong(timerTotalSecondCount)
            out.writeLong(timerSecondsPassed)
            out.writeInt(state)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(inParcel: Parcel): SavedState = SavedState(inParcel)
                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }
}