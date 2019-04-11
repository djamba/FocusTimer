package ru.ischenko.roman.focustimer.notification

import android.content.Context

/**
 * User: roman
 * Date: 20.03.19
 * Time: 22:06
 */
class StartTimerUseCase(private val context: Context) {

    operator fun invoke(time: Long, title: String, message: String) {
        FocusTimerNotificationService.startTimer(
                context, title, message, System.currentTimeMillis(), time,
                arrayOf(CancelAction, ResumePauseAction))
    }
}