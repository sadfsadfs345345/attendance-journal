package com.attendance.app.domain.usecase

import android.content.Context
import androidx.core.content.FileProvider
import com.attendance.app.domain.model.Statistics
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

class ExportCsvUseCase @Inject constructor(
    private val context: Context
) {
    fun invoke(stats: List<Statistics>, groupId: String): android.net.Uri {
        val file = File(context.cacheDir, "attendance_${groupId}.csv")
        val writer = FileWriter(file)
        val printer = CSVPrinter(
            writer,
            CSVFormat.DEFAULT.builder()
                .setHeader("Student", "Total", "Present", "Excused", "Unexcused", "Sick", "Percent")
                .build()
        )
        stats.forEach { s ->
            printer.printRecord(
                s.studentName, s.totalLessons, s.present,
                s.absentExcused, s.absentUnexcused, s.sick,
                "%.1f%%".format(s.attendancePercent)
            )
        }
        printer.flush()
        printer.close()
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }
}
