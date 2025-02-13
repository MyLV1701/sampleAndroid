package com.example.bg_counter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive called");
        if (intent != null && "com.example.bg_counter.ACTION_TIMER_COMPLETE".equals(intent.getAction())) {
            Log.d(TAG, "Received ACTION_TIMER_COMPLETE");
            // ...existing code...
        } else {
            Log.d(TAG, "Received unknown action");
        }
    }
}
