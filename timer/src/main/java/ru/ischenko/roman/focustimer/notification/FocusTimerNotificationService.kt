package ru.ischenko.roman.focustimer.notification

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import dagger.android.DaggerService
import javax.inject.Inject

class FocusTimerNotificationService : DaggerService() {

    companion object {
        private const val EXTRA_ACTION = "EXTRA_ACTION"
        private const val EXTRA_ACTION_START = "EXTRA_ACTION_START"
        private const val EXTRA_ACTION_CANCEL = "EXTRA_ACTION_CANCEL"
        private const val EXTRA_ACTION_PAUSE_RESUME_WORK = "EXTRA_ACTION_PAUSE_RESUME_WORK"
        private const val EXTRA_START_TIME = "EXTRA_START_TIME"
        private const val EXTRA_TOTAL_TIME = "EXTRA_TOTAL_TIME"
        private const val EXTRA_ACTIONS = "EXTRA_ACTIONS"
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_MESSAGE = "EXTRA_MESSAGE"

        fun startTimer(context: Context, title: String, message: String,
                       startTime: Long, totalSeconds: Long,
                       actions: Array<NotificationAction>) {
            val intent = Intent(context, FocusTimerNotificationService::class.java)
            intent.putExtra(EXTRA_ACTION, EXTRA_ACTION_START)
            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_MESSAGE, message)
            intent.putExtra(EXTRA_START_TIME, startTime)
            intent.putExtra(EXTRA_TOTAL_TIME, totalSeconds)
            intent.putExtra(EXTRA_ACTIONS, actions)
            context.startService(intent)
        }

        fun resumePauseTimer(context: Context) {
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

    @Inject
    lateinit var contentIntent: Intent

    private val binder = NotificationServiceBinder()
    private var progressHandler: FocusTimerProgressHandler? = null

    var onTimeChangedListener: OnTimeChangedListener? = null

    private var timerStarted = false
    private var totalSeconds: Long = 0
    private var focusTimerNotification: FocusTimerNotificationImpl? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val action = intent?.getStringExtra(EXTRA_ACTION)

        if (focusTimerNotification == null) {
            initNotification()
        }

        focusTimerNotification?.register()

        when (action) {
            EXTRA_ACTION_START -> {

                val title = intent.getStringExtra(EXTRA_TITLE)
                val message = intent.getStringExtra(EXTRA_MESSAGE)
                totalSeconds = intent.getLongExtra(EXTRA_TOTAL_TIME, 0)
                val actions = getActionsFromIntent(intent)

                initProgressHandler()

                val notification = focusTimerNotification?.createNotification(title, message,
                        isOngoing = true, shouldNotify = false, actions = actions)
                startForeground(FocusTimerNotificationImpl.FOCUS_TIMER_NOTIFICATION_REQUEST_CODE, notification)

                timerStarted = true
                progressHandler?.startTimer()
            }
            EXTRA_ACTION_CANCEL -> {
                cancelTimer()
            }
            EXTRA_ACTION_PAUSE_RESUME_WORK -> {
                resumePauseTimer()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun getActionsFromIntent(intent: Intent) : List<NotificationAction> {
        val parcelableActions = intent.getParcelableArrayExtra(EXTRA_ACTIONS)
        val actions = mutableListOf<NotificationAction>()
        for (parcelableAction in parcelableActions) {
            actions.add(parcelableAction as NotificationAction)
        }
        return actions
    }

    private fun initNotification() {
        focusTimerNotification = FocusTimerNotificationImpl(this, contentIntent)
        focusTimerNotification?.focusTimerNotificationListener =
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
    }

    private fun  initProgressHandler() {
        progressHandler = FocusTimerProgressHandler(this, totalSeconds)
        progressHandler?.focusTimerProgressHandlerListener =
                object : FocusTimerProgressHandler.FocusTimerProgressHandlerListener {

            override fun onTimeChanged(secondsPassed: Long) {
                val timeLeft = totalSeconds - secondsPassed
                focusTimerNotification?.updateProgress(timeLeft)
                onTimeChangedListener?.onTimeChanged(secondsPassed)
                progressHandler?.continueTimer()
            }

            override fun onTimeFinish() {
                finishTimer()
            }
        }

        progressHandler?.register()
    }

    private fun resumePauseTimer() {
        if (timerStarted) {
            timerStarted = false
            progressHandler?.stopTimer()
            focusTimerNotification?.pause()
            onTimeChangedListener?.onTimerPaused()
        } else {
            timerStarted = true
            progressHandler?.startTimer()
            focusTimerNotification?.resume()
            onTimeChangedListener?.onTimerResumed()
        }
    }

    private fun cancelTimer() {
        timerStarted = false
        progressHandler?.stopTimer()

        unregister()
        stopForeground(false)
        stopSelf()

        focusTimerNotification?.cancel()
        onTimeChangedListener?.onTimerCancel()
    }

    private fun finishTimer() {
        timerStarted = false
        progressHandler?.stopTimer()

        unregister()
        stopForeground(false)

        onTimeChangedListener?.onTimerFinish()
    }

    private fun unregister() {
        focusTimerNotification?.unregister()
        progressHandler?.unregister()
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    inner class NotificationServiceBinder : Binder() {
        internal val service: FocusTimerNotificationService
            get() = this@FocusTimerNotificationService
    }
}
