import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // TRY THIS: Try running your application with "flutter run". You'll see
        // the application has a purple toolbar. Then, without quitting the app,
        // try changing the seedColor in the colorScheme below to Colors.green
        // and then invoke "hot reload" (save your changes or press the "hot
        // reload" button in a Flutter-supported IDE, or press "r" if you used
        // the command line to start the app).
        //
        // Notice that the counter didn't reset back to zero; the application
        // state is not lost during the reload. To reset the state, use hot
        // restart instead.
        //
        // This works for code too, not just values: Most code changes can be
        // tested with just a hot reload.
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;
  final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin =
      FlutterLocalNotificationsPlugin();

  final title = 'Default Title';
  final body = 'Default Body';


  @override
  void initState() {
    super.initState();
    //_initializeNotifications();
  }

  // Future<void> _showNotification() async {
  //   const DarwinInitializationSettings initializationSettingsDarwin =
  //       DarwinInitializationSettings(
  //     requestAlertPermission: true,
  //     requestBadgePermission: true,
  //     requestSoundPermission: true,
  //   );
  //
  //   const AndroidInitializationSettings initializationSettingsAndroid =
  //       AndroidInitializationSettings('@mipmap/ic_launcher');
  //
  //   const InitializationSettings initializationSettings = InitializationSettings(
  //     android: initializationSettingsAndroid,
  //     iOS: initializationSettingsDarwin,
  //   );
  //
  //   await flutterLocalNotificationsPlugin.initialize(
  //     initializationSettings,
  //   );
  //
  //   AndroidNotificationDetails androidPlatformChannelSpecifics =
  //   AndroidNotificationDetails(
  //     'flutter_blue_plus_example_id',
  //     'flutter_blue_plus_example_channel',
  //     channelDescription: 'channelDescription',
  //     importance: Importance.max,
  //     priority: Priority.high,
  //     icon: '@mipmap/ic_launcher',
  //   );
  //
  //   NotificationDetails platformChannelSpecifics = NotificationDetails(
  //     android: androidPlatformChannelSpecifics,
  //     iOS: const DarwinNotificationDetails(),
  //   );
  //
  //   await flutterLocalNotificationsPlugin.show(
  //     0,
  //     'Counter Updated',
  //     'Count is: $_counter',
  //     platformChannelSpecifics,
  //   );
  // }


  Future<void> showNotification(String title, String body) async {
    final FlutterLocalNotificationsPlugin flutterLocalNotificationsPlugin = FlutterLocalNotificationsPlugin();

    const DarwinInitializationSettings initializationSettingsDarwin =
    DarwinInitializationSettings(
      requestAlertPermission: true,
      requestBadgePermission: true,
      requestSoundPermission: true,
    );

    const AndroidInitializationSettings initializationSettingsAndroid =
    AndroidInitializationSettings('@mipmap/ic_launcher');

    const InitializationSettings initializationSettings = InitializationSettings(
      android: initializationSettingsAndroid,
      iOS: initializationSettingsDarwin, // 修正箇所
    );

    await flutterLocalNotificationsPlugin.initialize(
      initializationSettings,
    );

    AndroidNotificationDetails androidPlatformChannelSpecifics =
    AndroidNotificationDetails(
      'flutter_blue_plus_example_id',
      'flutter_blue_plus_example_channel',
      channelDescription: 'channelDescription',
      importance: Importance.max,
      priority: Priority.high,
      icon: '@mipmap/ic_launcher',
      styleInformation: BigTextStyleInformation(body),
    );

    NotificationDetails platformChannelSpecifics = NotificationDetails(
      android: androidPlatformChannelSpecifics,
      iOS: const DarwinNotificationDetails(),
    );

    // await flutterLocalNotificationsPlugin.show(
    //   0, title, body, platformChannelSpecifics,
    // );
    await flutterLocalNotificationsPlugin.show(
      0,
      'Counter Updated',
      'Count is: $_counter',
      platformChannelSpecifics,
    );
  }

  void _incrementCounter() {
    setState(() {
      _counter++;
      showNotification(title, body);
    });
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Scaffold(
      appBar: AppBar(
        // TRY THIS: Try changing the color here to a specific color (to
        // Colors.amber, perhaps?) and trigger a hot reload to see the AppBar
        // change color while the other colors stay the same.
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
      ),
      body: Center(
        // Center is a layout widget. It takes a single child and positions it
        // in the middle of the parent.
        child: Column(
          // Column is also a layout widget. It takes a list of children and
          // arranges them vertically. By default, it sizes itself to fit its
          // children horizontally, and tries to be as tall as its parent.
          //
          // Column has various properties to control how it sizes itself and
          // how it positions its children. Here we use mainAxisAlignment to
          // center the children vertically; the main axis here is the vertical
          // axis because Columns are vertical (the cross axis would be
          // horizontal).
          //
          // TRY THIS: Invoke "debug painting" (choose the "Toggle Debug Paint"
          // action in the IDE, or press "p" in the console), to see the
          // wireframe for each widget.
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'Press the button to increment counter.\nCheck notifications!',
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
