package ru.ischenko.roman.focustimer.ui.main.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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

        private const val ACTION_RESUME: String = "FocusTimerNotification.ACTION_RESUME"
        private const val ACTION_PAUSE: String = "FocusTimerNotification.ACTION_PAUSE"
        private const val ACTION_CANCEL: String = "FocusTimerNotification.ACTION_CANCEL"
    }

    private lateinit var pendingIntent: PendingIntent
    private lateinit var notificationBuilder: NotificationCompat.Builder
    var focusTimerNotificationListener: FocusTimerNotificationListener? = null

    private val intent = Intent(context, FocusTimerActivity::class.java)
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val pausePendingIntent = PendingIntent.getBroadcast(context,0, Intent(ACTION_PAUSE), 0)
    private val resumePendingIntent = PendingIntent.getBroadcast(context,0, Intent(ACTION_RESUME), 0)
    private val cancelPendingIntent = PendingIntent.getBroadcast(context,0, Intent(ACTION_CANCEL), 0)

    private val pauseAction = NotificationCompat.Action(R.drawable.ic_pause, "Pause", pausePendingIntent)
    private val resumeAction = NotificationCompat.Action(R.drawable.ic_resume, "Resume", resumePendingIntent)
    private val cancelAction = NotificationCompat.Action(R.drawable.ic_stop, "Cancel", cancelPendingIntent)

    private val pauseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            focusTimerNotificationListener?.onPause()
        }
    }

    private val resumeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            focusTimerNotificationListener?.onResume()
        }
    }

    private val cancelReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            focusTimerNotificationListener?.onCancel()
        }
    }

    fun register() {
        context.registerReceiver(pauseReceiver, IntentFilter(ACTION_PAUSE))
        context.registerReceiver(resumeReceiver, IntentFilter(ACTION_RESUME))
        context.registerReceiver(cancelReceiver, IntentFilter(ACTION_CANCEL))
    }

    fun unregister() {
        context.unregisterReceiver(pauseReceiver)
        context.unregisterReceiver(resumeReceiver)
        context.unregisterReceiver(cancelReceiver)
    }

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
                .addAction(cancelAction)
                .addAction(pauseAction)
                .setShowWhen(true)

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
        showNotification()
    }

    @SuppressLint("RestrictedApi")
    fun finish() {
        notificationBuilder.mActions.clear()
        notificationBuilder
                .setAutoCancel(true)
                .setOngoing(false)
        notificationBuilder.setContentTitle(context.getString(R.string.focus_timer_notification_rest))
        showNotification()
    }

    fun cancel() {
        pendingIntent.cancel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(FOCUS_TIMER_NOTIFICATION_CHANNEL_ID)
        }

        notificationManager.cancel(FOCUS_TIMER_NOTIFICATION_REQUEST_CODE)
    }

    @SuppressLint("RestrictedApi")
    fun pause() {
        notificationBuilder.mActions.remove(pauseAction)
        notificationBuilder.addAction(resumeAction)
        showNotification()
    }

    @SuppressLint("RestrictedApi")
    fun resume() {
        notificationBuilder.mActions.remove(resumeAction)
        notificationBuilder.addAction(pauseAction)
        showNotification()
    }

    private fun showNotification() {
        notificationManager.notify(FOCUS_TIMER_NOTIFICATION_REQUEST_CODE, notificationBuilder.build())
    }

    interface FocusTimerNotificationListener {

        fun onPause()

        fun onResume()

        fun onCancel()
    }
}