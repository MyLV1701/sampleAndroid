/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bg_counter

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class BLEScanService : Service() {

    private lateinit var scanner: BluetoothLeScanner
    private var scanPendingIntent: PendingIntent? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        scanner = bluetoothManager.adapter.bluetoothLeScanner
        startScan()
        return START_STICKY
    }

    @SuppressLint("InlinedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private fun startScan() {
        try {
            val scanSettings: ScanSettings = ScanSettings.Builder()
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setReportDelay(3000)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

            scanPendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                Intent(this, BLEScanReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
            )

            val scanFilters = listOf(
                ScanFilter.Builder()
                    .setDeviceName("A")  // Filter for devices with name "A"
                    .build()
            )
            if (::scanner.isInitialized && scanPendingIntent != null) {
                scanPendingIntent?.let { 
                    pendingIntent -> scanner.startScan(scanFilters, scanSettings, pendingIntent)
                }
                Log.d("BLEScanService", "BLE Scan device started --> scanner.startScan()")
            } else {
                Log.e("BLEScanService", "Scanner not initialized or pending intent is null")
            }
        } catch (e: Exception) {
            Log.e("BLEScanService", "Error starting BLE scan: ${e.message}")
        }

        Log.d("BLEScanService", "startScan is invoked --> startScan()")
    }

    override fun onDestroy() {
        super.onDestroy()
        scanPendingIntent?.let { pendingIntent ->
            scanner.stopScan(pendingIntent)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
class BLEScanReceiver : BroadcastReceiver() {

    companion object {
        val devices = MutableStateFlow(emptyList<ScanResult>())
        const val CHANNEL_ID = "BLE_SCAN_CHANNEL"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val results = intent.getScanResults()
        Log.d("BLEScanReceiver", "Devices found: ${results.size}")

        // if (results.isNotEmpty()) {
        //     devices.update { scanResults ->
        //         (scanResults + results).distinctBy { it.device.address }
        //     }
        //     showNotification(context, "BLE Device Detected", "Found ${results.size} devices")
        // }
    }

    private fun showNotification(context: Context, title: String, content: String) {
        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "BLE Scan Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for BLE scan results"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun Intent.getScanResults(): List<ScanResult> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArrayListExtra(
                BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT,
                ScanResult::class.java,
            )
        } else {
            @Suppress("DEPRECATION")
            getParcelableArrayListExtra(BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT)
        } ?: emptyList()
}
