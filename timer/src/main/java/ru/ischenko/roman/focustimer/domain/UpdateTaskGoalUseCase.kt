package ru.ischenko.roman.focustimer.domain

import ru.ischenko.roman.focustimer.data.model.Task

class UpdateTaskGoalUseCase {

    operator fun invoke(task: Task, goal: String) {
        task.goal = goal

    }
}