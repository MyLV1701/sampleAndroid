import 'package:flutter/material.dart';

class ErrorDialog {
  static void showPermissionDeniedDialog(
      BuildContext context, List<String> deniedPermissions) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text('Permission Denied'),
          content: Text(
              'The following permissions were denied: ${deniedPermissions.join(', ')}'),
          actions: <Widget>[
            TextButton(
              child: Text('OK'),
              onPressed: () {
                Navigator.of(context).pop();
              },
            ),
          ],
        );
      },
    );
  }
}
