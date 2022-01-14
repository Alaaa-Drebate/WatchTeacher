package com.example.watchteacher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.watchteacher.api.Constants;
import com.example.watchteacher.model.User;
import com.example.watchteacher.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProfileActivity extends AppCompatActivity {

    int userId;
    String title;
    boolean myAccount = false;

    EditText mNameET, mPhoneET, mEmailET, mPasswordET;
    boolean nameSet = false, phoneSet = false, emailSet = false, passwordSet = false;
    Button mSaveBtn;
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        Intent sender = getIntent();
        if(sender!= null){
            userId = sender.getIntExtra("userId", -1);
            title = sender.getStringExtra("title");
        }

        if(userId == SharedPrefManager.getInstance(this).getUserId()){
            myAccount = true;
        }

        bindViews();

        mSaveBtn.setOnClickListener(v -> {
            mSaveBtn.setEnabled(false);
            validateUserData();
            updateUser();
        });

        if(title != null){
            mTitle.setText(title);
        }
    }


    private void bindViews() {
        mNameET = findViewById(R.id.full_name);
        mPhoneET = findViewById(R.id.phone);
        mEmailET = findViewById(R.id.email);
        mPasswordET = findViewById(R.id.password);
        mSaveBtn = findViewById(R.id.save_btn);
        mTitle = findViewById(R.id.title);
    }

    private void validateUserData() {

        //first getting the values
        final String email = mEmailET.getText().toString();
        final String pass = mPasswordET.getText().toString();
        final String name = mNameET.getText().toString();
        final String phone = mPhoneET.getText().toString();

        //checking if username is not empty
        if (!TextUtils.isEmpty(name)) {
            nameSet = true;
        }

        //checking if password is not empty
        if (!TextUtils.isEmpty(phone)) {
            phoneSet = true;
        }

        //checking if username is not empty
        if (TextUtils.isEmpty(email)) {
            emailSet = true;
        }

        //checking if password is not empty
        if (TextUtils.isEmpty(pass)) {
            passwordSet = true;
        }

    }

    private void updateUser() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        //first getting the values
        final String email = mEmailET.getText().toString();
        final String pass = mPasswordET.getText().toString();
        final String name = mNameET.getText().toString();
        final String phone = mPhoneET.getText().toString();


        ANRequest.PostRequestBuilder networking = AndroidNetworking.post("http://nawar.scit.co/oup/school-reports/api/auth/update-user.php");

        networking.addBodyParameter("user_id", String.valueOf(userId));
        if(emailSet)
            networking.addBodyParameter("email", email);
        if(passwordSet)
            networking.addBodyParameter("password", pass);
        if(nameSet)
            networking.addBodyParameter("name", name);
        if(phoneSet)
            networking.addBodyParameter("phone_number", phone);

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

                                Toast.makeText(EditProfileActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //save offline data only if this is the manager account being updated
                                if(myAccount){
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
                                    SharedPrefManager.getInstance(getApplicationContext()).updateUser(user);
                                }

                                finish();
                                mSaveBtn.setEnabled(true);
                            } else if(obj.getInt("status") == -1){
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                mSaveBtn.setEnabled(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        pDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}