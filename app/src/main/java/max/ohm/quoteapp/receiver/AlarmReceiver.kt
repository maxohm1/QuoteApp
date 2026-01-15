package max.ohm.quoteapp.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import max.ohm.quoteapp.MainActivity
import max.ohm.quoteapp.R
import max.ohm.quoteapp.domain.repository.QuoteRepository
import max.ohm.quoteapp.util.Constants
import max.ohm.quoteapp.util.NotificationScheduler
import max.ohm.quoteapp.util.Resource
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: QuoteRepository

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        
        scope.launch {
            try {
                val result = repository.getDailyQuote()
                if (result is Resource.Success) {
                    result.data?.let { quote ->
                        showNotification(context, quote.text, quote.author)
                    }
                }
                
                // Reschedule for next day using the time provided in the intent
                val timeStr = intent.getStringExtra(NotificationScheduler.EXTRA_TIME)
                if (timeStr != null) {
                    NotificationScheduler.scheduleDailyQuote(context, timeStr)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
    
    private fun showNotification(context: Context, quoteText: String, author: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("✨ Your Daily Inspiration")
            .setContentText("\"$quoteText\" — $author")
            .setStyle(NotificationCompat.BigTextStyle().bigText("\"$quoteText\"\n\n— $author"))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Constants.NOTIFICATION_ID, notification)
    }
}
