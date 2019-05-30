package ru.ischenko.roman.focustimer.data.repository

import ru.ischenko.roman.focustimer.data.datasource.local.dao.ProjectDao
import ru.ischenko.roman.focustimer.data.model.Project
import ru.ischenko.roman.focustimer.data.repository.converters.ProjectConverter

interface ProjectRepository {

    suspend fun getProjectById(id: Long): Project?

    suspend fun getProjectByName(projectName: String): Project?

    suspend fun createProject(project: Project)

    suspend fun saveProject(project: Project)

    suspend fun deleteProject(project: Project)
}

class ProjectRepositoryImpl(private val projectDao: ProjectDao,
                            private val projectConverter: ProjectConverter): ProjectRepository {

    override suspend fun getProjectById(id: Long): Project? {
        val projectEntity = projectDao.getById(id)
        return projectConverter.convert(projectEntity)
    }

    override suspend fun getProjectByName(projectName: String): Project? {
        val projectEntity = projectDao.getByName(projectName)
        return projectConverter.convert(projectEntity)
    }

    override suspend fun createProject(project: Project) {
        val projectEntity = projectConverter.convertBack(project)
        projectEntity?.let {
            project.id = projectDao.insert(it)
        }
    }

    override suspend fun saveProject(project: Project) {
        val projectEntity = projectConverter.convertBack(project)
        projectEntity?.let {
            projectDao.update(it)
        }
    }

    override suspend fun deleteProject(project: Project) {
        val projectEntity = projectConverter.convertBack(project)
        projectEntity?.let {
            projectDao.delete(it)
        }
    }

}