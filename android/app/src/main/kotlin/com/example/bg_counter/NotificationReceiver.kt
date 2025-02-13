package com.example.bg_counter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "timer_channel"
        private const val NOTIFICATION_ID = 1
    }

    private val TAG = "NotificationReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive called")
        if (intent.action == TimerService.ACTION_TIMER_COMPLETE) {
            val counter = intent.getIntExtra(TimerService.COUNTER_VALUE, 0)
            Log.d(TAG, "Received broadcast with counter value: $counter")
            showNotification(context, counter)
        } else {
            Log.d(TAG, "Received unknown action: ${intent.action}")
        }
    }

    private fun showNotification(context: Context, counter: Int) {
        Log.d(TAG, "Creating notification")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Timer Update")
            .setContentText("Counter value: $counter")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
        Log.d(TAG, "Notification displayed with counter value: $counter")
    }
}