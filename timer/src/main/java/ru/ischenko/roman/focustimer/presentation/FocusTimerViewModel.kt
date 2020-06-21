package ru.ischenko.roman.focustimer.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.ischenko.roman.focustimer.data.model.Pomodoro
import ru.ischenko.roman.focustimer.data.model.Task
import ru.ischenko.roman.focustimer.domain.*
import ru.ischenko.roman.focustimer.domain.error.CreatePomodoroException
import ru.ischenko.roman.focustimer.domain.error.CreateTaskException
import ru.ischenko.roman.focustimer.domain.error.GetPomodoroException
import ru.ischenko.roman.focustimer.domain.error.UpdateTaskException
import ru.ischenko.roman.focustimer.notification.*
import ru.ischenko.roman.focustimer.settings.domain.GetPomodoreDefaultEstimateUseCase
import ru.ischenko.roman.focustimer.settings.domain.GetPomodoreRestTimeUseCase
import ru.ischenko.roman.focustimer.settings.domain.GetPomodoreTimeUseCase
import ru.ischenko.roman.focustimer.timer.R
import ru.ischenko.roman.focustimer.utils.Event
import ru.ischenko.roman.focustimer.utils.ResourceProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

enum class UiState { STARTED_WORK, STARTED_REST, PAUSED, STOPPED }

private const val DEFAULT_TASK_ESTIMATE = 4
private const val ACTION_REST: String = "ACTION_REST"
private const val ACTION_WORK: String = "ACTION_WORK"

