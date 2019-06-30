package ru.ischenko.roman.focustimer.ui.common

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import ru.ischenko.roman.focustimer.core.R

/**
 * User: roman
 * Date: 12.06.19
 * Time: 22:00
 */
class PomodoroCounter @JvmOverloads constructor(context: Context, attrSet: AttributeSet? = null, defStyleAttr: Int = 0):
        View(context, attrSet, defStyleAttr) {

    companion object {
        const val TIMEOUT: Long = 1000
        const val GRAVITY_LEFT: Int = 0
        const val GRAVITY_RIGHT: Int = 1
    }

    var plannedPomodoros: Int = 4
        set(value) {
            if (totalPomodoros == 0) {
                totalPomodoros = value
            }
            field = value
        }

    var currentPomodoroPass: Int = 1
        set(value) {
            if (value == 0) {
                totalPomodoros = plannedPomodoros
            }
            field = value
            postInvalidate()
        }

    var isTimerRunning: Boolean = false
        set(isRunning) {
            if (isRunning) {
                totalPomodoros = if (currentPomodoroPass < plannedPomodoros) {
                    plannedPomodoros
                } else {
                    currentPomodoroPass + 1
                }
                progressHandler.postDelayed(progressUpdater, TIMEOUT)
            } else {
                pendingPomodoroColor = plannedPomodoroPaint
                progressHandler.removeCallbacks(progressUpdater)
            }
            field = isRunning
            postInvalidate()
        }

    private var radius = 0F
    private var padding = 0F
    private val gravity: Int

    private var totalPomodoros: Int = 0
    private var tickTack: Boolean = false

    private lateinit var passedPomodoroPaint: Paint
    private lateinit var plannedPomodoroPaint: Paint
    private lateinit var overPlannedPomodoroPaint: Paint
    private var pendingPomodoroColor: Paint

    private val progressHandler = Handler()
    private val progressUpdater = object : Runnable {
        override fun run() {
            if (isTimerRunning) {
                pendingPomodoroColor = if (tickTack) {
                    if (plannedPomodoros >= totalPomodoros) {
                        passedPomodoroPaint
                    } else {
                        overPlannedPomodoroPaint
                    }
                } else {
                    plannedPomodoroPaint
                }
                tickTack = !tickTack
                progressHandler.postDelayed(this, TIMEOUT)
            }
            postInvalidate()
        }
    }

    init {
        val typedArray : TypedArray = context.theme.obtainStyledAttributes(attrSet, R.styleable.PomodoroCounter, 0, 0)

        try {
            passedPomodoroPaint = createPaint(typedArray.getColor(R.styleable.PomodoroCounter_passedColor, 0))
            plannedPomodoroPaint = createPaint(typedArray.getColor(R.styleable.PomodoroCounter_plannedColor, 0))
            overPlannedPomodoroPaint = createPaint(typedArray.getColor(R.styleable.PomodoroCounter_overPlannedColor, 0))
            pendingPomodoroColor = plannedPomodoroPaint
            gravity = typedArray.getInt(R.styleable.PomodoroCounter_gravity, GRAVITY_LEFT)
        } finally {
            typedArray.recycle()
        }
    }

    private fun createPaint(@ColorInt clr: Int): Paint {
        return Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = clr
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        val w = width
        val h = height
        radius = if (h > w) w / 2F else h / 2F
        padding = radius / 5
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (i in 0 until totalPomodoros) {

            val counterPaint = when {
                i == currentPomodoroPass -> pendingPomodoroColor
                i >= plannedPomodoros -> overPlannedPomodoroPaint
                i < currentPomodoroPass -> passedPomodoroPaint
                else -> plannedPomodoroPaint
            }

            if (gravity == GRAVITY_LEFT) {
                canvas?.drawCircle(radius + (radius * 2 * i + padding * i), radius, radius, counterPaint)
            } else {
                canvas?.drawCircle(measuredWidth - (radius + (radius * 2 * i + padding * i)), radius, radius, counterPaint)
            }
        }
    }
}