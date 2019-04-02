package ru.ischenko.roman.focustimer.ui.main

import android.app.Notification
import ru.ischenko.roman.focustimer.ui.notification.NotificationAction

/**
 * User: roman
 * Date: 20.02.19
 * Time: 22:36
 */

interface FocusTimerNotificationCreator {

    fun createNotification(title: String, message: String, isOngoing: Boolean,
                           actions: List<NotificationAction>): Notification
}

interface FocusTimerNotification {

    var focusTimerNotificationListener: FocusTimerNotificationListener?

    var focusTimerActionNotificationListener: FocusTimerActionNotificationListener?

    fun register()

    fun unregister()

    fun updateProgress(workTimeSec: Long)

    fun notify(title: String, message: String, isOngoing: Boolean,
               actions: List<NotificationAction>)

    fun cancel()

    fun pause()

    fun resume()

    interface FocusTimerNotificationListener {

        fun onPause()

        fun onResume()

        fun onCancel()
    }

    interface FocusTimerActionNotificationListener {

        fun onAction(action: String)
    }
}