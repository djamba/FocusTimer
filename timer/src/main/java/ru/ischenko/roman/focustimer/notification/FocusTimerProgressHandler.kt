package ru.ischenko.roman.focustimer.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.SystemClock
import java.util.concurrent.TimeUnit

/**
 * User: roman
 * Date: 03.02.19
 * Time: 18:38
 */
class FocusTimerProgressHandler(private val context: Context, private val totalSeconds: Long) {

    companion object {
        private const val HANDLER_MESSAGE_ID = 45678
        private const val ACTION_ALARM: String = "FocusTimerProgressHandler.ACTION_ALARM"
    }

    private var isStarted: Boolean = false
    private var isRegistered: Boolean = false

    private var totalSecondsPassed: Long = 0L
    private var startTimeFromLastPause: Long = 0L

    private val timerHandler: Handler
    private val screenStateReceiver: BroadcastReceiver

    private var alarmManager: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmPendingIntent = PendingIntent.getBroadcast(context,0, Intent(ACTION_ALARM), 0)

    var focusTimerProgressHandlerListener: FocusTimerProgressHandlerListener? = null

    init {
        timerHandler = Handler(Handler.Callback {
            if (it.what == HANDLER_MESSAGE_ID) {
                val secondsPassed = totalSecondsPassed + (System.currentTimeMillis() - startTimeFromLastPause) / 1000
                if (secondsPassed <= totalSeconds) {
                    focusTimerProgressHandlerListener?.onTimeChanged(secondsPassed)
                } else {
                    focusTimerProgressHandlerListener?.onTimeFinish()
                }
            }

            return@Callback true
        })

        screenStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action
                if (action == Intent.ACTION_SCREEN_ON && isStarted) {
                    timerHandler.sendEmptyMessage(HANDLER_MESSAGE_ID)
                } else if (action == Intent.ACTION_SCREEN_OFF) {
                    timerHandler.removeMessages(HANDLER_MESSAGE_ID)
                }
            }
        }
    }

    private val alarmReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            focusTimerProgressHandlerListener?.onTimeFinish()
        }
    }

    fun register() {
        val screenStateIntentFilter = IntentFilter(Intent.ACTION_SCREEN_ON).apply {
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        context.registerReceiver(screenStateReceiver, screenStateIntentFilter)

        context.registerReceiver(alarmReceiver, IntentFilter(ACTION_ALARM))
        isRegistered = true
    }

    fun unregister() {
        if (isRegistered) {
            context.unregisterReceiver(screenStateReceiver)
            context.unregisterReceiver(alarmReceiver)
            isRegistered = false
        }
    }

    fun startTimer() {
        isStarted = true
        startTimeFromLastPause = System.currentTimeMillis()
        timerHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_ID, 1000)
        alarmManager?.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + TimeUnit.SECONDS.toMillis(totalSeconds),
            alarmPendingIntent
        )
    }

    fun continueTimer() {
        isStarted = true
        timerHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_ID, 1000)
    }

    fun stopTimer() {
        isStarted = false
        totalSecondsPassed += (System.currentTimeMillis() - startTimeFromLastPause) / 1000
        timerHandler.removeMessages(HANDLER_MESSAGE_ID)
        alarmManager?.cancel(alarmPendingIntent)
    }

    interface FocusTimerProgressHandlerListener {

        fun onTimeChanged(secondsPassed: Long)

        fun onTimeFinish()
    }
}