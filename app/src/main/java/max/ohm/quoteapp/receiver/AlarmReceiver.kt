package max.ohm.quoteapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import max.ohm.quoteapp.util.NotificationScheduler
import max.ohm.quoteapp.worker.DailyQuoteWorker

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Trigger the worker immediately to show notification
        val workRequest = OneTimeWorkRequest.Builder(DailyQuoteWorker::class.java).build()
        WorkManager.getInstance(context).enqueue(workRequest)
        
        // Reschedule the alarm for the next day
        val timeStr = intent.getStringExtra(NotificationScheduler.EXTRA_TIME)
        if (timeStr != null) {
            // Schedule for tomorrow (logic inside scheduleDailyQuote handles this because "now" matches trigger time)
            NotificationScheduler.scheduleDailyQuote(context, timeStr)
        }
    }
}
