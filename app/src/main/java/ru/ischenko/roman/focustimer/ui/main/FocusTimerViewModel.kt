package ru.ischenko.roman.focustimer.ui.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.ischenko.roman.focustimer.R
import ru.ischenko.roman.focustimer.ui.notification.*
import ru.ischenko.roman.focustimer.utils.ui.Event
import ru.ischenko.roman.focustimer.utils.ui.ResourceProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * User: roman
 * Date: 22.07.18
 * Time: 21:06
 */

enum class UiState { STARTED, PAUSED, STOPPED }

class FocusTimerViewModel(private val startTimerUseCase: StartTimerUseCase,
                          private val stopTimerUseCase: StopTimerUseCase,
                          private val resumePauseTimerUseCase: ResumePauseTimerUseCase,
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
    val stopTimerEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val editGoalTextEvent: MutableLiveData<Event<Unit>> = MutableLiveData()

    private var isWorkTime = true

    init {
        goal.value = resourceProvider.getText(R.string.focus_timer_notification_no_goal)

        uiState.value = UiState.STOPPED

        notificationServiceDelegate.startService(onTimeChangedListener = this)

        notification.focusTimerActionNotificationListener = object : FocusTimerNotification.FocusTimerActionNotificationListener {

            override fun onAction(action: String) {
                Timber.d("onAction($action)")

                // TODO: Привсти UI-таймера в консистентное состояние
                when (action) {
                    ACTION_REST -> {
                        startTimerEvent.value = Event(REST_TIME)
                        startTimerUseCase(REST_TIME,
                                resourceProvider.getText(R.string.focus_timer_notification_rest),
                                goal.value ?: resourceProvider.getText(R.string.focus_timer_notification_no_goal))
                    }
                    ACTION_WORK -> {
                        startTimerEvent.value = Event(POMODORE_TIME)
                        startTimerUseCase(POMODORE_TIME, resourceProvider.getText(R.string.focus_timer_notification_focus_on_work),
                                 goal.value ?: resourceProvider.getText(R.string.focus_timer_notification_no_goal))
                    }
                }
            }
        }

        notification.register()
    }

    private fun showNotification() {

        val title: String
        val message: String
        val actions: List<NotificationAction>

        if (isWorkTime) {
            actions = listOf(CancelAction, CustomAction(ACTION_REST, "Rest"))
            title = resourceProvider.getText(R.string.focus_timer_notification_rest)
            message = goal.value ?: resourceProvider.getText(R.string.focus_timer_notification_no_goal)
        } else {
            actions = listOf(CancelAction, CustomAction(ACTION_WORK, "Work"))
            title = resourceProvider.getText(R.string.focus_timer_notification_focus_on_work)
            message = goal.value ?: resourceProvider.getText(R.string.focus_timer_notification_no_goal)
        }

        isWorkTime = !isWorkTime

        notification.notify(title, message, false, actions)
    }

    fun handleStartTimer() {
        uiState.value = UiState.STARTED
        startTimerEvent.value = Event(POMODORE_TIME)
        startTimerUseCase(POMODORE_TIME, resourceProvider.getText(R.string.focus_timer_notification_focus_on_work),
                goal.value ?: "focus")
    }

    fun handleStopTimer() {
        if (uiState.value != UiState.STOPPED) {
            stopTimerEvent.value = Event(Unit)
            stopTimerUseCase()
        }
    }

    fun handleResumePauseTimer() {
        if (uiState.value != UiState.STOPPED) {
            resumePauseTimerUseCase()
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
        showNotification()
    }

    override fun onTimerCancel() {
        uiState.value = UiState.STOPPED
    }

    override fun onCleared() {
        super.onCleared()
        notification.unregister()
        notificationServiceDelegate.stopService()
    }
}
