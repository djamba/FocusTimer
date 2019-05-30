package ru.ischenko.roman.focustimer.data.datasource.local.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
        @PrimaryKey(autoGenerate = true) val idProject: Long,
        val projectName: String
)