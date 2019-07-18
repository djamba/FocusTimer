package ru.ischenko.roman.focustimer.data.datasource.local.dao

import androidx.room.*
import ru.ischenko.roman.focustimer.data.datasource.local.dto.PomodoroDto
import ru.ischenko.roman.focustimer.data.datasource.local.dto.PomodoroEntity
import java.util.*

@Dao
interface PomodoroDao {

    @Query("""SELECT * FROM pomodoros
                    INNER JOIN tasks ON taskId = idTask
                    LEFT OUTER JOIN projects ON projectId = idProject""")
    suspend fun getAll(): List<PomodoroDto>

    @Query("""SELECT * FROM pomodoros
                    INNER JOIN tasks ON taskId = idTask
                    LEFT OUTER JOIN projects ON projectId = idProject
                    WHERE idPomodoro = :id""")
    suspend fun getById(id: Long): PomodoroDto

    @Query("SELECT COUNT(*) FROM pomodoros WHERE completeDate BETWEEN :beginDate AND :endDate")
    fun getCountInPeriod(beginDate: Date, endDate: Date): Long

    @Query("""SELECT * FROM pomodoros
                    INNER JOIN tasks ON taskId = idTask
                    LEFT OUTER JOIN projects ON projectId = idProject
                    WHERE completeDate BETWEEN :beginDate AND :endDate""")
    fun getPomodorosInPeriod(beginDate: Date, endDate: Date): List<PomodoroDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pomodoro: PomodoroEntity): Long

    @Update
    suspend fun update(pomodoro: PomodoroEntity)

    @Delete
    suspend fun delete(pomodoro: PomodoroEntity)
}