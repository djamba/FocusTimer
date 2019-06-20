package ru.ischenko.roman.focustimer.ui.common.adapters

import androidx.databinding.BindingAdapter
import ru.ischenko.roman.focustimer.ui.common.PomodoroCounter

object PomodoroCounterBindingAdapters {

    @JvmStatic
    @BindingAdapter("timerRunning")
    fun updateTimeBindingAdapter(timerView: PomodoroCounter, isTimerRunning: Boolean) {
        timerView.isTimerRunning = isTimerRunning
    }
}