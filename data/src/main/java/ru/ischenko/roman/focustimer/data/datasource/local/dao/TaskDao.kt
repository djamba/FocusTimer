package ru.ischenko.roman.focustimer.data.datasource.local.dao

import androidx.room.*
import ru.ischenko.roman.focustimer.data.datasource.local.dto.TaskDto
import ru.ischenko.roman.focustimer.data.datasource.local.dto.TaskEntity

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks, projects WHERE projectId = idProject")
    suspend fun getAll(): List<TaskDto>

    @Query("SELECT * FROM tasks, projects WHERE projectId = idProject AND idTask = :id")
    suspend fun getById(id: Long): TaskDto

    @Query("SELECT * FROM tasks, projects WHERE projectId = idProject AND goal = :goal")
    suspend fun getByGoal(goal: String): TaskDto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)
}