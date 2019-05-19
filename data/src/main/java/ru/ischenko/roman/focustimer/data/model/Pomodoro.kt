package ru.ischenko.roman.focustimer.data.model

data class Pomodoro(
    val task: Task,
    val pomodoroTime: Long = 0,
    val goalAchieved: Boolean = false,
    val efficiencyMark: Int = 0,
    val retrospectiveComment: String? = null,
    val distractionCount: Int = 0
)