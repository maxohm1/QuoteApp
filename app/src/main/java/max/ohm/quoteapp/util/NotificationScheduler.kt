package max.ohm.quoteapp.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.WorkManager
import max.ohm.quoteapp.receiver.AlarmReceiver
import java.util.Calendar

object NotificationScheduler {
    
    const val EXTRA_TIME = "extra_time"
    private const val ALARM_REQUEST_CODE = 1001
    
    fun scheduleDailyQuote(context: Context, timeStr: String) {
        // 1. Cancel legacy WorkManager job if any
        WorkManager.getInstance(context).cancelUniqueWork(Constants.DAILY_QUOTE_WORK_NAME)

        // 2. Schedule Alarm
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
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
            
            // If the target time is in the past, schedule for tomorrow
            if (target.timeInMillis <= now.timeInMillis) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
            
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra(EXTRA_TIME, timeStr)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context, 
                ALARM_REQUEST_CODE, 
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, target.timeInMillis, pendingIntent)
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, target.timeInMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, target.timeInMillis, pendingIntent)
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun cancelDailyQuote(context: Context) {
        // Cancel WorkManager
        WorkManager.getInstance(context).cancelUniqueWork(Constants.DAILY_QUOTE_WORK_NAME)
        
        // Cancel Alarm
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 
            ALARM_REQUEST_CODE, 
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}
