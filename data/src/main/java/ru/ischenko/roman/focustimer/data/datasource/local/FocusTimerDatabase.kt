package ru.ischenko.roman.focustimer.data.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.ischenko.roman.focustimer.data.datasource.local.dao.PomodoroDao
import ru.ischenko.roman.focustimer.data.datasource.local.dao.ProjectDao
import ru.ischenko.roman.focustimer.data.datasource.local.dao.TaskDao
import ru.ischenko.roman.focustimer.data.datasource.local.dto.PomodoroEntity
import ru.ischenko.roman.focustimer.data.datasource.local.dto.ProjectEntity
import ru.ischenko.roman.focustimer.data.datasource.local.dto.TaskEntity

@TypeConverters(Converters::class)
@Database(entities = [ PomodoroEntity::class, TaskEntity::class, ProjectEntity::class ], version = 1)
abstract class FocusTimerDatabase : RoomDatabase() {

    abstract fun pomodoroDao(): PomodoroDao

    abstract fun taskDao(): TaskDao

    abstract fun projectDao(): ProjectDao

    companion object {
        fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext, FocusTimerDatabase::class.java, "FocusTimer.db")
                        .build()
    }
}