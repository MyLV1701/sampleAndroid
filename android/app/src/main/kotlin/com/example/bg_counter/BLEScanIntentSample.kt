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
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
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
import java.util.UUID
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult as BluetoothScanResult

class BLEScanService : Service() {

    private lateinit var scanner: BluetoothLeScanner
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var scanPendingIntent: PendingIntent? = null

    companion object {
        const val BLE_SCAN_RESULT = "com.example.bg_counter.BLE_SCAN_RESULT"
        const val BLE_SCAN_VALUE = "ble_scan_value"
        val SERVICE_UUID: UUID = UUID.fromString("0000fd81-0000-1000-8000-00805f9b34fb")
    }

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
                    .setDeviceName("Basic_BLE")  // Filter for devices with name "A"
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
        val devices = MutableStateFlow(emptyList<BluetoothScanResult>())
        const val CHANNEL_ID = "ble_scan_channel"
        private const val BLE_NOTIFICATION_ID = 2
    }

    override fun onReceive(context: Context, intent: Intent) {

        Log.d("BLEScanReceiver", "onReceive is invoked --> onReceive()")
        
        val results = intent.getScanResults()
        results.forEach { result ->
            val device = result.device
            // Try multiple ways to get the device name
            val deviceName = result.scanRecord?.deviceName  // From scan record
                ?: device.name                             // Cached name
                ?: "Unknown Device"                        // Fallback
            
            Log.d("BLEScanReceiver", """
                Device Found:
                Address: ${device.address}
                Name: $deviceName
                RSSI: ${result.rssi}
                Advertisement Data: ${result.scanRecord?.bytes?.contentToString()}
            """.trimIndent())
        }

        //showNotification(context)
    }

    private fun showNotification(context: Context) {
        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             val channel = NotificationChannel(
                CHANNEL_ID,
                "ble_scan Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("ble_scan Update")
            .setContentText("ble_scan context")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(BLE_NOTIFICATION_ID, notification)
    }

    private fun Intent.getScanResults(): List<BluetoothScanResult> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArrayListExtra(
                BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT,
                BluetoothScanResult::class.java,
            )
        } else {
            @Suppress("DEPRECATION")
            getParcelableArrayListExtra(BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT)
        } ?: emptyList()
}
