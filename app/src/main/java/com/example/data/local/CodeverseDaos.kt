package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM code_projects ORDER BY updatedAtMillis DESC")
    fun observeAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM code_projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectById(projectId: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProject(project: ProjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(projects: List<ProjectEntity>)

    @Query("DELETE FROM code_projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: String)
}

@Dao
interface CompletedLessonDao {
    @Query("SELECT * FROM completed_lessons ORDER BY completedAtMillis DESC")
    fun observeCompletedLessons(): Flow<List<CompletedLessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCompletedLesson(entity: CompletedLessonEntity)

    @Query("DELETE FROM completed_lessons")
    suspend fun clearAll()
}
