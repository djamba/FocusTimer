package ru.ischenko.roman.focustimer.data.datasource.local

import androidx.room.TypeConverter
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? =
        value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? =
        date?.time
}