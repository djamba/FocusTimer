package ru.ischenko.roman.focustimer.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.ischenko.roman.focustimer.data.model.Task
import ru.ischenko.roman.focustimer.data.repository.TaskRepository
import ru.ischenko.roman.focustimer.domain.error.CreateTaskException

class CreateFreeTaskUseCase(private val taskRepository: TaskRepository) {

    @Throws(CreateTaskException::class)
    suspend operator fun invoke(goal: String, estimatedPomodorosCount: Int): Task = withContext(Dispatchers.IO) {
        try {
            val task = Task(project = null, goal = goal, estimatedPomodorosCount = estimatedPomodorosCount,
                            spendPomodorosCount = 0, complete = false, scheduledTime = null)
            taskRepository.createTask(task)
            return@withContext task
        }
        catch (e: Throwable) {
            throw CreateTaskException("Can't create task", e)
        }
    }
}
