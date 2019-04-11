package ru.ischenko.roman.focustimer.notification

import android.content.Context


interface FocusTimerServiceController {

    fun startTimer(time: Long, title: String, message: String)

    fun stopTimer()

    fun resumePauseTimer()
}

class FocusTimerServiceControllerIml(private val context: Context) : FocusTimerServiceController {

    override fun startTimer(time: Long, title: String, message: String) =
        FocusTimerNotificationService.startTimer(
                context, title, message, System.currentTimeMillis(), time,
                arrayOf(CancelAction, ResumePauseAction))

    override fun stopTimer() =
         FocusTimerNotificationService.cancelNotification(context)

    override fun resumePauseTimer() =
         FocusTimerNotificationService.resumePauseTimer(context)
}