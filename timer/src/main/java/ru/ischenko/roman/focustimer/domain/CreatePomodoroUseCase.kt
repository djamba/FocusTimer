package ru.ischenko.roman.focustimer.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ischenko.roman.focustimer.data.model.Pomodoro
import ru.ischenko.roman.focustimer.data.model.Task
import ru.ischenko.roman.focustimer.data.repository.PomodoroRepository
import ru.ischenko.roman.focustimer.domain.error.CreatePomodoroException
import java.util.*

class CreatePomodoroUseCase(private val repository: PomodoroRepository) {

    @Throws(CreatePomodoroException::class)
    suspend operator fun invoke(task: Task, pomodoroTime: Long): Pomodoro = withContext(Dispatchers.IO) {
        try {
            val pomodoro = Pomodoro(task, Date(), pomodoroTime)
            repository.createPomodoro(pomodoro)
            return@withContext pomodoro
        }
        catch (e: Throwable) {
            throw CreatePomodoroException("Can't create pomodoro", e)
        }
    }
}