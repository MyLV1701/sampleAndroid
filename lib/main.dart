import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;

  static const platform = MethodChannel('com.example.bg_counter/timer');

  bool _isServiceRunning = false;

  Future<void> _toggleService() async {
    try {
       if (_isServiceRunning)
       {
        await platform.invokeMethod('stopService');
       }
       else 
       {
        await platform.invokeMethod('startService');
       }

      setState(() {
          _isServiceRunning = !_isServiceRunning;
      });


    } on PlatformException catch (e) {
      print("failed to toggle service : '${e.message}' . ");
    }
  }


  // Initialize notifications
  final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
  FlutterLocalNotificationsPlugin();

  late final AndroidInitializationSettings initializationSettingsAndroid;
  late final InitializationSettings initializationSettings;

  @override
  void initState() {
    super.initState();
    initializationSettingsAndroid =
    const AndroidInitializationSettings('@mipmap/ic_launcher');
    initializationSettings =
        InitializationSettings(android: initializationSettingsAndroid);
    flutterLocalNotificationsPlugin.initialize(initializationSettings);
  }

  Future<void> _showNotification() async {
    const AndroidNotificationDetails androidNotificationDetails =
    AndroidNotificationDetails(
      'your channel id',
      'your channel name',
      channelDescription: 'your channel description',
      importance: Importance.max,
      priority: Priority.high,
    );

    const NotificationDetails notificationDetails =
    NotificationDetails(android: androidNotificationDetails);

    await flutterLocalNotificationsPlugin.show(
      0,
      'Hello!',
      'The counter : $_counter',
      notificationDetails,
    );
  }

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
    _showNotification();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              _isServiceRunning ? 'Service is Running' : 'Service is stoped',
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _toggleService,
        tooltip: _isServiceRunning ? 'Stop Service' : 'Start Service',
        child: Icon(_isServiceRunning? Icons.stop : Icons.play_arrow),
      ),
    );
  }
}
