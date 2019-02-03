package ru.ischenko.roman.focustimer.ui.main.notification

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.util.concurrent.TimeUnit

class FocusTimerNotificationService : Service() {

    companion object {
        private const val EXTRA_ACTION = "EXTRA_ACTION"
        private const val EXTRA_ACTION_START = "EXTRA_ACTION_START"
        private const val EXTRA_ACTION_CANCEL = "EXTRA_ACTION_CANCEL"
        private const val EXTRA_ACTION_PAUSE_RESUME_WORK = "EXTRA_ACTION_PAUSE_RESUME_WORK"
        private const val EXTRA_GOAL = "EXTRA_GOAL"
        private const val EXTRA_START_TIME = "EXTRA_START_TIME"
        private const val EXTRA_TOTAL_TIME = "EXTRA_TOTAL_TIME"

        fun showNotification(context: Context, goal: String, startTime: Long, totalSeconds: Long) {
            val intent = Intent(context, FocusTimerNotificationService::class.java)
            intent.putExtra(EXTRA_ACTION, EXTRA_ACTION_START)
            intent.putExtra(EXTRA_GOAL, goal)
            intent.putExtra(EXTRA_START_TIME, startTime)
            intent.putExtra(EXTRA_TOTAL_TIME, totalSeconds)
            context.startService(intent)
        }

        fun pauseResumeWork(context: Context) {
            val intent = Intent(context, FocusTimerNotificationService::class.java)
            intent.putExtra(EXTRA_ACTION, EXTRA_ACTION_PAUSE_RESUME_WORK)
            context.startService(intent)
        }

        fun cancelNotification(context: Context) {
            val intent = Intent(context, FocusTimerNotificationService::class.java)
            intent.putExtra(EXTRA_ACTION, EXTRA_ACTION_CANCEL)
            context.startService(intent)
        }
    }

    private val binder = NotificationServiceBinder()
    private lateinit var focusTimerProgressHandler: FocusTimerProgressHandler

    var onTimeChangedListener: OnTimeChangedListener? = null

    private var timerStarted = false
    private var startTime: Long = 0
    private var secondsPassed: Long = 0
    private var totalSeconds: Long = 0
    private lateinit var focusTimerNotification: FocusTimerNotification

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val action = intent?.getStringExtra(EXTRA_ACTION)

        if (action == EXTRA_ACTION_START) {

            val goal = intent.getStringExtra(EXTRA_GOAL)
            startTime = intent.getLongExtra(EXTRA_START_TIME, System.currentTimeMillis())
            totalSeconds = intent.getLongExtra(EXTRA_TOTAL_TIME, 0)

            initFocusTimerProgressHandler()

            val notification = initFocusTimerNotification(goal)
            startForeground(FocusTimerNotification.FOCUS_TIMER_NOTIFICATION_REQUEST_CODE, notification)

            timerStarted = true
            focusTimerProgressHandler.startTimer()
        }
        else if(action == EXTRA_ACTION_CANCEL) {
            cancelTimer()
        }
        else if(action == EXTRA_ACTION_PAUSE_RESUME_WORK) {
            resumePauseTimer()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        focusTimerProgressHandler.unregister()
    }

    private fun initFocusTimerNotification(goal: String) : Notification {

        focusTimerNotification = FocusTimerNotification(this)
        val notification = focusTimerNotification.notifyFocusOnWork(goal)

        focusTimerNotification.register()
        focusTimerNotification.focusTimerNotificationListener =
                object : FocusTimerNotification.FocusTimerNotificationListener {

            override fun onPause() {
                resumePauseTimer()
            }

            override fun onResume() {
                resumePauseTimer()
            }

            override fun onCancel() {
                cancelTimer()
            }
        }

        return notification
    }

    private fun  initFocusTimerProgressHandler() {
        focusTimerProgressHandler = FocusTimerProgressHandler(this, startTime, totalSeconds)
        focusTimerProgressHandler.focusTimerProgressHandlerListener =
                object : FocusTimerProgressHandler.FocusTimerProgressHandlerListener {

            override fun onTimeChanged(secondsPassed: Long) {
                val timeLeft = totalSeconds - secondsPassed
                focusTimerNotification.updateProgress(timeToString(timeLeft))
                onTimeChangedListener?.onTimeChanged(secondsPassed)
                focusTimerProgressHandler.continueTimer()
            }

            override fun onTimeFinish() {
                finishTimer()
            }
        }

        focusTimerProgressHandler.register()
    }

    private fun resumePauseTimer() {
        if (timerStarted) {
            timerStarted = false
            focusTimerProgressHandler.stopTimer()
            focusTimerNotification.pause()
            onTimeChangedListener?.onTimerPaused()
        } else {
            startTime = System.currentTimeMillis() - (secondsPassed + 1) * 1000
            timerStarted = true
            focusTimerProgressHandler.continueTimer()
            focusTimerNotification.resume()
            onTimeChangedListener?.onTimerResumed()
        }
    }

    private fun cancelTimer() {
        timerStarted = false
        focusTimerProgressHandler.cancelTimer()
        stopSelf()

        focusTimerNotification.cancel()
        onTimeChangedListener?.onTimerCancel()
    }

    private fun finishTimer() {
        timerStarted = false
        focusTimerProgressHandler.cancelTimer()
        stopSelf()

        focusTimerNotification.finish()
        focusTimerNotification.unregister()
        onTimeChangedListener?.onTimerFinish()
    }

    private fun timeToString(timeLeft: Long) : String {
        val min = TimeUnit.SECONDS.toMinutes(timeLeft)
        val sec = timeLeft - TimeUnit.MINUTES.toSeconds(min)
        return "$min:%02d".format(sec)
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    inner class NotificationServiceBinder : Binder() {
        internal val service: FocusTimerNotificationService
            get() = this@FocusTimerNotificationService
    }

    interface OnTimeChangedListener {

        fun onTimeChanged(timerSecondsPassed: Long)

        fun onTimerPaused()

        fun onTimerResumed()

        fun onTimerFinish()

        fun onTimerCancel()
    }
}
