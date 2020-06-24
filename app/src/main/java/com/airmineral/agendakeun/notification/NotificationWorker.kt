package com.airmineral.agendakeun.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(
    context,
    workerParams
) {
    override fun doWork(): Result {
        val desc = inputData.getString("DESC") ?: return Result.failure()
        sendNotification(this.applicationContext, "Reminder", desc)
        return Result.success()
    }
}
