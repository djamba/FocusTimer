package ru.ischenko.roman.focustimer.ui.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.ischenko.roman.focustimer.R
import ru.ischenko.roman.focustimer.ui.main.notification.*
import ru.ischenko.roman.focustimer.utils.ui.Event
import ru.ischenko.roman.focustimer.utils.ui.ResourceProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * User: roman
 * Date: 22.07.18
 * Time: 21:06
 */
class FocusTimerViewModel(private val startTimerUseCase: StartTimerUseCase,
                          private val notification: FocusTimerNotification,
                          private val resourceProvider: ResourceProvider) : ViewModel() {

    companion object {
        private val POMODORE_TIME = TimeUnit.MINUTES.toSeconds(25)
        private val REST_TIME = TimeUnit.MINUTES.toSeconds(5)

        private const val ACTION_REST: String = "ACTION_REST"
        private const val ACTION_WORK: String = "ACTION_WORK"
    }

    val goal: MutableLiveData<String> = MutableLiveData()
    val startTimerEvent: MutableLiveData<Event<Long>> = MutableLiveData()

    private var isWorkTime = true

    init {
        goal.value = "Цель помидора"

        notification.focusTimerActionNotificationListener = object : FocusTimerNotification.FocusTimerActionNotificationListener {

            override fun onAction(action: String) {
                Timber.d("onAction($action)")

                // TODO: Привсти UI-таймера в консистентное состояние
                when (action) {
                    ACTION_REST -> {
                        startTimerEvent.value = Event(REST_TIME)
                        startTimerUseCase(REST_TIME,
                                resourceProvider.getText(R.string.focus_timer_notification_rest),
                                resourceProvider.getText(R.string.focus_timer_notification_rest_content))
                    }
                    ACTION_WORK -> {
                        startTimerEvent.value = Event(POMODORE_TIME)
                        startTimerUseCase(POMODORE_TIME, resourceProvider.getText(R.string.focus_timer_notification_focus_on_work),
                                 goal.value ?: "focus")
                    }
                }
            }
        }

        notification.register()
    }

    fun showNotification() {

        val title: String
        val message: String
        val actions: List<NotificationAction>

        if (isWorkTime) {
            actions = listOf(CancelAction, CustomAction(ACTION_REST, "Rest"))
            title = resourceProvider.getText(R.string.focus_timer_notification_rest)
            message = resourceProvider.getText(R.string.focus_timer_notification_rest_content)
        } else {
            actions = listOf(CancelAction, CustomAction(ACTION_WORK, "Work"))
            title = resourceProvider.getText(R.string.focus_timer_notification_focus_on_work)
            message = goal.value ?: "focus"
        }

        isWorkTime = !isWorkTime

        notification.notify(title, message, false, actions)
    }

    fun handleStartTimer() {
        startTimerEvent.value = Event(POMODORE_TIME)
        startTimerUseCase(POMODORE_TIME, resourceProvider.getText(R.string.focus_timer_notification_focus_on_work),
                goal.value ?: "focus")
    }

    override fun onCleared() {
        super.onCleared()
        notification.unregister()
    }
}
