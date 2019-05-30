package ru.ischenko.roman.focustimer.data.repository.converters

import ru.ischenko.roman.focustimer.data.datasource.local.dto.ProjectEntity
import ru.ischenko.roman.focustimer.data.model.Project

class ProjectConverter {

    fun convert(projectEntity: ProjectEntity?): Project? {
        if (projectEntity == null) {
            return null
        }
        with (projectEntity) {
            return Project(projectName, idProject)
        }
    }

    fun convertBack(project: Project?): ProjectEntity? {
        if (project == null) {
            return null
        }
        with (project) {
            return ProjectEntity(id, projectName)
        }
    }
}