package com.example.watchteacher.services;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.watchteacher.helper.GpsTracker;
import com.example.watchteacher.model.LatLon;
import com.example.watchteacher.utils.GPSTools;
import com.example.watchteacher.utils.SharedPrefManager;

import java.util.Timer;
import java.util.TimerTask;

public class TrackerService extends Service {

    private static final String TAG = TrackerService.class.getSimpleName();

    private Timer timer = new Timer();

    private int myId;

    Context ctx;

    private double schoolLat, schoolLon;

    private GpsTracker tracker;

    private final int THRESHOLD_DISTANCE = 10;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public TrackerService() {
    }

    public TrackerService(Context applicationContext) {
        super();
        ctx = applicationContext;
        Log.i(TAG, "SensorService class");
    }

    @Override
    public void onCreate() {
        super.onCreate();


        myId = SharedPrefManager.getInstance(this).getUserId();

        if(!SharedPrefManager.getInstance(this).isLocationSet()){
            //in case we've already stored the school location
            LatLon latLon = SharedPrefManager.getInstance(this).getSchoolLocation();
            schoolLat = latLon.getLat();
            schoolLon = latLon.getLon();
        }else {
            Toast.makeText(this, "school location is not set yet!", Toast.LENGTH_SHORT).show();
            this.stopSelf();
        }

        tracker = new GpsTracker(this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand()");
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                doOperation();   //Your code here
            }
        }, 0, 5 * 1000);//2 Minute
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "serviceOnDestroy()");

        Intent broadcastIntent = new Intent("services.BoradcastTracker");
        sendBroadcast(broadcastIntent);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "onLowMemory()");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "serviceonTaskRemoved()");


        // workaround for kitkat: set an alarm service to trigger service again
        Intent intent = new Intent(getApplicationContext(), TrackerService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);

        super.onTaskRemoved(rootIntent);
    }






    public void doOperation() {


        tracker.getLocation();
        //34.8756158 35.8940172
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(GPSTools.distance(schoolLat, tracker.getLatitude(), schoolLon, tracker.getLongitude()) > THRESHOLD_DISTANCE){
                    sendAbsentReport();
                }else {
                    Log.e("school", schoolLat + " " + schoolLon);
                    Log.e("current", tracker.getLatitude() + " " + tracker.getLongitude());
                    Toast.makeText(TrackerService.this, "nothing to do, continue working!", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private void sendAbsentReport() {

    }


}
