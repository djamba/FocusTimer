package ru.ischenko.roman.focustimer.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ischenko.roman.focustimer.data.repository.PomodoroRepository
import ru.ischenko.roman.focustimer.date.getBeginToday
import ru.ischenko.roman.focustimer.date.getEndToday
import ru.ischenko.roman.focustimer.domain.error.GetPomodoroException

class GetTodayPomodorosCountUseCase(private val repository: PomodoroRepository) {

    @Throws(GetPomodoroException::class)
    suspend operator fun invoke(): Long = withContext(Dispatchers.IO) {
        try {
            return@withContext repository.getCountPomodorosInPeriod(getBeginToday(), getEndToday())
        }
        catch (e: Throwable) {
            throw GetPomodoroException("Can't get pomodoro", e)
        }
    }
}