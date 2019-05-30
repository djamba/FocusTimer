package ru.ischenko.roman.focustimer.data.model

import java.util.*

data class Pomodoro(
        val task: Task,
        var completeDate: Date,
        var pomodoroTime: Long = 0,
        var goalAchieved: Boolean = false,
        var efficiencyMark: Int = 0,
        var retrospectiveComment: String? = null,
        var distractionCount: Int = 0,
        var id: Long = 0
)