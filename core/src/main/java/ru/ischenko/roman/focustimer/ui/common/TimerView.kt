package ru.ischenko.roman.focustimer.ui.common

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import ru.ischenko.roman.focustimer.core.R
import java.lang.Math.*

/**
 * User: roman
 * Date: 21.11.17
 * Time: 19:08
 */
class TimerView@JvmOverloads constructor(context: Context, attrSet: AttributeSet? = null, defStyleAttr: Int = 0):
        View(context, attrSet, defStyleAttr) {

    companion object {
        const val CIRCLE_ANGLE: Float = 360F
        const val START_ANGLE: Float = -90f
        const val SHADOW_OFFSET: Float = 3f
        const val SHADOW_RADIUS_MEDIUM: Float = 3f
        const val SHADOW_RADIUS_LARGE: Float = 5f
        const val TIMER_PULSE_FACTOR: Float = 12f
        const val STATUS_DRAWABLE_FACTOR: Float = 20f
        const val DEFAULT_STATUS_STARTED = "Started"
        const val DEFAULT_STATUS_PAUSED = "Paused"
        const val DEFAULT_STATUS_STOPPED = "Stopped"
    }

    var onTimeViewListener: OnTimeViewListener? = null

    private val activePaint: Paint
	private val inactivePaint: Paint
	private val backgroundPaint: Paint
	private val markerPaint: Paint
	private val textPaint: Paint
	private val hintTextPaint: Paint
	private val statusPaint: Paint

    private val backgroundPadding: Float

	private val lineWidth: Int
	private val markerDiameter: Int

    private var isPaused: Boolean = false
    private val statusStarted: String
    private val statusPaused: String
    private val statusStopped: String
    private val statusDrawablePath = Path()

    private val drawRect = RectF()

	private var timerTotalSecondCount: Long = 0L
	private var timerSecondsPassed: Long = 0
	private var progressAngle: Float = 0f
    private var stepAngle: Float = 0f

    private var centerX = 0F
    private var centerY = 0F
    private var radius = 0F

    init {

        val typedArray : TypedArray = context.theme.obtainStyledAttributes(attrSet, R.styleable.TimerView, 0, 0)

		try {
			activePaint = createPaint(typedArray.getColor(R.styleable.TimerView_activeColor, 0))
            statusPaint = createPaint(typedArray.getColor(R.styleable.TimerView_activeColor, 0))
			inactivePaint = createPaint(typedArray.getColor(R.styleable.TimerView_inactiveColor, 0))
            markerPaint = createPaint(typedArray.getColor(R.styleable.TimerView_markerColor, 0))
            markerPaint.style = Paint.Style.FILL
            backgroundPaint = createPaint(typedArray.getColor(R.styleable.TimerView_backgroundColor, 0))
            backgroundPaint.style = Paint.Style.FILL

            backgroundPadding = typedArray.getDimension(R.styleable.TimerView_backgroundPadding, 0F)

            textPaint = createPaint(typedArray.getColor(R.styleable.TimerView_textColor, 0))
            textPaint.textSize = typedArray.getDimension(R.styleable.TimerView_timerFontSize, 0F)
            textPaint.style = Paint.Style.FILL
            textPaint.isFakeBoldText = true

            hintTextPaint = createPaint(typedArray.getColor(R.styleable.TimerView_statusColor, 0))
            hintTextPaint.textSize = typedArray.getDimension(R.styleable.TimerView_statusFontSize, 0F)
            hintTextPaint.style = Paint.Style.FILL
            hintTextPaint.isFakeBoldText = true

            statusStarted = typedArray.getString(R.styleable.TimerView_statusStarted) ?: DEFAULT_STATUS_STARTED
            statusPaused = typedArray.getString(R.styleable.TimerView_statusPaused) ?: DEFAULT_STATUS_PAUSED
            statusStopped = typedArray.getString(R.styleable.TimerView_statusStopped) ?: DEFAULT_STATUS_STOPPED

			lineWidth = typedArray.getDimensionPixelSize(R.styleable.TimerView_lineWidth, 0)
            activePaint.strokeWidth = lineWidth.toFloat()
            inactivePaint.strokeWidth = lineWidth.toFloat()
            statusPaint.strokeWidth = lineWidth.toFloat()

			markerDiameter = typedArray.getDimensionPixelSize(R.styleable.TimerView_markerDiameter, 0)
		} finally {
			typedArray.recycle()
		}

        setupShadow()
    }

    private fun setupShadow() {
        activePaint.setShadowLayer(SHADOW_RADIUS_MEDIUM, SHADOW_OFFSET, SHADOW_OFFSET, Color.GRAY)
        inactivePaint.setShadowLayer(SHADOW_RADIUS_MEDIUM, SHADOW_OFFSET, SHADOW_OFFSET, Color.GRAY)
        markerPaint.setShadowLayer(SHADOW_RADIUS_LARGE, SHADOW_OFFSET, SHADOW_OFFSET, Color.GRAY)
        setLayerType(LAYER_TYPE_SOFTWARE, activePaint)
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
        isPaused = false
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

    fun pauseTimer(paused: Boolean) {
        isPaused = paused
        postInvalidate()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()

        val savedState = SavedState(superState)
        savedState.savedStep = stepAngle
        savedState.progressAngle = progressAngle
        savedState.timerTotalSecondCount = timerTotalSecondCount
        savedState.timerSecondsPassed = timerSecondsPassed
        savedState.isPaused = isPaused

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
        isPaused = parcelable.isPaused
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        val w = width
        val h = height

        centerX = w / 2F
        centerY = h / 2F

        radius = if (h > w) w / 2F else h / 2F
        radius -= markerDiameter
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val progressAngleInRad = (START_ANGLE + progressAngle) / 180.0 * PI
        val markerX = centerX + radius * cos(progressAngleInRad)
        val markerY = centerY + radius * sin(progressAngleInRad)

        drawTimer(canvas, centerX, centerY, radius, markerX, markerY)

        drawTimerClock(canvas, centerX, centerY)

        drawStatus(canvas, centerX, centerY)
    }

    private fun drawTimer(canvas: Canvas, centerX: Float, centerY: Float, radius: Float, markerX: Double, markerY: Double) {

        // draw dial and background
        canvas.drawCircle(centerX, centerY, radius, inactivePaint)
        val padding = if (timerSecondsPassed % 2 == 0L) backgroundPadding
                      else backgroundPadding + backgroundPadding / TIMER_PULSE_FACTOR
        canvas.drawCircle(centerX, centerY, radius - padding, backgroundPaint)

        // draw progress
        drawRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        canvas.drawArc(drawRect, START_ANGLE, progressAngle, false, activePaint)

        // draw marker
        canvas.drawCircle(markerX.toFloat(), markerY.toFloat(), markerDiameter.toFloat(), markerPaint)
    }

    private fun drawTimerClock(canvas: Canvas, centerX: Float, centerY: Float) {

        val secondsLeft = timerTotalSecondCount - timerSecondsPassed
        val min = secondsLeft / 60
        val sec = secondsLeft % 60

        val time = (if (min > 9) min.toString() else "0$min") + ":" +
                   (if (sec > 9) sec.toString() else "0$sec")
        val textSize = textPaint.measureText(time)

        canvas.drawText(time, centerX - textSize / 2, centerY, textPaint)
    }

    private fun drawStatus(canvas: Canvas, centerX: Float, centerY: Float) {

        val hintText = if (timerTotalSecondCount == 0L) { statusStopped }
                       else if (timerTotalSecondCount != 0L && !isPaused) { statusStarted }
                       else { statusPaused }

        val hintSize = hintTextPaint.measureText(hintText)
        canvas.drawText(hintText, centerX - hintSize / 2, centerY + textPaint.textSize / 2, hintTextPaint)

        drawStatusDrawable(canvas)
    }

    private fun drawStatusDrawable(canvas: Canvas) {
        val step = radius / STATUS_DRAWABLE_FACTOR
        val startX = centerX - step
        val startY = centerY + radius / 2

        statusDrawablePath.reset()
        statusDrawablePath.moveTo(startX, startY)

        if (isPaused || timerSecondsPassed == 0L) {
            val doubleStep = step * 2
            statusDrawablePath.lineTo(startX, startY + doubleStep)
            statusDrawablePath.lineTo(startX + doubleStep, startY + step)
            statusDrawablePath.close()
        } else {
            val tripleStep = step * 3
            statusDrawablePath.lineTo(startX, startY + tripleStep)
            statusDrawablePath.moveTo(startX + tripleStep, startY)
            statusDrawablePath.lineTo(startX + tripleStep, startY + tripleStep)
        }

        canvas.drawPath(statusDrawablePath, statusPaint)
    }

    interface OnTimeViewListener {

        fun onComplete()
    }

    private class SavedState : BaseSavedState {

        var savedStep: Float = 0f
        var progressAngle: Float = 0f
        var timerTotalSecondCount: Long = 0L
        var timerSecondsPassed: Long = 0L
        var isPaused: Boolean = false

        constructor(superState: Parcelable?) : super(superState) {}

        private constructor(inParcel: Parcel) : super(inParcel) {
            savedStep = inParcel.readFloat()
            progressAngle = inParcel.readFloat()
            timerTotalSecondCount = inParcel.readLong()
            timerSecondsPassed = inParcel.readLong()
            isPaused = inParcel.readInt() != 0
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(savedStep)
            out.writeFloat(progressAngle)
            out.writeLong(timerTotalSecondCount)
            out.writeLong(timerSecondsPassed)
            out.writeInt( if (isPaused) 1 else 0 )
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