class FocusTimerViewModel(private val timer: FocusTimerController,
                          private val notification: FocusTimerNotification,
                          private val focusTimerService: FocusTimerServiceMediator,
                          private val resourceProvider: ResourceProvider,
                          private val createPomodoroUseCase: CreatePomodoroUseCase,
                          private val getTodayPomodorosCountUseCase: GetTodayPomodorosCountUseCase,
                          private val createFreeTaskUseCase: CreateFreeTaskUseCase,
                          private val increaseSpendPomodoroInTaskUseCase: IncreaseSpendPomodoroInTaskUseCase,
                          private val updateTaskGoalUseCase: UpdateTaskGoalUseCase,
                          private val getPomodoreTimeUseCase: GetPomodoreTimeUseCase,
                          private val getPomodoreRestTimeUseCase: GetPomodoreRestTimeUseCase,
                          private val getPomodoreDefaultEstimateUseCase: GetPomodoreDefaultEstimateUseCase
) : ViewModel(), OnTimeChangedListener {

    private var currentTask: Task? = null
    private var currentPomodoro: Pomodoro? = null
    private var pomodoreTime: Long = 0
    private var restTime: Long = 0

    val goal: MutableLiveData<String> = MutableLiveData()
    val estimatedPomodorosCount: MutableLiveData<Int> = MutableLiveData()
    val spendPomodorosCount: MutableLiveData<Int> = MutableLiveData()
    val todayPomodoroCount: MutableLiveData<Long> = MutableLiveData()

    val uiState: MutableLiveData<UiState> = MutableLiveData()
    val timerSecondsPassed: MutableLiveData<Long> = MutableLiveData()

    val startTimerEvent: MutableLiveData<Event<Long>> = MutableLiveData()
    val editGoalTextEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val errorEvent: MutableLiveData<Event<String>> = MutableLiveData()

    private var isWorkTime = true
    private lateinit var slogan: String

    init {
        uiState.value = UiState.STOPPED

        goal.value = resourceProvider.getText(R.string.focus_timer_notification_no_goal)
        goal.observeForever { goal ->
            if (uiState.value != UiState.STOPPED) {
                timer.updateTimer(slogan, goal)
            }
        }

        spendPomodorosCount.value = 0
        refreshSettings()

        focusTimerService.startService(onTimeChangedListener = this)

        getTodayPomodorosCount()

        setupNotification()
    }

    internal fun refreshSettings() {
        estimatedPomodorosCount.value = getPomodoreDefaultEstimateUseCase()
        pomodoreTime = TimeUnit.MINUTES.toSeconds(getPomodoreTimeUseCase())
        restTime = TimeUnit.MINUTES.toSeconds(getPomodoreRestTimeUseCase())
    }

    private fun getTodayPomodorosCount() {
        viewModelScope.launch {
            try {
                todayPomodoroCount.value = getTodayPomodorosCountUseCase()
            } catch(e: GetPomodoroException) {
                Timber.e(e, e.message)
            }
        }
    }

    private fun setupNotification() {

        notification.notificationActionListener = object : FocusTimerNotification.NotificationActionListener {

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
        uiState.value = UiState.STARTED_WORK
        startTimerEvent.value = Event(pomodoreTime)
        slogan = resourceProvider.getText(R.string.focus_timer_notification_focus_on_work)
        timer.startTimer(pomodoreTime, slogan,
                goal.value ?: resourceProvider.getText(R.string.focus_timer_notification_no_goal))
    }

    private fun startTimerForRest() {
        uiState.value = UiState.STARTED_REST
        startTimerEvent.value = Event(restTime)
        slogan = resourceProvider.getText(R.string.focus_timer_notification_rest)
        timer.startTimer(restTime, slogan,
                goal.value ?: resourceProvider.getText(R.string.focus_timer_notification_no_goal))
    }

    fun handleUpdateTaskGoal() {
        viewModelScope.launch {
            if (uiState.value != UiState.STOPPED || currentTask?.spendPomodorosCount == 0) {
                goal.value?.let {
                    updateTaskGoal(it)
                }
            } else {
                currentTask = null
                currentPomodoro = null
                estimatedPomodorosCount.value = DEFAULT_TASK_ESTIMATE
                spendPomodorosCount.value = 0
            }
        }
    }

    fun handleStartStopTimer() {
        if (uiState.value == UiState.STOPPED) {
            if (isWorkTime) {
                startTimerForWork()
            } else {
                startTimerForRest()
            }
        } else {
            timer.resumePauseTimer()
        }
    }

    fun handleCancelTimer() {
        if (uiState.value != UiState.STOPPED) {
            uiState.value = UiState.STOPPED
            this.timerSecondsPassed.value = 0L
            timer.stopTimer()
        }
    }

    fun handleStartEditGoalText() {
        editGoalTextEvent.value = Event(Unit)
    }

    private suspend fun checkGoalAndCreateTaskIfNeed() {
        goal.value?.let {
            createOrUpdateTask(it)
        } ?: run {
            errorEvent.value = Event(resourceProvider.getText(R.string.focus_timer_goal_error))
        }
    }

    private suspend fun createOrUpdateTask(goal: String) {
        try {
            currentTask?.let {
                if (it.goal != goal) {
                    // If task not have spend pomodoro's, then we just edit task
                    if (it.spendPomodorosCount == 0) {
                        Timber.d("Update task goal because spend counter is zero")
                        updateTaskGoalUseCase(it, goal)
                    } else {
                        Timber.d("Crate new task")
                        spendPomodorosCount.value = 0
                        currentTask = createFreeTaskUseCase(goal, estimatedPomodorosCount.value ?: DEFAULT_TASK_ESTIMATE)
                    }
                }
            } ?: run {
                Timber.d("Crate task")
                spendPomodorosCount.value = 0
                currentTask = createFreeTaskUseCase(goal, estimatedPomodorosCount.value ?: DEFAULT_TASK_ESTIMATE)
            }
        }
        catch (e: CreateTaskException) {
            Timber.e(e, e.message)
            errorEvent.value = Event(resourceProvider.getText(R.string.focus_timer_create_task_error))
        }
        catch (e: UpdateTaskException) {
            Timber.e(e, e.message)
            errorEvent.value = Event(resourceProvider.getText(R.string.focus_timer_update_task_error))
        }
    }

    private suspend fun updateTaskGoal(goal: String) {
        try {
            currentTask?.let {
                Timber.d("Update task goal")
                updateTaskGoalUseCase(it, goal)
            }
        }
        catch (e: UpdateTaskException) {
            Timber.e(e, e.message)
            errorEvent.value = Event(resourceProvider.getText(R.string.focus_timer_update_task_error))
        }
    }

    override fun onTimeChanged(timerSecondsPassed: Long) {
        this.timerSecondsPassed.value = timerSecondsPassed
    }

    override fun onTimerPaused() {
        uiState.value = UiState.PAUSED
    }

    override fun onTimerResumed() {
        uiState.value = if (isWorkTime) {
            UiState.STARTED_WORK
        } else {
            UiState.STARTED_REST
        }
    }

    override fun onTimerFinish() {
        uiState.value = UiState.STOPPED
        this.timerSecondsPassed.value = 0L

        viewModelScope.launch(Dispatchers.Main.immediate) {
            createPomodoro()
        }

        showNotification()
    }

    private suspend fun createPomodoro() {
        if (isWorkTime) {
            try {
                checkGoalAndCreateTaskIfNeed()
                currentTask?.let { it ->
                    Timber.d("Create pomodoro and increase spend counter in task")
                    currentPomodoro = createPomodoroUseCase(it, pomodoreTime)
                    spendPomodorosCount.value = increaseSpendPomodoroInTaskUseCase(it)
                    todayPomodoroCount.value = (todayPomodoroCount.value ?: 0) + 1
                }
            }
            catch (e: CreatePomodoroException) {
                Timber.e(e, e.message)
                errorEvent.value = Event(resourceProvider.getText(R.string.focus_timer_create_pomodoro_error))
            }
            catch (e: UpdateTaskException) {
                Timber.e(e, e.message)
                errorEvent.value = Event(resourceProvider.getText(R.string.focus_timer_update_task_error))
            }
        }
    }

    override fun onTimerCancel() {
        isWorkTime = true
        uiState.value = UiState.STOPPED
        this.timerSecondsPassed.value = 0L
    }

    override fun onCleared() {
        super.onCleared()
        notification.unregister()
        focusTimerService.stopService()
    }
}
