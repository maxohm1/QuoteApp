package max.ohm.quoteapp.util

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import max.ohm.quoteapp.worker.DailyQuoteWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    
    fun scheduleDailyQuote(context: Context, timeStr: String) {
        val workManager = WorkManager.getInstance(context)
        
        try {
            val parts = timeStr.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            
            if (target.before(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
            
            val initialDelay = target.timeInMillis - now.timeInMillis
            
            val workRequest = PeriodicWorkRequestBuilder<DailyQuoteWorker>(
                1, TimeUnit.DAYS
            )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()
            
            workManager.enqueueUniquePeriodicWork(
                Constants.DAILY_QUOTE_WORK_NAME,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                workRequest
            )
            
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback default
        }
    }
    
    fun cancelDailyQuote(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(Constants.DAILY_QUOTE_WORK_NAME)
    }
}
