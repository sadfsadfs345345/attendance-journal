package com.attendance.app.domain.model

enum class Role(val displayName: String, val priority: Int) {
    HEADMAN("Староста", 1),
    TEACHER("Учитель", 2),
    CURATOR("Куратор", 3),
    DIRECTOR("Директор", 4);

    fun canApprove(other: Role): Boolean = this.priority > other.priority
}
