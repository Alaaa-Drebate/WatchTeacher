package com.example.watchteacher.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.watchteacher.R;
import com.example.watchteacher.helper.GpsTracker;
import com.example.watchteacher.model.LatLon;
import com.example.watchteacher.utils.GPSTools;
import com.example.watchteacher.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

public class TrackWorker extends Worker {

    private int myId;

    public static  final String  CHANNEL_ID = "myChannelID";

    private double schoolLat, schoolLon;

    private GpsTracker tracker;

    private final int THRESHOLD_DISTANCE = 1000;

    private Context context;

    public TrackWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }


    @Override
    public Result doWork() {


        myId = SharedPrefManager.getInstance(context).getUserId();

        doing();


        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }



    void doing(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if(!SharedPrefManager.getInstance(context).isLocationSet()){
                    //in case we've already stored the school location
                    LatLon latLon = SharedPrefManager.getInstance(context).getSchoolLocation();
                    schoolLat = latLon.getLat();
                    schoolLon = latLon.getLon();
                }else {
                    Toast.makeText(context, "school location is not set yet!", Toast.LENGTH_SHORT).show();
                }

                tracker = new GpsTracker(context);

                tracker.getLocation();

                Log.e("working!!!", "message");

                if(GPSTools.distance(schoolLat, tracker.getLatitude(), schoolLon, tracker.getLongitude()) > THRESHOLD_DISTANCE){
                    sendAbsentReport();
                    updateTeacherCurrentLocation();
                }else {
                    Toast.makeText(context, "nothing to do, continue working!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendAbsentReport() {

        ANRequest.PostRequestBuilder networking = AndroidNetworking.post("http://nawar.scit.co/oup/school-reports/api/reports/add.php");

        networking.addBodyParameter("user_id", String.valueOf(myId));
        networking.addBodyParameter("lat", String.valueOf(tracker.getLatitude()));
        networking.addBodyParameter("lng", String.valueOf(tracker.getLongitude()));
        networking.addBodyParameter("content", "got out of school during work hours");

        networking.setPriority(Priority.MEDIUM);
        networking.build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response

                        try {
                            //converting response to json object
                            JSONObject obj = response;

                            //if no error in response
                            if (obj.getInt("status") == 1) {

//                                Toast.makeText(context, "new location saved successfully", Toast.LENGTH_SHORT).show();

                            } else if(obj.getInt("status") == -1){
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context, anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTeacherCurrentLocation() {
        ANRequest.PostRequestBuilder networking = AndroidNetworking.post("http://nawar.scit.co/oup/school-reports/api/auth/update-user.php");

        networking.addBodyParameter("user_id", String.valueOf(myId));
        networking.addBodyParameter("lat", String.valueOf(tracker.getLatitude()));
        networking.addBodyParameter("lng", String.valueOf(tracker.getLongitude()));

        networking.setPriority(Priority.MEDIUM);
        networking.build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response

                        try {
                            //converting response to json object
                            JSONObject obj = response;

                            //if no error in response
                            if (obj.getInt("status") == 1) {

//                                Toast.makeText(context, "new location saved successfully", Toast.LENGTH_SHORT).show();

                            } else if(obj.getInt("status") == -1){
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(context, anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
