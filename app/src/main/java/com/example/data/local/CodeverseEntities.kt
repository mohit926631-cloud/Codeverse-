package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "code_projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val htmlCode: String,
    val cssCode: String,
    val jsCode: String,
    val templateTag: String,
    val updatedAtMillis: Long
)

@Entity(tableName = "completed_lessons")
data class CompletedLessonEntity(
    @PrimaryKey val lessonId: String,
    val worldId: String,
    val bestAccuracyPercent: Int,
    val completedAtMillis: Long
)
