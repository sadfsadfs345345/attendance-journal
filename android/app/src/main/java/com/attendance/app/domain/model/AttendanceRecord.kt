package com.attendance.app.domain.model

data class AttendanceRecord(
    val id: String,
    val studentId: String,
    val lessonId: String,
    val status: AttendanceStatus,
    val markedByRole: Role,
    val approvalChain: List<ApprovalStep> = emptyList(),
    val note: String? = null,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class ApprovalStep(
    val role: Role,
    val approved: Boolean,
    val comment: String? = null,
    val timestampMillis: Long
)
