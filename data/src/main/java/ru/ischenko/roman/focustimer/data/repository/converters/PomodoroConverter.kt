package ru.ischenko.roman.focustimer.data.repository.converters

import ru.ischenko.roman.focustimer.data.datasource.local.dto.PomodoroDto
import ru.ischenko.roman.focustimer.data.datasource.local.dto.PomodoroEntity
import ru.ischenko.roman.focustimer.data.model.Pomodoro
import java.util.*

class PomodoroConverter(private val taskConverter: TaskConverter) {

    fun convert(pomodoroDto: PomodoroDto): Pomodoro {
        with (pomodoroDto.pomodoroEntity) {
            return Pomodoro(taskConverter.convert(pomodoroDto.taskDto), Date(completeDate),
                    pomodoroTime, goalAchieved, efficiencyMark, retrospectiveComment, distractionCount)
        }
    }

    fun convertBack(pomodoro: Pomodoro): PomodoroDto {
        with (pomodoro) {
            val pomodoroEntity = PomodoroEntity(id, completeDate.time, pomodoroTime, goalAchieved,
                    efficiencyMark, retrospectiveComment, distractionCount, pomodoro.task.id)
            val taskDto = taskConverter.convertBack(pomodoro.task)
            return PomodoroDto(pomodoroEntity, taskDto)
        }
    }
}