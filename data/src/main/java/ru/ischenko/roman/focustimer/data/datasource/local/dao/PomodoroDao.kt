package ru.ischenko.roman.focustimer.data.datasource.local.dao

import androidx.room.*
import ru.ischenko.roman.focustimer.data.datasource.local.dto.PomodoroDto
import ru.ischenko.roman.focustimer.data.datasource.local.dto.PomodoroEntity

@Dao
interface PomodoroDao {

    @Query("SELECT * FROM tasks, projects, pomodoros WHERE projectId = idProject AND taskId = idTask")
    suspend fun getAll(): List<PomodoroDto>

    @Query("SELECT * FROM tasks, projects, pomodoros WHERE projectId = idProject AND taskId = idTask AND idPomodoro = :id")
    suspend fun getById(id: Long): PomodoroDto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pomodoro: PomodoroEntity): Long

    @Update
    suspend fun update(pomodoro: PomodoroEntity)

    @Delete
    suspend fun delete(pomodoro: PomodoroEntity)
}