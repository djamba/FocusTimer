package ru.ischenko.roman.focustimer.reports.agenda.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ischenko.roman.focustimer.date.beginTime
import ru.ischenko.roman.focustimer.date.endTime
import ru.ischenko.roman.focustimer.date.nextDay
import ru.ischenko.roman.focustimer.date.prevDay
import ru.ischenko.roman.focustimer.reports.R
import ru.ischenko.roman.focustimer.reports.agenda.domain.GetPomodorosInPeriodUseCase
import ru.ischenko.roman.focustimer.reports.agenda.domain.error.GetPomodorosInPeriodException
import ru.ischenko.roman.focustimer.reports.agenda.presentation.convertor.PomodoroToPomodoroItemConverter
import ru.ischenko.roman.focustimer.reports.agenda.presentation.model.PomodoroItem
import ru.ischenko.roman.focustimer.utils.Event
import ru.ischenko.roman.focustimer.utils.ResourceProvider
import timber.log.Timber
import java.util.*

class AgendaViewModel(private val getPomodorosInPeriodUseCase: GetPomodorosInPeriodUseCase,
                      private val resourceProvider: ResourceProvider,
                      private val pomodoroToPomodoroItemConverter: PomodoroToPomodoroItemConverter) : ViewModel() {

    val date = MutableLiveData<Date>()
    val pomodoros: MutableLiveData<List<PomodoroItem>> = MutableLiveData()
    val errorEvent: MutableLiveData<Event<String>> = MutableLiveData()

    init {
        date.value = Date()
    }

    fun loadCurrentDayReport() = viewModelScope.launch {
        val selectedDate: Date = date.value ?: Date()
        try {
            pomodoros.value = getPomodorosInPeriodUseCase(selectedDate.beginTime(), selectedDate.endTime()).map { pomodoroToPomodoroItemConverter.convert(it) }
        } catch (e: GetPomodorosInPeriodException) {
            Timber.e(e, e.message)
            errorEvent.value = Event(resourceProvider.getText(R.string.agenda_error_load_pomodoros))
        }
    }

    fun loadPrevDayReport() {
        val selectedDate: Date = date.value ?: Date()
        date.value = selectedDate.prevDay()
        loadCurrentDayReport()
    }

    fun loadNextDayReport() {
        val selectedDate: Date = date.value ?: Date()
        date.value = selectedDate.nextDay()
        loadCurrentDayReport()
    }
}