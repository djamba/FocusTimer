package ru.ischenko.roman.focustimer.ui.main.notification

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
class FocusTimerProgressHandler(private val context: Context, startTime: Long, private val totalSeconds: Long) {

    companion object {
        private const val HANDLER_MESSAGE_ID = 45678
        private const val ACTION_ALARM: String = "FocusTimerProgressHandler.ACTION_ALARM"
    }

    private val timerHandler: Handler
    private val screenStateReceiver: BroadcastReceiver

    private var alarmManager: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val alarmPendingIntent = PendingIntent.getBroadcast(context,0, Intent(ACTION_ALARM), 0)

    var focusTimerProgressHandlerListener: FocusTimerProgressHandlerListener? = null

    init {
        timerHandler = Handler(Handler.Callback {
            if (it.what == HANDLER_MESSAGE_ID) {
                val secondsPassed = (System.currentTimeMillis() - startTime) / 1000
                if (secondsPassed < totalSeconds) {
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
                if (action == Intent.ACTION_SCREEN_ON) {
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
    }

    fun unregister() {
        context.unregisterReceiver(screenStateReceiver)
        context.unregisterReceiver(alarmReceiver)
    }

    fun startTimer() {
        timerHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_ID, 1000)
        alarmManager?.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + TimeUnit.SECONDS.toMillis(totalSeconds),
            alarmPendingIntent
        )
    }

    fun continueTimer() {
        timerHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_ID, 1000)
    }

    fun stopTimer() {
        timerHandler.removeMessages(HANDLER_MESSAGE_ID)
    }

    fun cancelTimer() {
        timerHandler.removeMessages(HANDLER_MESSAGE_ID)
        alarmManager?.cancel(alarmPendingIntent)
    }

    interface FocusTimerProgressHandlerListener {

        fun onTimeChanged(secondsPassed: Long)

        fun onTimeFinish()
    }
}