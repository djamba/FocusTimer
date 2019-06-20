package ru.ischenko.roman.focustimer.timer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import ru.ischenko.roman.focustimer.data.model.Pomodoro
import ru.ischenko.roman.focustimer.data.model.Task
import ru.ischenko.roman.focustimer.domain.CreateFreeTaskUseCase
import ru.ischenko.roman.focustimer.domain.CreatePomodoroUseCase
import ru.ischenko.roman.focustimer.domain.IncreaseSpendPomodoroInTaskUseCase
import ru.ischenko.roman.focustimer.domain.UpdateTaskGoalUseCase
import ru.ischenko.roman.focustimer.domain.error.CreatePomodoroException
import ru.ischenko.roman.focustimer.domain.error.CreateTaskException
import ru.ischenko.roman.focustimer.domain.error.UpdateTaskException
import ru.ischenko.roman.focustimer.notification.*
import ru.ischenko.roman.focustimer.presentation.FocusTimerViewModel
import ru.ischenko.roman.focustimer.presentation.UiState
import ru.ischenko.roman.focustimer.utils.Event
import ru.ischenko.roman.focustimer.utils.ResourceProvider
import java.util.concurrent.TimeUnit

class FocusTimerViewModelTest {

    @get:Rule var rule: TestRule = InstantTaskExecutorRule()

    private val task = mockk<Task>(relaxed = true)
    private val pomodoro = mockk<Pomodoro>(relaxed = true)

    private val timer = mockk<FocusTimerController>(relaxed = true)
    private val notification = mockk<FocusTimerNotification>(relaxed = true)
    private val notificationService = mockk<FocusTimerServiceMediator>(relaxed = true)
    private val resourceProvider = mockk<ResourceProvider>()

    private val createPomodoroUseCase = mockk<CreatePomodoroUseCase>()
    private val createFreeTaskUseCase = mockk<CreateFreeTaskUseCase>()
    private val increaseSpendPomodoroInTaskUseCase = mockk<IncreaseSpendPomodoroInTaskUseCase>()
    private val updateTaskGoalUseCase = mockk<UpdateTaskGoalUseCase>()

    private lateinit var focusTimerViewModel: FocusTimerViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        every { resourceProvider.getText(R.string.focus_timer_rest) } returns REST
        every { resourceProvider.getText(R.string.focus_timer_work) } returns WORK
        every { resourceProvider.getText(R.string.focus_timer_notification_rest) } returns REST_NOTIFICATION
        every { resourceProvider.getText(R.string.focus_timer_notification_focus_on_work) } returns WORK_NOTIFICATION
        every { resourceProvider.getText(R.string.focus_timer_notification_no_goal) } returns GOAL

        coEvery { createFreeTaskUseCase.invoke(any(), any()) } returns task
        coEvery { createPomodoroUseCase.invoke(any(), any()) } returns pomodoro
        coEvery { updateTaskGoalUseCase.invoke(any(), any()) } returns Unit
        coEvery { increaseSpendPomodoroInTaskUseCase.invoke(any()) } returns SPEND_POMODOROS

