package ru.ischenko.roman.focustimer.data.model

data class Task(
        var project: Project?,
        var goal: String,
        var estimatedPomodorosCount: Int,
        var spendPomodorosCount: Int,
        var complete: Boolean,
        var scheduledTime: Int?,
        var id: Long = 0
)