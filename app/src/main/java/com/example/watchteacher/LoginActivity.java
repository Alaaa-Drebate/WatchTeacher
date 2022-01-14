package com.example.watchteacher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.watchteacher.api.Constants;
import com.example.watchteacher.model.User;
import com.example.watchteacher.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText mEmailET, mPasswordET;
    Button mLoginBtn;
    TextView mLoginSignUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            switch (SharedPrefManager.getInstance(this).getUserType()){
                case Constants.MANAGER:
                    startActivity(new Intent(this, ManagerMainActivity.class));
                    finish();
                    break;
                case Constants.TEACHER:
                    startActivity(new Intent(this, TeacherMainActivity.class));
                    finish();
                    break;
            }
        }

        bindViews();

        mLoginSignUpBtn.setOnClickListener(v -> {
            LayoutInflater factory = LayoutInflater.from(this);
            final View view = factory.inflate(R.layout.enter_manager_key_dialog, null);
            final AlertDialog enterManagerKeyDialog = new AlertDialog.Builder(this).create();
            enterManagerKeyDialog.setCancelable(true);
            enterManagerKeyDialog.setView(view);

            EditText keyET = view.findViewById(R.id.key);
            TextView save = view.findViewById(R.id.save);
            TextView cancel = view.findViewById(R.id.cancel);


            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String key = keyET.getText().toString();
                    String managerKey = getResources().getString(R.string.manager_key);

                    if(TextUtils.isEmpty(key)){
                        Toast.makeText(LoginActivity.this, "manager key must not be empty!", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        if(TextUtils.equals(key, managerKey)){
                            Toast.makeText(LoginActivity.this, "welcome sir", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, ManagerRegisterActivity.class));
                            enterManagerKeyDialog.dismiss();
                        }else{
                            Toast.makeText(LoginActivity.this, "wrong key! please try again", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterManagerKeyDialog.dismiss();
                }
            });
            enterManagerKeyDialog.show();

        });

        mLoginBtn.setOnClickListener(v -> {
            mLoginBtn.setEnabled(false);
            if(validateUserData()){
                userLogin();
            }
        });

    }



    private void bindViews() {
        mEmailET = findViewById(R.id.email);
        mPasswordET = findViewById(R.id.password);
        mLoginBtn = findViewById(R.id.login_btn);
        mLoginSignUpBtn = findViewById(R.id.login_signup_btn);
    }

    private boolean validateUserData() {

        //first getting the values
        final String email = mEmailET.getText().toString();
        final String pass = mPasswordET.getText().toString();

        //checking if username is empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "please enter your email address!", Toast.LENGTH_SHORT).show();
            mLoginBtn.setEnabled(true);
            return false;
        }

        //checking if password is empty
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "please enter your password!", Toast.LENGTH_SHORT).show();
            mLoginBtn.setEnabled(true);
            return false;
        }

        return true;

    }


    private void userLogin() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        //first getting the values
        final String email = mEmailET.getText().toString();
        final String pass = mPasswordET.getText().toString();

        AndroidNetworking.post("http://nawar.scit.co/oup/school-reports/api/auth/login.php")
                .addBodyParameter("email", email)
                .addBodyParameter("password", pass)
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

                                JSONObject userJson = obj.getJSONObject("data");

                                int userType = userJson.getInt("type");
                                double lat, lon;
                                //getting the user from the response
                                User user;
                                switch (userType){
                                    case Constants.MANAGER:
                                        //getting the user from the response
                                        SharedPrefManager.getInstance(getApplicationContext()).setUserType(Constants.MANAGER);
                                        user = new User(
                                                Integer.parseInt(userJson.getString("id")),
                                                userJson.getString("name"),
                                                userJson.getString("email"),
                                                userJson.getString("phone_number")
                                        );
                                        Toast.makeText(LoginActivity.this, "welcome manager "+user.getId(), Toast.LENGTH_SHORT).show();
                                        //storing the user in shared preferences
                                        SharedPrefManager.getInstance(LoginActivity.this).userLogin(user);

                                        lat = Double.parseDouble(userJson.getString("lat"));
                                        lon = Double.parseDouble(userJson.getString("lng"));
                                        SharedPrefManager.getInstance(LoginActivity.this).setSchoolLocation(true);
                                        SharedPrefManager.getInstance(LoginActivity.this).setSchoolLocation(lat, lon);
                                        goToManagerMainActivity();
                                        finish();
                                        mLoginBtn.setEnabled(true);
                                        break;
                                    case Constants.TEACHER:

                                        SharedPrefManager.getInstance(getApplicationContext()).setUserType(Constants.TEACHER);
                                        user = new User(
                                                Integer.parseInt(userJson.getString("id")),
                                                userJson.getString("name"),
                                                userJson.getString("email"),
                                                userJson.getString("phone_number"),
                                                Integer.parseInt(userJson.getString("admin_id"))
                                        );
                                        //storing the user in shared preferences
                                        SharedPrefManager.getInstance(LoginActivity.this).teacherLogin(user);

                                        JSONObject school = userJson.getJSONObject("school") ;
                                        lat = Double.parseDouble(school.getString("lat"));
                                        lon = Double.parseDouble(school.getString("lng"));
                                        Toast.makeText(LoginActivity.this, lat+""+lon, Toast.LENGTH_SHORT).show();
                                        SharedPrefManager.getInstance(LoginActivity.this).setSchoolLocation(true);
                                        SharedPrefManager.getInstance(LoginActivity.this).setSchoolLocation(lat, lon);
                                        goToTeacherMainActivity();
                                        finish();
                                        mLoginBtn.setEnabled(true);
                                        break;
                                }

                            } else if(obj.getInt("status") == -1){
                                Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                mLoginBtn.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            mLoginBtn.setEnabled(true);
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        mLoginBtn.setEnabled(true);
                        Toast.makeText(LoginActivity.this, anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void goToTeacherMainActivity() {
        startActivity(new Intent(this, TeacherMainActivity.class));
    }

    private void goToManagerMainActivity() {
        startActivity(new Intent(this, ManagerMainActivity.class));
    }
}