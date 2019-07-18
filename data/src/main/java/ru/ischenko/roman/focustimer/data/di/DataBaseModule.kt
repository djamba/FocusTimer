package ru.ischenko.roman.focustimer.data.di

import dagger.Module
import dagger.Provides
import ru.ischenko.roman.focustimer.data.datasource.local.FocusTimerDatabase
import ru.ischenko.roman.focustimer.data.repository.PomodoroRepository
import ru.ischenko.roman.focustimer.data.repository.PomodoroRepositoryImpl
import ru.ischenko.roman.focustimer.data.repository.TaskRepository
import ru.ischenko.roman.focustimer.data.repository.TaskRepositoryImpl
import ru.ischenko.roman.focustimer.data.repository.converters.PomodoroConverter
import ru.ischenko.roman.focustimer.data.repository.converters.ProjectConverter
import ru.ischenko.roman.focustimer.data.repository.converters.TaskConverter

@Module
class DataBaseModule {

    @Provides
    fun provideTaskConverter() : TaskConverter =
            TaskConverter(ProjectConverter())

    @Provides
    fun providePomodoroConverter(taskConverter: TaskConverter) : PomodoroConverter =
            PomodoroConverter(taskConverter)

    @Provides
    fun providePomodoroRepository(database: FocusTimerDatabase,
                                  pomodoroConverter: PomodoroConverter,
                                  taskConverter: TaskConverter) : PomodoroRepository =
            PomodoroRepositoryImpl(database.pomodoroDao(), pomodoroConverter, database.taskDao(), taskConverter)

    @Provides
    fun provideTaskRepository(database: FocusTimerDatabase,
                              taskConverter: TaskConverter) : TaskRepository =
            TaskRepositoryImpl(database.taskDao(), taskConverter)
}