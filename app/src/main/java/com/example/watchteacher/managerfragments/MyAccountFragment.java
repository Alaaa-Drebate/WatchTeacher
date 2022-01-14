package com.example.watchteacher.managerfragments;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.watchteacher.EditProfileActivity;
import com.example.watchteacher.ManagerRegisterActivity;
import com.example.watchteacher.R;
import com.example.watchteacher.api.Constants;
import com.example.watchteacher.helper.GpsTracker;
import com.example.watchteacher.model.User;
import com.example.watchteacher.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;


public class MyAccountFragment extends Fragment {

    public static String TAG = "myAccountFragment";

    Button mEditProfileBtn;
    Button mUpdateSchoolLocationBtn;

    TextView mUserNameTV;
    TextView mPhoneTV;
    TextView mEmailTV;

    Button mLogoutBtn;

    String name, phone, email;
    int usertype = -1;
    int myId;

    double schoolLat, schoolLon;

    public MyAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usertype = SharedPrefManager.getInstance(requireContext()).getUserType();
        myId = SharedPrefManager.getInstance(requireContext()).getUserId();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);

        if(usertype==Constants.TEACHER){
            mEditProfileBtn.setVisibility(View.GONE);
            mUpdateSchoolLocationBtn.setVisibility(View.GONE);
        }else if(usertype == Constants.MANAGER){
            mUpdateSchoolLocationBtn.setVisibility(View.VISIBLE);
            mEditProfileBtn.setVisibility(View.VISIBLE);
        }

        updateLayout();

        //go to edit profile activity
        mEditProfileBtn.setOnClickListener(v ->
        {
            Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
            intent.putExtra("userId", myId);
            intent.putExtra("title", getResources().getString(R.string.edit_your_profile));
            requireActivity().startActivity(intent);
        });

        mLogoutBtn.setOnClickListener(v -> logOut());

        mUpdateSchoolLocationBtn.setOnClickListener(v -> {
            handleLocation();
        });

    }

    private void handleLocation() {

        LayoutInflater factory = LayoutInflater.from(requireContext());
        final View view = factory.inflate(R.layout.location_confirmation_dialog, null);
        final AlertDialog locationConfirmationDialog = new AlertDialog.Builder(requireContext()).create();
        locationConfirmationDialog.setView(view);

        TextView yes = view.findViewById(R.id.yes_btn);
        TextView no = view.findViewById(R.id.no_btn);


        yes.setOnClickListener(v -> {

            Context ctx = requireContext();
            GpsTracker tracker = new GpsTracker(ctx);
            if(!tracker.canGetLocation()){
                tracker.showSettingsAlert();
            }else {
                Location location = tracker.getLocation();
                if(location != null){
                    SharedPrefManager.getInstance(ctx).setSchoolLocation(true);
                    schoolLat = location.getLatitude();
                    schoolLon = location.getLongitude();
                    SharedPrefManager.getInstance(ctx).setSchoolLocation(schoolLat, schoolLon);
                    Log.e("lat", schoolLat+"");
                    Log.e("lon", schoolLon+"");
                }else {
                    Toast.makeText(ctx, "there was problem with getting your location please try again", Toast.LENGTH_SHORT).show();
                }


                final ProgressDialog pDialog = new ProgressDialog(ctx);
                pDialog.setMessage("Processing Please wait...");
                pDialog.show();

                ANRequest.PostRequestBuilder networking = AndroidNetworking.post("http://nawar.scit.co/oup/school-reports/api/auth/update-user.php");

                networking.addBodyParameter("user_id", String.valueOf(myId));
                networking.addBodyParameter("lat", String.valueOf(schoolLat));
                networking.addBodyParameter("lng", String.valueOf(schoolLon));

                networking.setPriority(Priority.MEDIUM);
                networking.build()
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

                                        Toast.makeText(ctx, "new location saved successfully", Toast.LENGTH_SHORT).show();

                                    } else if(obj.getInt("status") == -1){
                                        Toast.makeText(ctx, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                pDialog.dismiss();
                                Toast.makeText(ctx, anError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                locationConfirmationDialog.dismiss();
            }

        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationConfirmationDialog.dismiss();
            }
        });
        locationConfirmationDialog.show();

    }

    private void updateLayout() {
        User user = SharedPrefManager.getInstance(requireContext()).getUserData();
        name = user.getName();
        email = user.getEmail();
        phone = user.getPhone();

        mUserNameTV.setText(name);
        mEmailTV.setText(email);
        mPhoneTV.setText(phone);
    }

    private void bindViews(View v) {
        mEditProfileBtn = v.findViewById(R.id.edit_btn);
        mUpdateSchoolLocationBtn = v.findViewById(R.id.update_location_btn);

        mUserNameTV = v.findViewById(R.id.name);
        mPhoneTV = v.findViewById(R.id.phone);
        mEmailTV = v.findViewById(R.id.email);

        mLogoutBtn = v.findViewById(R.id.logout_btn);
    }

    public void logOut(){
        WorkManager manager = WorkManager.getInstance(requireContext());
        manager.cancelAllWorkByTag("tracker");
        Context context = requireContext();
        SharedPrefManager.getInstance(context).logout();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        assert intent != null;
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLayout();
    }
}