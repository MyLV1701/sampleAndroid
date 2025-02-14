# Note for android PendingIntent (service related)

## onStartCommand function

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

#### Summary (onStartCommand)
The BLEScanService class manages the BLE scan process, starting the scan in the onStartCommand method and stopping it in the onDestroy method.
The BLEScanReceiver class handles the scan results and displays notifications.
To start the BLE scan, you need to start the BLEScanService, which will invoke the startScan method. This can be done from an Activity, Fragment, or any other component that can start a service.


## onDestroy function


The onDestroy function in the BLEScanService class is called when the service is being destroyed. This can happen in several scenarios:

Scenarios When onDestroy is Called
1. Service is Stopped:
When you explicitly stop the service by calling stopService(Intent) from an Activity, Fragment, or another component.

```java
val intent = Intent(this, BLEScanService::class.java)
stopService(intent)
```
2. Service is Stopped by the System:

The Android system may stop the service to reclaim resources if the system is under memory pressure.
The service may also be stopped if it is no longer needed or if the app is being closed.

3. Service Lifecycle Ends:
If the service is a bound service and all clients unbind from it, the service will be destroyed.
If the service is a foreground service and the foreground notification is removed, the service may be destroyed.

What Happens in onDestroy
In the BLEScanService class, the onDestroy method is overridden to perform cleanup tasks when the service is destroyed. Specifically, it stops the BLE scan by calling scanner.stopScan(pendingIntent).
Here is the relevant code:
``` java
override fun onDestroy() {
    super.onDestroy()
    scanPendingIntent?.let { pendingIntent ->
        scanner.stopScan(pendingIntent)
    }
}
```

Example of Stopping the Service
Here is an example of how you might start and stop the BLEScanService from an Activity:
``` java
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start the BLEScanService
        val startIntent = Intent(this, BLEScanService::class.java)
        startService(startIntent)
    }

    override fun onDestroy() {
        super.onDestroy()

        // Stop the BLEScanService
        val stopIntent = Intent(this, BLEScanService::class.java)
        stopService(stopIntent)
    }
}
```
#### Summary (onDestroy)
The onDestroy method in the BLEScanService class is called when the service is being destroyed. This can happen when the service is explicitly stopped, when the system stops the service, or when the service lifecycle ends. In the onDestroy method, the BLE scan is stopped to ensure that resources are properly released.

