package ru.ischenko.roman.focustimer.data.model

data class Task(
    var project: Project?,
    var goal: String,
    var estimatedPomodorosCount: Int,
    var scheduledTime: Int?
)