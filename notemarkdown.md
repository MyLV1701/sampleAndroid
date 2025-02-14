### note for android PendingIntent


- Where to Invoke BLEScanService::startScan

The startScan method is already being invoked in the onStartCommand method of the BLEScanService class. To start the BLE scan, you need to start the BLEScanService. Here is an example of how to start the service from an Activity:

```java
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start the BLEScanService
        val intent = Intent(this, BLEScanService::class.java)
        startService(intent)
    }
}
```

Summary
The BLEScanService class manages the BLE scan process, starting the scan in the onStartCommand method and stopping it in the onDestroy method.
The BLEScanReceiver class handles the scan results and displays notifications.
To start the BLE scan, you need to start the BLEScanService, which will invoke the startScan method. This can be done from an Activity, Fragment, or any other component that can start a service.


