package com.attendance.app.domain.model

data class Student(
    val id: String,
    val fullName: String,
    val groupId: String,
    val isExpelled: Boolean = false,
    val expelledAt: Long? = null
)
