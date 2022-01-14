package com.example.watchteacher.managerfragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.watchteacher.ManagerRegisterActivity;
import com.example.watchteacher.R;
import com.example.watchteacher.adapters.NotificationsAdapter;
import com.example.watchteacher.adapters.ReportsAdapter;
import com.example.watchteacher.api.Constants;
import com.example.watchteacher.model.Report;
import com.example.watchteacher.model.User;
import com.example.watchteacher.utils.DateTimeUtils;
import com.example.watchteacher.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class NotificationsFragment extends Fragment {

    public static String TAG = "notificationsFragment";


    ArrayList<Report> reports;
    RecyclerView todayReportsList;
    NotificationsAdapter notificationsAdapter;


    public NotificationsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reports = new ArrayList<Report>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        todayReportsList = view.findViewById(R.id.today_reports_list);

        getTodayReports();


    }

    void getTodayReports(){

        final ProgressDialog pDialog = new ProgressDialog(requireContext());
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        reports = new ArrayList<>();


        AndroidNetworking.get("http://nawar.scit.co/oup/school-reports/api/reports/all.php?admin_id="+String.valueOf(SharedPrefManager.getInstance(requireContext()).getUserId()))
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
                                    if(DateTimeUtils.isToday(report.getDateTime()))
                                        reports.add(report);
                                }

                                notificationsAdapter = new NotificationsAdapter(requireContext(), reports);
                                todayReportsList.setAdapter(notificationsAdapter);
                            } else if(obj.getInt("status") == -1){
                                Toast.makeText(requireContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        Toast.makeText(requireContext(), anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

}