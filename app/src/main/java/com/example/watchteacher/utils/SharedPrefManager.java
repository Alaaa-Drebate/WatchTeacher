package com.example.watchteacher.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.example.watchteacher.model.LatLon;
import com.example.watchteacher.model.User;

public class SharedPrefManager {

    private static final String KEY_ID = "keyid";
    private static final String SHARED_PREF_NAME = "generalFile";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_PHONE = "keyphone";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_MANAGER_ID = "manager_id";

    //for manager only
    private static final String KEY_LOCATION_SET = "locationSet";

    private static SharedPrefManager mInstance;
    private static Context context;

    private SharedPrefManager(Context context) {
        SharedPrefManager.context = context;
    }
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //this method will store the user data in shared preferences
    //customer
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.apply();
    }

    public void teacherLogin(User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putInt(KEY_MANAGER_ID, user.getManagerId());
        editor.apply();
    }


    //this method will checker whether user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ID, -1) != -1;
    }

    public void setUserType(int type){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_TYPE, type);
        editor.apply();
    }

    public int getUserType(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_TYPE, -1);
    }

    //this method will give the logged in user id
    public int getUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ID, -1);
    }

    //this method will give the logged in user id
    public int getManagerId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_MANAGER_ID, -1);
    }

    public void setSchoolLocation(boolean location){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_LOCATION_SET, location);
        editor.apply();
    }

    public void setSchoolLocation(double lat, double lon){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LAT, String.valueOf(lat));
        editor.putString(KEY_LON, String.valueOf(lon));
        editor.apply();
    }

    public boolean isLocationSet(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_LOCATION_SET, false);
    }

    public LatLon getSchoolLocation(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new LatLon(Double.parseDouble(sharedPreferences.getString(KEY_LAT, "200")), Double.parseDouble(sharedPreferences.getString(KEY_LON, "200")));
    }

    //this method will give the logged in user
    public User getUserData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_ID, -1),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_PHONE, null)
        );
    }





    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
    }

    public void updateUser(User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.apply();
    }



}
