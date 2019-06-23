package ru.ischenko.roman.focustimer.data.datasource.local.dao

import androidx.room.*
import ru.ischenko.roman.focustimer.data.datasource.local.dto.PomodoroDto
import ru.ischenko.roman.focustimer.data.datasource.local.dto.PomodoroEntity
import java.util.*

@Dao
interface PomodoroDao {

    @Query("SELECT * FROM tasks, projects, pomodoros WHERE projectId = idProject AND taskId = idTask")
    suspend fun getAll(): List<PomodoroDto>

    @Query("SELECT * FROM tasks, projects, pomodoros WHERE projectId = idProject AND taskId = idTask AND idPomodoro = :id")
    suspend fun getById(id: Long): PomodoroDto

    @Query("SELECT COUNT(*) FROM pomodoros WHERE completeDate BETWEEN :beginDate AND :endDate")
    fun getCountInPeriod(beginDate: Date, endDate: Date): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pomodoro: PomodoroEntity): Long

    @Update
    suspend fun update(pomodoro: PomodoroEntity)

    @Delete
    suspend fun delete(pomodoro: PomodoroEntity)
}