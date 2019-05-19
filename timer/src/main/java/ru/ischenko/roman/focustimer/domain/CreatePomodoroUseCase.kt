package ru.ischenko.roman.focustimer.domain

import ru.ischenko.roman.focustimer.data.model.Pomodoro
import ru.ischenko.roman.focustimer.data.model.Task

class CreatePomodoroUseCase {

    operator fun invoke(task: Task, pomodoroTime: Long) : Pomodoro {
        val pomodoro = Pomodoro(task, pomodoroTime)
        return pomodoro
    }
}