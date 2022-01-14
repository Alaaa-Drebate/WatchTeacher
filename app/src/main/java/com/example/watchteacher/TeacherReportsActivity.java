package com.example.watchteacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.watchteacher.adapters.NotificationsAdapter;
import com.example.watchteacher.adapters.ReportsAdapter;
import com.example.watchteacher.model.Report;
import com.example.watchteacher.utils.DateTimeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TeacherReportsActivity extends AppCompatActivity {


    ArrayList<Report> reports;
    RecyclerView reportsList;
    ReportsAdapter reportsAdapter;

    int id;
    String name, phone;
    double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_reports);

        reportsList = findViewById(R.id.reports_list);


        Intent sender = getIntent();
        if(sender != null){
            id = sender.getIntExtra("id", -1);
            name = sender.getStringExtra("name");
            phone = sender.getStringExtra("phone");
            lat = sender.getDoubleExtra("lat", 200);
            lon = sender.getDoubleExtra("lon", 200);
        }

        getTeacherReports();
    }

    private void getTeacherReports() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        reports = new ArrayList<>();

        String teacherId = String.valueOf(id);
        AndroidNetworking.get("http://nawar.scit.co/oup/school-reports/api/reports/user.php?user_id="+ teacherId)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        pDialog.dismiss();

                        try {
                            //converting response to json object
                            JSONObject obj = response;

                            //if no error in response
                            if (obj.getInt("status") == 1) {

                                Toast.makeText(TeacherReportsActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONArray reportsArray = obj.getJSONArray("data");
                                Report report;
                                for(int i = 0; i < reportsArray.length(); i++){
                                    JSONObject reportObj = reportsArray.getJSONObject(i);
                                    report = new Report(
                                            Integer.parseInt(reportObj.getString("id")),
                                            Integer.parseInt(reportObj.getString("user_id")),
                                            reportObj.getString("user_name"),
                                            reportObj.getString("content"),
                                            reportObj.getString("created_at")
                                    );
                                    reports.add(report);
                                }

                                reportsAdapter = new ReportsAdapter(TeacherReportsActivity.this, reports);
                                reportsList.setAdapter(reportsAdapter);

                            } else if(obj.getInt("status") == -1){
                                Toast.makeText( TeacherReportsActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        Toast.makeText(TeacherReportsActivity.this, anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
}