package ru.ischenko.roman.focustimer.notification

interface OnTimeChangedListener {

    fun onTimeChanged(timerSecondsPassed: Long)

    fun onTimerPaused()

    fun onTimerResumed()

    fun onTimerFinish()

    fun onTimerCancel()
}