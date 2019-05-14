package ru.ischenko.roman.focustimer.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.ischenko.roman.focustimer.notification.*
import ru.ischenko.roman.focustimer.timer.R
import ru.ischenko.roman.focustimer.utils.Event
import ru.ischenko.roman.focustimer.utils.ResourceProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

enum class UiState { STARTED, PAUSED, STOPPED }

class FocusTimerViewModel(private val focusTimerServiceController: FocusTimerServiceController,
                          private val notification: FocusTimerNotification,
                          private val notificationServiceDelegate: NotificationServiceDelegate,
                          private val resourceProvider: ResourceProvider) : ViewModel(), OnTimeChangedListener {

    companion object {
        private val POMODORE_TIME = TimeUnit.MINUTES.toSeconds(25)
        private val REST_TIME = TimeUnit.MINUTES.toSeconds(5)

        private const val ACTION_REST: String = "ACTION_REST"
        private const val ACTION_WORK: String = "ACTION_WORK"
    }

    val goal: MutableLiveData<String> = MutableLiveData()
    val uiState: MutableLiveData<UiState> = MutableLiveData()
    val timerSecondsPassed: MutableLiveData<Long> = MutableLiveData()

    val startTimerEvent: MutableLiveData<Event<Long>> = MutableLiveData()
    val editGoalTextEvent: MutableLiveData<Event<Unit>> = MutableLiveData()

    private var isWorkTime = true
    private lateinit var slogan: String

    init {
        goal.value = resourceProvider.getText(R.string.focus_timer_notification_no_goal)

        goal.observeForever { goal ->
            if (uiState.value == UiState.STARTED) {
                focusTimerServiceController.updateTimer(slogan, goal)
            }
        }

        uiState.value = UiState.STOPPED

        notificationServiceDelegate.startService(onTimeChangedListener = this)

        notification.focusTimerActionNotificationListener = object : FocusTimerNotification.FocusTimerActionNotificationListener {

            override fun onAction(action: String) {
                Timber.d("onAction($action)")

                when (action) {
                    ACTION_WORK -> {
                        startTimerForWork()
                    }
                    ACTION_REST -> {
                        startTimerForRest()
                    }
                }
            }
        }

        notification.register()
    }

    private fun showNotification() {

        val message: String
        val actions: List<NotificationAction>

        if (isWorkTime) {
            actions = listOf(CancelAction, CustomAction(ACTION_REST, resourceProvider.getText(R.string.focus_timer_rest)))
            slogan = resourceProvider.getText(R.string.focus_timer_notification_rest)
            message = goal.value ?: resourceProvider.getText(R.string.focus_timer_notification_no_goal)
        } else {
            actions = listOf(CancelAction, CustomAction(ACTION_WORK, resourceProvider.getText(R.string.focus_timer_work)))
            slogan = resourceProvider.getText(R.string.focus_timer_notification_focus_on_work)
            message = goal.value ?: resourceProvider.getText(R.string.focus_timer_notification_no_goal)
        }

        isWorkTime = !isWorkTime

        notification.notify(slogan, message, isOngoing = false, shouldNotify = true, actions = actions)
    }

    private fun startTimerForWork() {
        uiState.value = UiState.STARTED
        startTimerEvent.value = Event(POMODORE_TIME)
        slogan = resourceProvider.getText(R.string.focus_timer_notification_focus_on_work)
        focusTimerServiceController.startTimer(POMODORE_TIME, slogan,
                goal.value ?: resourceProvider.getText(R.string.focus_timer_notification_no_goal))
    }

    private fun startTimerForRest() {
        uiState.value = UiState.STARTED
        startTimerEvent.value = Event(REST_TIME)
        slogan = resourceProvider.getText(R.string.focus_timer_notification_rest)
        focusTimerServiceController.startTimer(POMODORE_TIME, slogan,
                goal.value ?: resourceProvider.getText(R.string.focus_timer_notification_no_goal))
    }

    fun handleStartStopTimer() {
        if (uiState.value == UiState.STOPPED) {
            if (isWorkTime) {
                startTimerForWork()
            } else {
                startTimerForRest()
            }
        } else {
            focusTimerServiceController.resumePauseTimer()
        }
    }

    fun handleCancelTimer() {
        if (uiState.value != UiState.STOPPED) {
            uiState.value = UiState.STOPPED
            this.timerSecondsPassed.value = 0L
            focusTimerServiceController.stopTimer()
        }
    }

    fun handleStartEditGoalText() {
        editGoalTextEvent.value = Event(Unit)
    }

    override fun onTimeChanged(timerSecondsPassed: Long) {
        this.timerSecondsPassed.value = timerSecondsPassed
    }

    override fun onTimerPaused() {
        uiState.value = UiState.PAUSED
    }

    override fun onTimerResumed() {
        uiState.value = UiState.STARTED
    }

    override fun onTimerFinish() {
        uiState.value = UiState.STOPPED
        this.timerSecondsPassed.value = 0L
        showNotification()
    }

    override fun onTimerCancel() {
        uiState.value = UiState.STOPPED
        this.timerSecondsPassed.value = 0L
    }

    override fun onCleared() {
        super.onCleared()
        notification.unregister()
        notificationServiceDelegate.stopService()
    }
}
