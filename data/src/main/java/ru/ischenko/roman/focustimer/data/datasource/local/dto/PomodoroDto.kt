package ru.ischenko.roman.focustimer.data.datasource.local.dto

import androidx.room.Embedded

data class PomodoroDto(
        @Embedded val pomodoroEntity: PomodoroEntity,
        @Embedded val taskDto: TaskDto
)