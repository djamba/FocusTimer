package ru.ischenko.roman.focustimer.data.datasource.local.dto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoros",
        foreignKeys = [ForeignKey(entity = TaskEntity::class,
                                  parentColumns = ["idTask"],
                                  childColumns = ["taskId"],
                                  onDelete = CASCADE)])
data class PomodoroEntity(
        @PrimaryKey(autoGenerate = true) val idPomodoro: Long,
        val completeDate: Long,
        val pomodoroTime: Long = 0,
        val goalAchieved: Boolean = false,
        val efficiencyMark: Int = 0,
        val retrospectiveComment: String? = null,
        val distractionCount: Int = 0,
        val taskId: Long
)