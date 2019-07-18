package ru.ischenko.roman.focustimer.data.repository

import ru.ischenko.roman.focustimer.data.datasource.local.dao.PomodoroDao
import ru.ischenko.roman.focustimer.data.datasource.local.dao.TaskDao
import ru.ischenko.roman.focustimer.data.model.Pomodoro
import ru.ischenko.roman.focustimer.data.repository.converters.PomodoroConverter
import ru.ischenko.roman.focustimer.data.repository.converters.TaskConverter
import java.util.*

interface PomodoroRepository {

    suspend fun getPomodoroById(id: Long): Pomodoro

    suspend fun getCountPomodorosInPeriod(beginDate: Date, endDate: Date): Long

    suspend fun getPomodorosInPeriod(beginDate: Date, endDate: Date): List<Pomodoro>

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

    override suspend fun getCountPomodorosInPeriod(beginDate: Date, endDate: Date): Long {
        return pomodoroDao.getCountInPeriod(beginDate, endDate)
    }

    override suspend fun getPomodorosInPeriod(beginDate: Date, endDate: Date): List<Pomodoro> {
        val pomodoros = pomodoroDao.getPomodorosInPeriod(beginDate, endDate)
        return pomodoros.map { pomodoroConverter.convert(it) }
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