package ru.ischenko.roman.focustimer.reports.agenda.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ischenko.roman.focustimer.data.model.Pomodoro
import ru.ischenko.roman.focustimer.data.repository.PomodoroRepository
import ru.ischenko.roman.focustimer.reports.agenda.domain.error.GetPomodorosInPeriodException
import java.util.*

class GetPomodorosInPeriodUseCase(private val repository: PomodoroRepository) {

    @Throws(GetPomodorosInPeriodException::class)
    suspend operator fun invoke(beginDate: Date, endDate: Date): List<Pomodoro> = withContext(Dispatchers.IO) {
        try {
            return@withContext repository.getPomodorosInPeriod(beginDate, endDate)
        }
        catch (e: Throwable) {
            throw GetPomodorosInPeriodException("Can't get pomodoros in period", e)
        }
    }
}