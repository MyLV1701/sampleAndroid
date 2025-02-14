package com.example.bg_counter
import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.bg_counter/timer"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "startService" ) 
            {
                // start Timer service
                // startService(Intent(this, TimerService::class.java))

                // start BLE scan service               
                startService(Intent(this, BLEScanService::class.java))

                result.success(null)
            }
            else if (call.method == "stopService" ) 
            {
                stopService(Intent(this, TimerService::class.java))
                result.success(null)
            }
            else
            {
                result.notImplemented()
            }
        }
    }
}
