package com.example.watchteacher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
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

public class AddTeacherActivity extends AppCompatActivity {

    EditText mNameET, mPhoneET, mEmailET, mPasswordET;
    Button mSaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        bindViews();

        mSaveBtn.setOnClickListener(v -> {
            mSaveBtn.setEnabled(false);
            if(validateUserData()){
                addNewTeacher();
            }
        });


    }

    private void bindViews() {
        mNameET = findViewById(R.id.full_name);
        mPhoneET = findViewById(R.id.phone);
        mEmailET = findViewById(R.id.email);
        mPasswordET = findViewById(R.id.password);
        mSaveBtn = findViewById(R.id.save_btn);
    }

    private boolean validateUserData() {

        //first getting the values
        final String email = mEmailET.getText().toString();
        final String pass = mPasswordET.getText().toString();
        final String name = mNameET.getText().toString();
        final String phone = mPhoneET.getText().toString();

        //checking if username is empty
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "please enter teacher's full name !", Toast.LENGTH_SHORT).show();
            mSaveBtn.setEnabled(true);
            return false;
        }

        //checking if password is empty
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "please enter teacher's phone number!", Toast.LENGTH_SHORT).show();
            mSaveBtn.setEnabled(true);
            return false;
        }

        //checking if username is empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "please enter teacher's email address!", Toast.LENGTH_SHORT).show();
            mSaveBtn.setEnabled(true);
            return false;
        }

        //checking if password is empty
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "please enter teacher's password!", Toast.LENGTH_SHORT).show();
            mSaveBtn.setEnabled(true);
            return false;
        }

        return true;
    }

    private void addNewTeacher() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        //first getting the values
        final String email = mEmailET.getText().toString();
        final String pass = mPasswordET.getText().toString();
        final String name = mNameET.getText().toString();
        final String phone = mPhoneET.getText().toString();

        AndroidNetworking.post("http://nawar.scit.co/oup/school-reports/api/auth/signup.php")
                .addBodyParameter("admin_id", String.valueOf(SharedPrefManager.getInstance(AddTeacherActivity.this).getUserId()))
                .addBodyParameter("name", name)
                .addBodyParameter("phone_number", phone)
                .addBodyParameter("email", email)
                .addBodyParameter("password", pass)
                .addBodyParameter("lat", "0.0")
                .addBodyParameter("lng", "0.0")
                .addBodyParameter("type", String.valueOf(Constants.TEACHER))
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

                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //storing the user in shared preferences
                                finish();

                                mSaveBtn.setEnabled(true);
                            } else if(obj.getInt("status") == -1){
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                mSaveBtn.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            mSaveBtn.setEnabled(true);
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        mSaveBtn.setEnabled(true);
                        Toast.makeText(AddTeacherActivity.this, anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


}