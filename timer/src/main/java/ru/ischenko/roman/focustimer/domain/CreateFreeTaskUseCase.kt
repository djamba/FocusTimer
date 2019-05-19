package ru.ischenko.roman.focustimer.domain

import ru.ischenko.roman.focustimer.data.model.Task

class CreateFreeTaskUseCase {

    operator fun invoke(goal: String) : Task {
        val task = Task(project = null, goal = goal, estimatedPomodorosCount = 1, scheduledTime = null)
        return task
    }
}
