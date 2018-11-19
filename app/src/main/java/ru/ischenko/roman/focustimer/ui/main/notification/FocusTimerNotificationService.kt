package ru.ischenko.roman.focustimer.ui.main.notification

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder

class FocusTimerNotificationService : Service() {

    companion object {
        private const val HANDLER_MESSAGE_ID = 45678
        private const val EXTRA_ACTION = "EXTRA_ACTION"
        private const val EXTRA_ACTION_START = "EXTRA_ACTION_START"
        private const val EXTRA_ACTION_CANCEL = "EXTRA_ACTION_CANCEL"
        private const val EXTRA_GOAL = "EXTRA_GOAL"
        private const val EXTRA_START_TIME = "EXTRA_START_TIME"
        private const val EXTRA_TOTAL_TIME = "EXTRA_TOTAL_TIME"

        fun showNotification(context: Context, goal: String, startTime: Long, totalSeconds: Int) {
            val intent = Intent(context, FocusTimerNotificationService::class.java)
            intent.putExtra(EXTRA_ACTION, EXTRA_ACTION_START)
            intent.putExtra(EXTRA_GOAL, goal)
            intent.putExtra(EXTRA_START_TIME, startTime)
            intent.putExtra(EXTRA_TOTAL_TIME, totalSeconds)
            context.startService(intent)
        }

        fun cancelNotification(context: Context) {
            val intent = Intent(context, FocusTimerNotificationService::class.java)
            intent.putExtra(EXTRA_ACTION, EXTRA_ACTION_CANCEL)
            context.startService(intent)
        }
    }

    private lateinit var timerHandler: Handler

    init {
        timerHandler = Handler(Handler.Callback {
            if (it.what == HANDLER_MESSAGE_ID) {
                val secondsPassed = (System.currentTimeMillis() - startTime) / 1000

                if (secondsPassed < totalSeconds) {
                    focusTimerNotification.updateProgress((totalSeconds - secondsPassed).toString())
                    timerHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_ID, 1000)
                }
                else {
                    finish()
                }
            }
            return@Callback true
        })
    }

    private var startTime: Long = 0
    private var totalSeconds: Int = 0
    private lateinit var focusTimerNotification: FocusTimerNotification

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val action = intent?.getStringExtra(EXTRA_ACTION)

        if (action == EXTRA_ACTION_START) {

            val goal = intent.getStringExtra(EXTRA_GOAL)
            startTime = intent.getLongExtra(EXTRA_START_TIME, System.currentTimeMillis())
            totalSeconds = intent.getIntExtra(EXTRA_TOTAL_TIME, 0)

            focusTimerNotification = FocusTimerNotification(this)
            val notification = focusTimerNotification.notifyFocusOnWork(goal)

            startForeground(FocusTimerNotification.FOCUS_TIMER_NOTIFICATION_REQUEST_CODE, notification)

            timerHandler.sendEmptyMessage(HANDLER_MESSAGE_ID)
        }
        else if(action == EXTRA_ACTION_CANCEL) {
            cancel()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun cancel() {
        timerHandler.removeMessages(HANDLER_MESSAGE_ID)
        focusTimerNotification.cancel()
        stopSelf()
    }

    private fun finish() {
        stopSelf()
        timerHandler.removeMessages(HANDLER_MESSAGE_ID)
        focusTimerNotification.finish()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
