package ru.ischenko.roman.focustimer.ui.main.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import ru.ischenko.roman.focustimer.R
import ru.ischenko.roman.focustimer.ui.FocusTimerActivity

/**
 * User: roman
 * Date: 30.09.18
 * Time: 13:09
 */
class FocusTimerNotification(val context: Context) {

    companion object {
        const val FOCUS_TIMER_NOTIFICATION_REQUEST_CODE: Int = 2356
        private const val FOCUS_TIMER_NOTIFICATION_CHANNEL_ID: String = "FOCUS_TIMER_NOTIFICATION_CHANNEL"
        private const val FOCUS_TIMER_NOTIFICATION_CHANNEL_NAME: String = "FOCUS_TIMER"
    }

    private lateinit var pendingIntent: PendingIntent
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private val intent = Intent(context, FocusTimerActivity::class.java)
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun notifyFocusOnWork(workGoal: String): Notification {

        pendingIntent = PendingIntent.getActivity(context, FOCUS_TIMER_NOTIFICATION_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)

        notificationBuilder = NotificationCompat.Builder(context, FOCUS_TIMER_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_focus_notification_icon)
                .setContentTitle(context.getString(R.string.focus_timer_notification_focus_on_work))
                .setContentText(workGoal)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(FOCUS_TIMER_NOTIFICATION_CHANNEL_ID,
                    FOCUS_TIMER_NOTIFICATION_CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder.build()
    }

    fun updateProgress(workTime: String) {
        notificationBuilder.setContentTitle(context.getString(R.string.focus_timer_notification_focus_on_work)  + workTime)
        notificationManager.notify(FOCUS_TIMER_NOTIFICATION_REQUEST_CODE, notificationBuilder.build())
    }

    fun finish() {
        notificationBuilder
                .setAutoCancel(true)
                .setOngoing(false)
        notificationBuilder.setContentTitle(context.getString(R.string.focus_timer_notification_rest))
        notificationManager.notify(FOCUS_TIMER_NOTIFICATION_REQUEST_CODE, notificationBuilder.build())
    }

    fun cancel() {
        pendingIntent.cancel()
    }
}