package com.example.bg_counter
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import android.util.Log

class TimerService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var counter = 0
    private val TAG = "TimerService"

    companion object {
        const val ACTION_TIMER_COMPLETE = "com.example.bg_counter.ACTION_TIMER_COMPLETE"
        const val A_BLE_SCAN_RESULT = "com.example.bg_counter.BLE_SCAN_RESULT"
        const val COUNTER_VALUE = "counter_value"
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        startCounting()
        return START_STICKY
    }

    private fun startCounting() {
        serviceScope.launch {
            while (true) {
                delay(3000) // wait for 3 seconds
                counter++
                Log.d(TAG, "Counter value: $counter")
                sendTimerCompleteBroadcast(counter)
            }
        }
    }

    private fun sendTimerCompleteBroadcast(counter : Int) {
        val intent = Intent(A_BLE_SCAN_RESULT).apply {
            setPackage(applicationContext.packageName)
        }
        
        val pendingIntent = 
        PendingIntent.getBroadcast(
            applicationContext, // The context in which the PendingIntent should start the broadcast.
            0, // Request code, used to identify the PendingIntent.
            intent, // The Intent to be broadcast.
            PendingIntent.FLAG_UPDATE_CURRENT // Flag to update the existing PendingIntent with the new Intent data.
            or PendingIntent.FLAG_MUTABLE // Flag to allow the PendingIntent to be mutable.
        )

        try {
            pendingIntent.send()
        } catch (e: PendingIntent.CanceledException) {
            println("Error sending broadcast:" + e.message)
            Log.e(TAG, "Error sending broadcast", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        serviceScope.cancel()
    }
}