package com.rooftop.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_PROGRAMME_TITLE) ?: return Result.failure()
        val channelName = inputData.getString(KEY_CHANNEL_NAME) ?: ""

        ensureNotificationChannel()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Starting soon: $title")
            .setContentText(channelName)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(applicationContext)
                .notify(System.currentTimeMillis().toInt(), notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS permission not granted — silently skip
        }

        return Result.success()
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Programme Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "rooftop_reminders"
        const val KEY_PROGRAMME_TITLE = "programme_title"
        const val KEY_CHANNEL_NAME = "channel_name"
    }
}
