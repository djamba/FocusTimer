package ru.ischenko.roman.focustimer.data.repository.converters

import ru.ischenko.roman.focustimer.data.datasource.local.dto.TaskDto
import ru.ischenko.roman.focustimer.data.datasource.local.dto.TaskEntity
import ru.ischenko.roman.focustimer.data.model.Task

class TaskConverter(private val projectConverter: ProjectConverter) {

    fun convert(taskDto: TaskDto): Task {
        with (taskDto.taskEntity) {
            return Task(projectConverter.convert(taskDto.projectEntity), goal, estimatedPomodorosCount, spendPomodorosCount,
                    complete, scheduledTime, idTask)
        }
    }

    fun convertBack(task: Task): TaskDto {
        with (task) {
            val taskEntity = TaskEntity(id, goal, estimatedPomodorosCount, spendPomodorosCount,
                    complete, scheduledTime, task.project?.id)
            val projectEntity = projectConverter.convertBack(task.project)
            return TaskDto(taskEntity, projectEntity)
        }
    }
}