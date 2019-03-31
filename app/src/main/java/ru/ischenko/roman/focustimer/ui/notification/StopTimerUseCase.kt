package ru.ischenko.roman.focustimer.ui.notification

import android.content.Context

/**
 * User: roman
 * Date: 27.03.19
 * Time: 22:46
 */
class StopTimerUseCase(private val context: Context) {

    operator fun invoke() {
        FocusTimerNotificationService.cancelNotification(context)
    }
}