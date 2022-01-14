package com.example.watchteacher.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastTracker extends BroadcastReceiver
{

    private static final String TAG = "RestartServiceReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Service Stops, let's restart again.");
        context.startService(new Intent(context, TrackerService.class));
    }
}