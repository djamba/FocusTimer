package ru.ischenko.roman.focustimer.data.repository

import ru.ischenko.roman.focustimer.data.datasource.local.dao.TaskDao
import ru.ischenko.roman.focustimer.data.model.Task
import ru.ischenko.roman.focustimer.data.repository.converters.TaskConverter

interface TaskRepository {

    suspend fun getTaskById(id: Long): Task

    suspend fun getTaskByGoal(goal: String): Task

    suspend fun createTask(task: Task)

    suspend fun saveTask(task: Task)

    suspend fun deleteTask(task: Task)
}

class TaskRepositoryImpl(private val taskDao: TaskDao,
                         private val taskConverter: TaskConverter): TaskRepository {

    override suspend fun getTaskById(id: Long): Task {
        val taskDto = taskDao.getById(id)
        return taskConverter.convert(taskDto)
    }

    override suspend fun getTaskByGoal(goal: String): Task {
        val taskDto = taskDao.getByGoal(goal)
        return taskConverter.convert(taskDto)
    }

    override suspend fun createTask(task: Task) {
        val taskDto = taskConverter.convertBack(task)
        task.id = taskDao.insert(taskDto.taskEntity)
    }

    override suspend fun saveTask(task: Task) {
        val taskDto = taskConverter.convertBack(task)
        taskDao.update(taskDto.taskEntity)
    }

    override suspend fun deleteTask(task: Task) {
        val taskDto = taskConverter.convertBack(task)
        taskDao.delete(taskDto.taskEntity)
    }

}