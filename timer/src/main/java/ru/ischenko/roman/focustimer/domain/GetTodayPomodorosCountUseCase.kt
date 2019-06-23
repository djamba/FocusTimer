package ru.ischenko.roman.focustimer.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ischenko.roman.focustimer.data.repository.PomodoroRepository
import ru.ischenko.roman.focustimer.domain.error.CreatePomodoroException
import ru.ischenko.roman.focustimer.domain.error.GetPomodoroException
import java.util.*

class GetTodayPomodorosCountUseCase(private val repository: PomodoroRepository) {

    @Throws(CreatePomodoroException::class)
    suspend operator fun invoke(): Long = withContext(Dispatchers.IO) {
        try {
            return@withContext repository.getPomodorosByPeriod(getBeginToday(), getEndToday())
        }
        catch (e: Throwable) {
            throw GetPomodoroException("Can't get pomodoro", e)
        }
    }

    private fun getBeginToday(): Date {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        return cal.time
    }

    private fun getEndToday(): Date {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }
        return cal.time
    }
}