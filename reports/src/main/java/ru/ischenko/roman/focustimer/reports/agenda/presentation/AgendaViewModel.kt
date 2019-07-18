package ru.ischenko.roman.focustimer.reports.agenda.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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
                      private val PomodoroToPomodoroItemConverter: PomodoroToPomodoroItemConverter) : ViewModel() {

    val pomodoros: MutableLiveData<List<PomodoroItem>> = MutableLiveData()
    val errorEvent: MutableLiveData<Event<String>> = MutableLiveData()

    fun loadPomodorosInPeriod(beginDate: Date, endDate: Date) {
        try {
            viewModelScope.launch {
                pomodoros.value = getPomodorosInPeriodUseCase(beginDate, endDate).
                                    map { PomodoroToPomodoroItemConverter.convert(it) }
            }
        }
        catch (e: GetPomodorosInPeriodException) {
            Timber.e(e, e.message)
            errorEvent.value = Event(resourceProvider.getText(R.string.agenda_error_load_pomodoros))
        }
    }
}