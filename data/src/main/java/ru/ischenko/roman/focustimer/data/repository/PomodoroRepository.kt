package ru.ischenko.roman.focustimer.data.repository

import ru.ischenko.roman.focustimer.data.datasource.local.dao.PomodoroDao
import ru.ischenko.roman.focustimer.data.datasource.local.dao.TaskDao
import ru.ischenko.roman.focustimer.data.model.Pomodoro
import ru.ischenko.roman.focustimer.data.repository.converters.PomodoroConverter
import ru.ischenko.roman.focustimer.data.repository.converters.TaskConverter

interface PomodoroRepository {

    suspend fun getPomodoroById(id: Long): Pomodoro

    suspend fun createPomodoro(pomodoro: Pomodoro)

    suspend fun savePomodoro(pomodoro: Pomodoro)

    suspend fun deletePomodoro(pomodoro: Pomodoro)
}

class PomodoroRepositoryImpl(private val pomodoroDao: PomodoroDao,
                             private val pomodoroConverter: PomodoroConverter,
                             private val taskDao: TaskDao,
                             private val taskConverter: TaskConverter) : PomodoroRepository {

    override suspend fun getPomodoroById(id: Long): Pomodoro {
        val pomodoroDto = pomodoroDao.getById(id)
        return pomodoroConverter.convert(pomodoroDto)
    }

    override suspend fun createPomodoro(pomodoro: Pomodoro) {
        val pomodoroDto = pomodoroConverter.convertBack(pomodoro)
        pomodoro.id = pomodoroDao.insert(pomodoroDto.pomodoroEntity)
    }

    override suspend fun savePomodoro(pomodoro: Pomodoro) {
        val pomodoroDto = pomodoroConverter.convertBack(pomodoro)
        pomodoroDao.update(pomodoroDto.pomodoroEntity)
    }

    override suspend fun deletePomodoro(pomodoro: Pomodoro) {
        val pomodoroDto = pomodoroConverter.convertBack(pomodoro)
        pomodoroDao.delete(pomodoroDto.pomodoroEntity)
    }
}