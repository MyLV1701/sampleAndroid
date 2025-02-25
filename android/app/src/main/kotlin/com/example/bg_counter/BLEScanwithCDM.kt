package com.example.bg_counter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.companion.AssociationRequest
import android.companion.CompanionDeviceManager
import android.companion.BluetoothLeDeviceFilter
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.regex.Pattern
import android.content.BroadcastReceiver

class BLEScanwithCDM : Service() {

    private lateinit var companionDeviceManager: CompanionDeviceManager
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val TARGET_DEVICE_NAME = "Basic_BLE" // Replace with your target device name
    
    override fun onCreate() {
        super.onCreate()
        companionDeviceManager = getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        startScan()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    private fun startScan() {
        val deviceFilter: BluetoothLeDeviceFilter = BluetoothLeDeviceFilter.Builder()
            .setNamePattern(Pattern.compile(TARGET_DEVICE_NAME))
            .build()

        val pairingRequest: AssociationRequest = AssociationRequest.Builder()
            .addDeviceFilter(deviceFilter)
            .setSingleDevice(true)
            .build()

        companionDeviceManager.associate(pairingRequest, object : CompanionDeviceManager.Callback() {
            override fun onDeviceFound(chooserLauncher: IntentSender) {
                Log.d("BLEScanwithCDM", "Device found: $TARGET_DEVICE_NAME")
            }

            override fun onFailure(error: CharSequence?) {
                Log.e("BLEScanwithCDM", "Failed to find device: ${error.toString()}")
            }
        }, null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}


@RequiresApi(Build.VERSION_CODES.O)
class BLEScanwithCMDReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.d("BLEScanwithCMDReceiver", "onReceive is invoked --> onReceive()")
        
        if (BluetoothDevice.ACTION_ACL_DISCONNECTED == intent.action) {
           Log.d("BLEScanwithCMDReceiver", "onReceive is invoked --> ACTION_ACL_DISCONNECTED ")

            val serviceIntent = Intent(context, BLEScanwithCDM::class.java)
            context.startService(serviceIntent)
            Log.d("BLEScanwithCMDReceiver", "Starting BLEScanwithCDM service")
            
        }        
    }
}