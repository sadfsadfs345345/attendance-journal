package com.attendance.app.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: Role,
    val groupId: String? = null,
    val token: String? = null
)
