package ru.ischenko.roman.focustimer.data.datasource.local.dto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "tasks",
        foreignKeys = [ForeignKey(entity = ProjectEntity::class,
                                  parentColumns = ["idProject"],
                                  childColumns = ["projectId"],
                                  onDelete = ForeignKey.CASCADE)])
data class TaskEntity(
        @PrimaryKey(autoGenerate = true) val idTask: Long,
        val goal: String,
        val estimatedPomodorosCount: Int,
        var spendPomodorosCount: Int,
        var complete: Boolean,
        val scheduledTime: Int?,
        val projectId: Long?
)