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
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.watchteacher.R;
import com.example.watchteacher.adapters.NotificationsAdapter;
import com.example.watchteacher.adapters.TeachersAdapter;
import com.example.watchteacher.model.Report;
import com.example.watchteacher.model.User;
import com.example.watchteacher.utils.DateTimeUtils;
import com.example.watchteacher.utils.SharedPrefManager;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TeachersFragment extends Fragment {

    public static String TAG = "teachersFragment";

    TextInputEditText mSearchET;

    //if you want to display some colors which determine teachers situation at the right moment
    double schoolLat, schoolLon;

    ArrayList<User> teachers;
    RecyclerView mTeachersList;
    TeachersAdapter mTeachersAdapter;

    String query;

    public TeachersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teachers, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTeachersList = view.findViewById(R.id.teachers_list);
        mSearchET = view.findViewById(R.id.search_edit_text);

        mSearchET.setOnEditorActionListener((v, actionId, event) -> {
            if (event == null) {
                query = mSearchET.getText().toString();
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    if(!query.isEmpty())
                        getTeachersByManagerId(query);
                }
                else if (actionId==EditorInfo.IME_ACTION_NEXT)
                {
                    // Capture soft enters in other singleLine EditTexts
                    if(!query.isEmpty())
                        getTeachersByManagerId(query);
                }
                else return false;  // Let system handle all other null KeyEvents
            } else
                return false;

            return false;
        });


        getTeachersByManagerId();
    }

    public void getTeachersByManagerId(){
        final ProgressDialog pDialog = new ProgressDialog(requireContext());
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        teachers = new ArrayList<>();

        String managerId = String.valueOf(SharedPrefManager.getInstance(requireContext()).getUserId());

        AndroidNetworking.get("http://nawar.scit.co/oup/school-reports/api/get-users.php?admin_id=" + managerId)
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

                                Toast.makeText(requireContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONArray reportsArray = obj.getJSONArray("data");
                                User user;
                                for(int i = 0; i < reportsArray.length(); i++){
                                    JSONObject userJson = reportsArray.getJSONObject(i);
                                    user = new User(
                                            userJson.getString("name"),
                                            Integer.parseInt(userJson.getString("id")),
                                            userJson.getString("phone_number"),
                                            Double.parseDouble(userJson.getString("lat")),
                                            Double.parseDouble(userJson.getString("lng"))
                                    );
                                    teachers.add(user);
                                }

                                mTeachersAdapter = new TeachersAdapter(requireContext(), teachers);
                                mTeachersList.setAdapter(mTeachersAdapter);

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

    public void getTeachersByManagerId(String name){
        final ProgressDialog pDialog = new ProgressDialog(requireContext());
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        teachers = new ArrayList<>();

        String managerId = String.valueOf(SharedPrefManager.getInstance(requireContext()).getUserId());

        AndroidNetworking.get("http://nawar.scit.co/oup/school-reports/api/get-users.php?admin_id=" + managerId + "&search_text=" + name)
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

                                Toast.makeText(requireContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONArray reportsArray = obj.getJSONArray("data");
                                User user;
                                for(int i = 0; i < reportsArray.length(); i++){
                                    JSONObject userJson = reportsArray.getJSONObject(i);
                                    user = new User(
                                            userJson.getString("name"),
                                            Integer.parseInt(userJson.getString("id")),
                                            userJson.getString("phone_number"),
                                            Double.parseDouble(userJson.getString("lat")),
                                            Double.parseDouble(userJson.getString("lng"))
                                    );
                                    teachers.add(user);
                                }

                                mTeachersAdapter = new TeachersAdapter(requireContext(), teachers);
                                mTeachersList.setAdapter(mTeachersAdapter);

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