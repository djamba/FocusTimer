package ru.ischenko.roman.focustimer.data.datasource.local.dto

import androidx.room.Embedded

data class TaskDto(
        @Embedded val taskEntity: TaskEntity,
        @Embedded val projectEntity: ProjectEntity?
)