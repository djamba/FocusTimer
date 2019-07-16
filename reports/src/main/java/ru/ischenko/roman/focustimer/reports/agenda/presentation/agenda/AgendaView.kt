package ru.ischenko.roman.focustimer.reports.agenda.presentation.agenda

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import ru.ischenko.roman.focustimer.reports.R
import java.util.*

/**
 * User: roman
 * Date: 14.07.19
 * Time: 18:36
 */
class AgendaView@JvmOverloads constructor(context: Context, attrSet: AttributeSet? = null, defStyleAttr: Int = 0):
        FrameLayout(context, attrSet, defStyleAttr) {

    companion object {
        private const val START_MINUTES_GAP = 30
    }

    private val hoursContainer: LinearLayout
    private val pomodoroContainer: FrameLayout

    private val minuteSizeDp: Float
    private val calendar = GregorianCalendar.getInstance()

    private var pomodoroItems: List<PomodoroItem>? = null

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    init {
        layoutInflater.inflate(R.layout.view_agenda, this, true)
        hoursContainer = findViewById(R.id.agenda_hours_container)
        pomodoroContainer = findViewById(R.id.agenda_pomodoro_container)

        val itemHeightDp = resources.getDimension(R.dimen.view_agenda_item_height)
        minuteSizeDp = itemHeightDp / 60

        fillHoursContainer()
    }

    private fun fillHoursContainer() {
        for (hour in 0..23) {
            val hourItem = layoutInflater.inflate(R.layout.view_agenda_hour_item, hoursContainer, false)
            val timeTextView = hourItem.findViewById<TextView>(R.id.hour_item_time_text_view)
            timeTextView.text = "$hour:00"
            hoursContainer.addView(hourItem)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val pomodoros = pomodoroItems ?: return

        pomodoroContainer.removeAllViews()
        inflatePomodorosItems(pomodoros)
        pomodoroItems = null
    }

    private fun inflatePomodorosItems(pomodoros: List<PomodoroItem>) {

        for (pomodoro in pomodoros) {

            val pomodoroItem = layoutInflater.inflate(R.layout.view_agenda_pomodoro_item, pomodoroContainer, false)
            val pomodoroTitleTextView = pomodoroItem.findViewById<TextView>(R.id.pomodoro_item_title)
            pomodoroTitleTextView.text = pomodoro.goal

            calendar.time = pomodoro.startDate
            val timeStartPomodoroInMinutes = (calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE) + START_MINUTES_GAP)
            val topMargin = minuteSizeDp * timeStartPomodoroInMinutes
            (pomodoroItem.layoutParams as LayoutParams).topMargin = topMargin.toInt()

            pomodoroContainer.addView(pomodoroItem)
        }
    }

    fun setPomodoros(pomodoros: List<PomodoroItem>) {
        this.pomodoroItems = pomodoros
    }
}