package com.attendance.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.attendance.app.data.local.dao.StudentDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class ExpelledStudentCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val studentDao: StudentDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val threshold = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
        val expired = studentDao.getExpiredExpelledStudents(threshold)
        if (expired.isNotEmpty()) {
            studentDao.deleteByIds(expired.map { it.id })
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "expelled_cleanup"

        fun schedule(workManager: WorkManager) {
            val request = PeriodicWorkRequestBuilder<ExpelledStudentCleanupWorker>(
                1, TimeUnit.DAYS
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            ).build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
