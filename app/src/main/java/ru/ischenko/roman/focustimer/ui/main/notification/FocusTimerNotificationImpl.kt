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
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import ru.ischenko.roman.focustimer.R
import ru.ischenko.roman.focustimer.ui.FocusTimerActivity
import java.util.concurrent.TimeUnit

/**
 * User: roman
 * Date: 30.09.18
 * Time: 13:09
 */
class FocusTimerNotificationImpl(private val context: Context) : FocusTimerNotification, FocusTimerNotificationCreator {

    companion object {
        const val FOCUS_TIMER_NOTIFICATION_REQUEST_CODE: Int = 2356
        private const val FOCUS_TIMER_NOTIFICATION_CHANNEL_ID: String = "FOCUS_TIMER_NOTIFICATION_CHANNEL"
        private const val FOCUS_TIMER_NOTIFICATION_CHANNEL_NAME: String = "FOCUS_TIMER"

        private const val ACTION_RESUME: String = "FocusTimerNotification.ACTION_RESUME"
        private const val ACTION_PAUSE: String = "FocusTimerNotification.ACTION_PAUSE"
        private const val ACTION_CANCEL: String = "FocusTimerNotification.ACTION_CANCEL"
        private const val ACTION_CUSTOM: String = "FocusTimerNotification.ACTION_CUSTOM"
        private const val EXTRA_ACTION: String = "FocusTimerNotification.EXTRA_ACTION"
    }

    private var title: String? = null
    private lateinit var pendingIntent: PendingIntent
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override var focusTimerNotificationListener: FocusTimerNotification.FocusTimerNotificationListener? = null
    override var focusTimerActionNotificationListener: FocusTimerNotification.FocusTimerActionNotificationListener? = null

    private val contentIntent = Intent(context, FocusTimerActivity::class.java)
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val pausePendingIntent = PendingIntent.getBroadcast(context,0, Intent(ACTION_PAUSE), 0)
    private val resumePendingIntent = PendingIntent.getBroadcast(context,0, Intent(ACTION_RESUME), 0)
    private val cancelPendingIntent = PendingIntent.getBroadcast(context,0, Intent(ACTION_CANCEL), 0)

    private val pauseAction = NotificationCompat.Action(R.drawable.ic_pause, "Pause", pausePendingIntent)
    private val resumeAction = NotificationCompat.Action(R.drawable.ic_resume, "Resume", resumePendingIntent)
    private val cancelAction = NotificationCompat.Action(R.drawable.ic_stop, "Cancel", cancelPendingIntent)

    private val actionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_PAUSE -> focusTimerNotificationListener?.onPause()
                ACTION_RESUME -> focusTimerNotificationListener?.onResume()
                ACTION_CANCEL -> focusTimerNotificationListener?.onCancel()
                ACTION_CUSTOM -> focusTimerActionNotificationListener?.onAction(
                            intent.getStringExtra(EXTRA_ACTION) ?: ACTION_CUSTOM)
            }
        }
    }

    override fun register() {
        val intentFilter = IntentFilter().apply {
            addAction(ACTION_CANCEL)
            addAction(ACTION_RESUME)
            addAction(ACTION_PAUSE)
            addAction(ACTION_CUSTOM)
        }
        context.registerReceiver(actionReceiver, intentFilter)
    }

    override fun unregister() {
        context.unregisterReceiver(actionReceiver)
    }

    override fun createNotification(title: String, message: String, isOngoing: Boolean,
                                    actions: List<NotificationAction>): Notification {

        this.title = title

        pendingIntent = PendingIntent.getActivity(context, FOCUS_TIMER_NOTIFICATION_REQUEST_CODE,
                contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        notificationBuilder = NotificationCompat.Builder(context, FOCUS_TIMER_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_focus_notification_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(!isOngoing)
                .setOngoing(isOngoing)
                .setOnlyAlertOnce(true)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentIntent(pendingIntent)
                .setShowWhen(true)
                .setLights(Color.parseColor("#ef5350"), 500, 500)

        for (action in actions) {
            when (action) {
                is ResumePauseAction -> notificationBuilder.addAction(pauseAction)
                is CancelAction -> notificationBuilder.addAction(cancelAction)
                is CustomAction -> {
                    val intent = Intent(ACTION_CUSTOM).apply { putExtra(EXTRA_ACTION, action.action) }
                    val pendingIntent = PendingIntent.getBroadcast(context, action.action.hashCode(), intent, 0)
                    val notificationAction = NotificationCompat.Action(R.drawable.ic_pause, action.title, pendingIntent)
                    notificationBuilder.addAction(notificationAction)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(FOCUS_TIMER_NOTIFICATION_CHANNEL_ID,
                    FOCUS_TIMER_NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder.build()
    }

    override fun updateProgress(workTimeSec: Long) {
        notificationBuilder.setContentTitle(title + timeToString(workTimeSec))
        showNotification()
    }

    override fun notify(title: String, message: String, isOngoing: Boolean,
                        actions: List<NotificationAction>) {
        val notification = createNotification(title, message, isOngoing, actions)
        notificationManager.notify(FOCUS_TIMER_NOTIFICATION_REQUEST_CODE, notification)
    }

    override fun cancel() {
        pendingIntent.cancel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel(FOCUS_TIMER_NOTIFICATION_CHANNEL_ID)
        }

        notificationManager.cancel(FOCUS_TIMER_NOTIFICATION_REQUEST_CODE)
    }

    @SuppressLint("RestrictedApi")
    override fun pause() {
        notificationBuilder.mActions.remove(pauseAction)
        notificationBuilder.addAction(resumeAction)
        showNotification()
    }

    @SuppressLint("RestrictedApi")
    override fun resume() {
        notificationBuilder.mActions.remove(resumeAction)
        notificationBuilder.addAction(pauseAction)
        showNotification()
    }

    private fun showNotification() {
        notificationManager.notify(FOCUS_TIMER_NOTIFICATION_REQUEST_CODE, notificationBuilder.build())
    }

    private fun timeToString(timeLeft: Long) : String {
        val min = TimeUnit.SECONDS.toMinutes(timeLeft)
        val sec = timeLeft - TimeUnit.MINUTES.toSeconds(min)
        return "$min:%02d".format(sec)
    }

}