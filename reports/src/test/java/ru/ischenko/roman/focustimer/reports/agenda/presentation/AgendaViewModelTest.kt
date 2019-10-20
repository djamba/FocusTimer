package ru.ischenko.roman.focustimer.reports.agenda.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import ru.ischenko.roman.focustimer.data.model.Pomodoro
import ru.ischenko.roman.focustimer.data.model.Project
import ru.ischenko.roman.focustimer.data.model.Task
import ru.ischenko.roman.focustimer.date.beginTime
import ru.ischenko.roman.focustimer.date.endTime
import ru.ischenko.roman.focustimer.date.nextDay
import ru.ischenko.roman.focustimer.date.prevDay
import ru.ischenko.roman.focustimer.reports.R
import ru.ischenko.roman.focustimer.reports.agenda.domain.GetPomodorosInPeriodUseCase
import ru.ischenko.roman.focustimer.reports.agenda.domain.error.GetPomodorosInPeriodException
import ru.ischenko.roman.focustimer.reports.agenda.presentation.convertor.PomodoroToPomodoroItemConverter
import ru.ischenko.roman.focustimer.utils.Event
import ru.ischenko.roman.focustimer.utils.ResourceProvider
import java.util.*

class AgendaViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val pomodoros: List<Pomodoro> = listOf(
            Pomodoro(
                Task(
                    Project("Project 1", 1),
                        "Goal 1", 1, 1, false, 1), Date())
    )

    private val getPomodorosInPeriodUseCase = mockk<GetPomodorosInPeriodUseCase>()
    private val resourceProvider = mockk<ResourceProvider>()
    private val pomodoroToPomodoroItemConverter = PomodoroToPomodoroItemConverter()

    private lateinit var viewModel: AgendaViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        coEvery { getPomodorosInPeriodUseCase(any(), any()) } returns pomodoros

        viewModel = AgendaViewModel(getPomodorosInPeriodUseCase, resourceProvider, pomodoroToPomodoroItemConverter)
    }

    @Test
    fun `WHEN load today report THEN load pomodores for current date`() {

        viewModel.loadCurrentDayReport()

        val beginDate = slot<Date>()
        val endDate = slot<Date>()
        coVerify { getPomodorosInPeriodUseCase(capture(beginDate), capture(endDate)) }
        assertEquals(beginDate.captured.toString(), Date().beginTime().toString())
        assertEquals(endDate.captured.toString(), Date().endTime().toString())

        assertEquals(viewModel.pomodoros.value, pomodoros.map { pomodoroToPomodoroItemConverter.convert(it) })
    }

    @Test
    fun `WHEN error load report THEN show error`() {

        every { resourceProvider.getText(R.string.agenda_error_load_pomodoros) } returns ERROR_MSG
        coEvery { getPomodorosInPeriodUseCase(any(), any()) } throws GetPomodorosInPeriodException("", null)

        viewModel.loadCurrentDayReport()

        coVerify { getPomodorosInPeriodUseCase(any(), any()) }
        assertEquals(Event(ERROR_MSG), viewModel.errorEvent.value)
    }

    @Test
    fun `WHEN load prev report THEN load pomodores for prev date`() {

        viewModel.loadPrevDayReport()

        val beginDate = slot<Date>()
        val endDate = slot<Date>()
        coVerify { getPomodorosInPeriodUseCase(capture(beginDate), capture(endDate)) }
        assertEquals(beginDate.captured.toString(), Date().prevDay().beginTime().toString())
        assertEquals(endDate.captured.toString(), Date().prevDay().endTime().toString())

        assertEquals(viewModel.pomodoros.value, pomodoros.map { pomodoroToPomodoroItemConverter.convert(it) })
    }

    @Test
    fun `WHEN load next report THEN load pomodores for next date`() {

        viewModel.loadNextDayReport()

        val beginDate = slot<Date>()
        val endDate = slot<Date>()
        coVerify { getPomodorosInPeriodUseCase(capture(beginDate), capture(endDate)) }
        assertEquals(beginDate.captured.toString(), Date().nextDay().beginTime().toString())
        assertEquals(endDate.captured.toString(), Date().nextDay().endTime().toString())

        assertEquals(viewModel.pomodoros.value, pomodoros.map { pomodoroToPomodoroItemConverter.convert(it) })
    }

    companion object TestData {
        private const val ERROR_MSG = "ERROR_MSG"
    }
}