        focusTimerViewModel = FocusTimerViewModel(timer, notification, notificationService,
                resourceProvider, createPomodoroUseCase, createFreeTaskUseCase, increaseSpendPomodoroInTaskUseCase, updateTaskGoalUseCase)
    }

    @Test
    fun `WHEN timer is stopped and now is work interval and user start button pressed THEN start timer for work`() {

        every { resourceProvider.getText(R.string.focus_timer_notification_focus_on_work) } returns SLAGON_WORK

        focusTimerViewModel.handleStartStopTimer()

        assertEquals(focusTimerViewModel.uiState.value, UiState.STARTED_WORK)
        assertEquals(focusTimerViewModel.startTimerEvent.value?.peekContent(), WORK_TIME)

        verify { timer.startTimer(WORK_TIME, SLAGON_WORK, focusTimerViewModel.goal.value!!) }
        verify { resourceProvider.getText(R.string.focus_timer_notification_focus_on_work) }
    }

    @Test
    fun `WHEN timer is stopped and now is rest interval and user start button pressed THEN start timer for rest`() {

        every { resourceProvider.getText(R.string.focus_timer_notification_rest) } returns SLAGON_REST
        focusTimerViewModel.onTimerFinish()

        focusTimerViewModel.handleStartStopTimer()

        assertEquals(focusTimerViewModel.uiState.value, UiState.STARTED_REST)
        assertEquals(focusTimerViewModel.startTimerEvent.value?.peekContent(), REST_TIME)

        verify { timer.startTimer(REST_TIME, SLAGON_REST, focusTimerViewModel.goal.value!!) }
        verify { resourceProvider.getText(R.string.focus_timer_notification_rest) }
    }

    @Test
    fun `WHEN timer is running and user start button pressed THEN pause or resume timer`() {

        focusTimerViewModel.uiState.value = UiState.STARTED_WORK

        focusTimerViewModel.handleStartStopTimer()

        verify { timer.resumePauseTimer() }
    }

    @Test
    fun `WHEN timer is running and task is created and user update task goal THEN save new task goal`() {

        focusTimerViewModel.onTimerFinish()
        focusTimerViewModel.uiState.value = UiState.STARTED_WORK

        focusTimerViewModel.handleUpdateTaskGoal()

        assertEquals(focusTimerViewModel.spendPomodorosCount.value, SPEND_POMODOROS)
        coVerify { updateTaskGoalUseCase.invoke(any(), focusTimerViewModel.goal.value!!) }
    }

    @Test
    fun `WHEN timer is running and task is created and user update task goal and update fail THEN show error`() {

        focusTimerViewModel.onTimerFinish()
        focusTimerViewModel.uiState.value = UiState.STARTED_WORK
        every { resourceProvider.getText(R.string.focus_timer_update_task_error) } returns ERROR
        coEvery { updateTaskGoalUseCase.invoke(any(), any()) } throws UpdateTaskException("", null)

        focusTimerViewModel.handleUpdateTaskGoal()

        assertEquals(focusTimerViewModel.errorEvent.value, Event(ERROR))
    }

    @Test
    fun `WHEN timer is stopped and user update task goal THEN not change goal for current task`() {

        focusTimerViewModel.uiState.value = UiState.STOPPED

        focusTimerViewModel.handleUpdateTaskGoal()

        assertEquals(focusTimerViewModel.spendPomodorosCount.value, 0)
        assertEquals(focusTimerViewModel.estimatedPomodorosCount.value, DEFAULT_TASK_ESTIMATE)
        coVerify(exactly = 0) { updateTaskGoalUseCase.invoke(any(), focusTimerViewModel.goal.value!!) }
    }

    @Test
    fun `WHEN timer is running and user stop timer THEN cancel pomodoro`() {

        focusTimerViewModel.uiState.value = UiState.STARTED_WORK

        focusTimerViewModel.handleCancelTimer()

        assertEquals(focusTimerViewModel.uiState.value, UiState.STOPPED)
        assertEquals(focusTimerViewModel.timerSecondsPassed.value, 0L)
        verify { timer.stopTimer() }
    }

    @Test
    fun `WHEN user init edit goal THEN send event for open dialog`() {

        focusTimerViewModel.handleStartEditGoalText()

        assertNotNull(focusTimerViewModel.editGoalTextEvent.value)
    }

    @Test
    fun `WHEN timer tik THEN show new timer passed time`() {

        val timerSecondsPassed = 1000L

        focusTimerViewModel.onTimeChanged(timerSecondsPassed)

        assertEquals(focusTimerViewModel.timerSecondsPassed.value, timerSecondsPassed)
    }

    @Test
    fun `WHEN timer paused THEN show paused state`() {

        focusTimerViewModel.onTimerPaused()

        assertEquals(focusTimerViewModel.uiState.value, UiState.PAUSED)
    }

    @Test
    fun `WHEN timer resumed THEN show started state`() {

        focusTimerViewModel.onTimerResumed()

        assertEquals(focusTimerViewModel.uiState.value, UiState.STARTED_WORK)
    }

    @Test
    fun `WHEN timer canceled THEN show started state`() {

        focusTimerViewModel.onTimerCancel()

        assertEquals(focusTimerViewModel.uiState.value, UiState.STOPPED)
        assertEquals(focusTimerViewModel.timerSecondsPassed.value, 0L)
    }

    @Test
    fun `WHEN timer finished THEN stop timer and save passed pomodoro`() {

        every { resourceProvider.getText(R.string.focus_timer_notification_rest) } returns SLAGON_REST

        focusTimerViewModel.onTimerFinish()

        assertEquals(focusTimerViewModel.uiState.value, UiState.STOPPED)
        assertEquals(focusTimerViewModel.timerSecondsPassed.value, 0L)

        coVerify { createFreeTaskUseCase.invoke(focusTimerViewModel.goal.value!!, DEFAULT_TASK_ESTIMATE) }
        coVerify { createPomodoroUseCase.invoke(any(), WORK_TIME) }
        coVerify { increaseSpendPomodoroInTaskUseCase.invoke(any()) }
    }

    @Test
    fun `WHEN timer finished THEN spend pomodoro increased`() {

        every { resourceProvider.getText(R.string.focus_timer_notification_rest) } returns SLAGON_REST

        focusTimerViewModel.onTimerFinish()

        assertEquals(focusTimerViewModel.spendPomodorosCount.value, SPEND_POMODOROS)
        coVerify { increaseSpendPomodoroInTaskUseCase.invoke(any()) }
    }

    @Test
    fun `WHEN work timer finished THEN show rest notification`() {

        val actions = listOf(CancelAction, CustomAction(ACTION_REST, REST))

        focusTimerViewModel.onTimerFinish()

        verify { notification.notify(REST_NOTIFICATION, focusTimerViewModel.goal.value!!, false, true, actions) }
    }

    @Test
    fun `WHEN rest timer finished THEN show work notification`() {

        val actions = listOf(CancelAction, CustomAction(ACTION_WORK, WORK))
        focusTimerViewModel.onTimerFinish() // work timer finished

        focusTimerViewModel.onTimerFinish()

        verify { notification.notify(WORK_NOTIFICATION, focusTimerViewModel.goal.value!!, false, true, actions) }
    }

    @Test
    fun `WHEN timer finished and goal is not set THEN do not save pomodoro and show error`() {

        every { resourceProvider.getText(R.string.focus_timer_goal_error) } returns ERROR
        focusTimerViewModel.goal.value = null

        focusTimerViewModel.onTimerFinish()

        assertEquals(focusTimerViewModel.errorEvent.value, Event(ERROR))

        coVerify(exactly = 0) { createFreeTaskUseCase.invoke(any(), DEFAULT_TASK_ESTIMATE) }
        coVerify(exactly = 0) { createPomodoroUseCase.invoke(any(), any()) }
        coVerify(exactly = 0) { increaseSpendPomodoroInTaskUseCase.invoke(any()) }
    }

    @Test
    fun `WHEN timer finished and occurred error until create task THEN do not save pomodoro and show error`() {

        every { resourceProvider.getText(R.string.focus_timer_create_task_error) } returns ERROR
        coEvery { createFreeTaskUseCase.invoke(any(), DEFAULT_TASK_ESTIMATE) } throws CreateTaskException("", null)

        focusTimerViewModel.onTimerFinish()

        assertEquals(focusTimerViewModel.errorEvent.value, Event(ERROR))

        coVerify { createFreeTaskUseCase.invoke(focusTimerViewModel.goal.value!!, DEFAULT_TASK_ESTIMATE) }
        coVerify(exactly = 0) { createPomodoroUseCase.invoke(any(), any()) }
        coVerify(exactly = 0) { increaseSpendPomodoroInTaskUseCase.invoke(any()) }
    }

    @Test
    fun `WHEN timer finished and occurred error until create pomodoro THEN do not save pomodoro and show error`() {

        every { resourceProvider.getText(R.string.focus_timer_create_pomodoro_error) } returns ERROR
        coEvery { createPomodoroUseCase.invoke(any(), any()) } throws CreatePomodoroException("", null)

        focusTimerViewModel.onTimerFinish()

        assertEquals(focusTimerViewModel.errorEvent.value, Event(ERROR))

        coVerify { createPomodoroUseCase.invoke(any(), WORK_TIME) }
        coVerify(exactly = 0) { increaseSpendPomodoroInTaskUseCase.invoke(any()) }
    }

    companion object TestData {
        private const val DEFAULT_TASK_ESTIMATE = 4
        private const val SPEND_POMODOROS = 3
        private const val REST = "REST"
        private const val REST_NOTIFICATION = "REST_NOTIFICATION"
        private const val WORK = "WORK"
        private const val WORK_NOTIFICATION = "WORK_NOTIFICATION"
        private const val GOAL = "GOAL"
        private const val SLAGON_WORK = "SLAGON_WORK"
        private const val SLAGON_REST = "SLAGON_REST"
        private const val ACTION_REST = "ACTION_REST"
        private const val ACTION_WORK = "ACTION_WORK"
        private const val ERROR = "ERROR"
        private val WORK_TIME = TimeUnit.MINUTES.toSeconds(25L)
        private val REST_TIME = TimeUnit.MINUTES.toSeconds(5L)
    }
}