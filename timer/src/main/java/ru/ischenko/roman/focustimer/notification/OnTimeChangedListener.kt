package ru.ischenko.roman.focustimer.notification

/**
 * User: roman
 * Date: 31.03.19
 * Time: 18:44
 */
interface OnTimeChangedListener {

    fun onTimeChanged(timerSecondsPassed: Long)

    fun onTimerPaused()

    fun onTimerResumed()

    fun onTimerFinish()

    fun onTimerCancel()
}