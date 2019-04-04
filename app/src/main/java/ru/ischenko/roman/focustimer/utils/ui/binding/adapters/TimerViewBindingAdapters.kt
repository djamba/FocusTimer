package ru.ischenko.roman.focustimer.utils.ui.binding.adapters

import androidx.databinding.BindingAdapter
import ru.ischenko.roman.focustimer.ui.common.TimerView

/**
 * User: roman
 * Date: 31.03.19
 * Time: 19:05
 */
object TimerViewBindingAdapters {

    @JvmStatic
    @BindingAdapter("timerSecondsPassed")
    fun updateTimeBindingAdapter(timerView: TimerView, timerSecondsPassed: Long) {
        timerView.updateTime(timerSecondsPassed)
    }

    @JvmStatic
    @BindingAdapter("stopTimer")
    fun stopTimeBindingAdapter(timerView: TimerView, isTimerStopped: Boolean) {
        if (isTimerStopped) {
            timerView.stopTimer()
        }
    }
}