package ru.ischenko.roman.focustimer.notification

import android.app.Notification

interface NotificationCreator {

    fun createNotification(title: String, message: String, isOngoing: Boolean, shouldNotify: Boolean,
                           actions: List<NotificationAction>): Notification
}

interface FocusTimerNotification {

    var timerListener: TimerListener?

    var notificationActionListener: NotificationActionListener?

    fun register()

    fun unregister()

    fun updateProgress(workTimeSec: Long)

    fun updateContent(title: String, message: String)

    fun notify(title: String, message: String, isOngoing: Boolean, shouldNotify: Boolean,
               actions: List<NotificationAction>)

    fun cancel()

    fun pause()

    fun resume()

    interface TimerListener {

        fun onPause()

        fun onResume()

        fun onCancel()
    }

    interface NotificationActionListener {

        fun onAction(action: String)
    }
}