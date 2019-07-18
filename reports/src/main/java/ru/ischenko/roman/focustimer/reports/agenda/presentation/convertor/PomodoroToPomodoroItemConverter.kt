package ru.ischenko.roman.focustimer.reports.agenda.presentation.convertor

import ru.ischenko.roman.focustimer.data.model.Pomodoro
import ru.ischenko.roman.focustimer.reports.agenda.presentation.model.PomodoroItem
import java.util.*
import java.util.concurrent.TimeUnit

class PomodoroToPomodoroItemConverter {

    fun convert(pomodoro: Pomodoro): PomodoroItem {
        with (pomodoro) {
            return PomodoroItem(minusMinutesFromDate(completeDate, pomodoroTime), pomodoroTime, task.goal)
        }
    }

    private fun minusMinutesFromDate(date: Date, minutes: Long): Date {
        return Date(date.time - TimeUnit.MINUTES.toMillis(minutes))
    }
}