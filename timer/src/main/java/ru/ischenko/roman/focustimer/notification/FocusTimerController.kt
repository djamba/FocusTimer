package ru.ischenko.roman.focustimer.notification

import android.content.Context

// TODO: move interface to domain
interface FocusTimerController {

    fun startTimer(time: Long, title: String, message: String)

    fun updateTimer(title: String, message: String)

    fun stopTimer()

    fun resumePauseTimer()
}

class FocusTimerServiceController(private val context: Context) : FocusTimerController {

    override fun startTimer(time: Long, title: String, message: String) =
        FocusTimerNotificationService.startTimer(
                context, title, message, System.currentTimeMillis(), time,
                arrayOf(CancelAction, ResumePauseAction))

    override fun updateTimer(title: String, message: String) =
        FocusTimerNotificationService.updateTimer(context, title, message)

    override fun stopTimer() =
         FocusTimerNotificationService.cancelNotification(context)

    override fun resumePauseTimer() =
         FocusTimerNotificationService.resumePauseTimer(context)
}