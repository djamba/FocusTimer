package ru.ischenko.roman.focustimer.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ischenko.roman.focustimer.data.model.Task
import ru.ischenko.roman.focustimer.data.repository.TaskRepository
import ru.ischenko.roman.focustimer.domain.error.UpdateTaskException

class IncreaseSpendPomodoroInTaskUseCase(private val taskRepository: TaskRepository) {

    @Throws(UpdateTaskException::class)
    suspend operator fun invoke(task: Task): Int = withContext(Dispatchers.IO) {
        try {
            task.spendPomodorosCount++
            taskRepository.saveTask(task)
        }
        catch (e: Throwable) {
            throw UpdateTaskException("Can't increase spend pomodoro in task", e)
        }
        return@withContext task.spendPomodorosCount
    }
}