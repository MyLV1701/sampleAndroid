
package com.example.bg_counter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.bluetooth.BluetoothAdapter
import android.os.Handler
import android.os.Looper



class BLEConnectionService : Service() {

    private var bluetoothGatt: BluetoothGatt? = null
    private var connectionAttempts = 0
    private val MAX_ATTEMPTS = 3

    companion object {
        const val CHANNEL_ID = "ble_connection_channel"
        private const val BLE_NOTIFICATION_ID = 1
        const val DEVICE_ADDRESS = "device_address"
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }


    fun BluetoothGatt.refresh(): Boolean {
        return try {
            val method = this::class.java.getMethod("refresh")
            method.invoke(this) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val deviceAddress = intent?.getStringExtra(DEVICE_ADDRESS)
        if (deviceAddress != null) {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val device : BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
            connectToDevice(device)
        } else {
            stopSelf()
        }
        return START_STICKY
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            Log.d("BLEConnectionService", "Successfully connected to ${gatt.device.address}")
                            connectionAttempts = 0  // Reset counter on successful connection
                            gatt.discoverServices()
                        }
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            Log.d("BLEConnectionService", "Disconnected from ${gatt.device.address}")
                            bluetoothGatt?.close()
                            bluetoothGatt = null
                            // Retry connection if not max attempts
                            if (connectionAttempts < MAX_ATTEMPTS) {
                                connectToDevice(gatt.device)
                            } else {
                                stopSelf()
                            }
                        }
                    }
                }
                else -> {
                    Log.e("BLEConnectionService", "Connection failed with status: $status")
                    bluetoothGatt?.close()
                    bluetoothGatt = null
                    // Retry on error
                    if (connectionAttempts < MAX_ATTEMPTS) {
                        connectToDevice(gatt.device)
                    } else {
                        stopSelf()
                    }
                }
            }
        }
    }

    private fun connectToDevice(device: BluetoothDevice) {

        val scanService = Intent(this, BLEScanService::class.java)
        stopService(scanService)
        
        bluetoothGatt?.close()
        bluetoothGatt = null

        connectionAttempts++

        Handler(Looper.getMainLooper()).postDelayed({
            bluetoothGatt = device.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
            bluetoothGatt?.refresh()
        }, 2000)  // Add a delay before reconnecting

        // bluetoothGatt = device.connectGatt(this, false, gattCallback)

        Log.d("BLEConnectionService", "Connecting to ${device.address}...")
    }

    private fun startForegroundService() {
        val notificationManager = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "BLE Connection Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("BLE Connection Service")
            .setContentText("Handling BLE connection")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(BLE_NOTIFICATION_ID, notification)

        Log.d("BLEConnectionService", "startForeground(BLE_NOTIFICATION_ID, notification)")
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
