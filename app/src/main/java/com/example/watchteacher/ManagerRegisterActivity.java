package com.example.watchteacher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.watchteacher.api.Constants;
import com.example.watchteacher.helper.GpsTracker;
import com.example.watchteacher.model.User;
import com.example.watchteacher.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ManagerRegisterActivity extends AppCompatActivity {

    EditText mNameET, mPhoneET, mEmailET, mPasswordET;
    Button mAddLocationBtn, mRegisterBtn;
    TextView mSignUpLoginBtn;

    double lat = 200;
    double lon = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bindViews();

        mAddLocationBtn.setOnClickListener(v -> handleLocation());

        mSignUpLoginBtn.setOnClickListener(v -> {
            finish();
        });

        mRegisterBtn.setOnClickListener(v -> {
            mRegisterBtn.setEnabled(false);
            if(validateUserData()){
                managerRegister();
            }
        });

    }


    private void handleLocation() {

        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.location_confirmation_dialog, null);
        final AlertDialog locationConfirmationDialog = new AlertDialog.Builder(this).create();
        locationConfirmationDialog.setView(view);

        TextView yes = view.findViewById(R.id.yes_btn);
        TextView no = view.findViewById(R.id.no_btn);


        yes.setOnClickListener(v -> {

            GpsTracker tracker = new GpsTracker(ManagerRegisterActivity.this);
            if(!tracker.canGetLocation()){
                tracker.showSettingsAlert();
            }else {
                Location location = tracker.getLocation();
                if(location != null){
                    SharedPrefManager.getInstance(ManagerRegisterActivity.this).setSchoolLocation(true);
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                    SharedPrefManager.getInstance(ManagerRegisterActivity.this).setSchoolLocation(lat, lon);
                    Log.e("lat", lat+"");
                    Log.e("lon", lon+"");
                }else {
                    Toast.makeText(ManagerRegisterActivity.this, "there was problem with getting your location please try again", Toast.LENGTH_SHORT).show();
                }
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

    private void bindViews() {
        mNameET = findViewById(R.id.full_name);
        mPhoneET = findViewById(R.id.phone);
        mEmailET = findViewById(R.id.email);
        mPasswordET = findViewById(R.id.password);
        mAddLocationBtn = findViewById(R.id.add_location_btn);
        mRegisterBtn = findViewById(R.id.register_btn);
        mSignUpLoginBtn = findViewById(R.id.signup_login_btn);
    }

    private boolean validateUserData() {

        //first getting the values
        final String email = mEmailET.getText().toString();
        final String pass = mPasswordET.getText().toString();
        final String name = mNameET.getText().toString();
        final String phone = mPhoneET.getText().toString();

        //checking if username is empty
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "please enter your full name !", Toast.LENGTH_SHORT).show();
            mRegisterBtn.setEnabled(true);
            return false;
        }

        //checking if password is empty
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "please enter your phone number!", Toast.LENGTH_SHORT).show();
            mRegisterBtn.setEnabled(true);
            return false;
        }

        //checking if username is empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "please enter your email address!", Toast.LENGTH_SHORT).show();
            mRegisterBtn.setEnabled(true);
            return false;
        }

        //checking if password is empty
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "please enter your password!", Toast.LENGTH_SHORT).show();
            mRegisterBtn.setEnabled(true);
            return false;
        }

        if(lat == 200 || lon == 200){
            Toast.makeText(this, "location is not set yet the app won't work until school location is set", Toast.LENGTH_LONG).show();
        }

        return true;
    }

    private void managerRegister() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        //first getting the values
        final String email = mEmailET.getText().toString();
        final String pass = mPasswordET.getText().toString();
        final String name = mNameET.getText().toString();
        final String phone = mPhoneET.getText().toString();

        AndroidNetworking.post("http://nawar.scit.co/oup/school-reports/api/auth/signup.php")
                .addBodyParameter("name", name)
                .addBodyParameter("phone_number", phone)
                .addBodyParameter("email", email)
                .addBodyParameter("password", pass)
                .addBodyParameter("lat", String.valueOf(lat))
                .addBodyParameter("lng", String.valueOf(lon))
                .addBodyParameter("type", String.valueOf(Constants.MANAGER))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        pDialog.dismiss();

                        try {
                            //converting response to json object
                            Toast.makeText(ManagerRegisterActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                            JSONObject obj = response;

                            //if no error in response
                            if (obj.getInt("status") == 1) {

                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("data");
                                User user;
                                SharedPrefManager.getInstance(getApplicationContext()).setUserType(Constants.MANAGER);
                                user = new User(
                                        Integer.parseInt(userJson.getString("id")),
                                        userJson.getString("name"),
                                        userJson.getString("email"),
                                        userJson.getString("phone_number")
                                );

                                //storing the user in shared preferences
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                goToManagerMainActivity();
                                finish();

                                mRegisterBtn.setEnabled(true);
                            } else if(obj.getInt("status") == -1){
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                mRegisterBtn.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        mRegisterBtn.setEnabled(true);
                        Toast.makeText(ManagerRegisterActivity.this, anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToManagerMainActivity() {
        startActivity(new Intent(this, ManagerMainActivity.class));
    }

}