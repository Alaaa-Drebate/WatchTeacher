package com.example.watchteacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.watchteacher.managerfragments.MyAccountFragment;
import com.example.watchteacher.services.TrackWorker;
import com.example.watchteacher.services.TrackerService;
import com.example.watchteacher.teacherfragments.MyReportsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;


public class TeacherMainActivity extends AppCompatActivity {

    TextView mTitle;
    BottomNavigationView mNavigationBar;

    MyReportsFragment mMyReportsFragment;
    MyAccountFragment mMyAccountFragment;

    FragmentManager fm;


    //for trial
    Intent mServiceIntent;
    private TrackerService mTrackerService;
    private static final String TAG = TeacherMainActivity.class.getSimpleName();


    boolean outOfMainFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);

        mNavigationBar = findViewById(R.id.bottom_nav_bar);

        mTitle = findViewById(R.id.title);

        mMyAccountFragment = new MyAccountFragment();
        mMyReportsFragment = new MyReportsFragment();

        fm = getSupportFragmentManager();

        if(savedInstanceState == null){
            //setting the first destination
            fm.beginTransaction().add(R.id.fragment_container, mMyReportsFragment, MyReportsFragment.TAG).commit();
            mNavigationBar.setSelectedItemId(R.id.my_reports);
            mTitle.setVisibility(View.VISIBLE);
        }

        //setting up the bottom navigation listener for navigating events
        mNavigationBar.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){

                case R.id.my_account:
                    outOfMainFragment = true;
                    fm.beginTransaction().replace(R.id.fragment_container, mMyAccountFragment, MyAccountFragment.TAG).commit();
                    mTitle.setText(getResources().getString(R.string.my_account));
                    return true;
                case R.id.my_reports:
                    outOfMainFragment = false;
                    fm.beginTransaction().replace(R.id.fragment_container, mMyReportsFragment, MyReportsFragment.TAG).commit();
                    mTitle.setText(getResources().getString(R.string.my_reports));
                    return true;
            }
            return false;
        });


        PeriodicWorkRequest trackUser =
                new PeriodicWorkRequest.Builder(TrackWorker.class, 15, TimeUnit.SECONDS)
                        .setInitialDelay(1, TimeUnit.SECONDS)
                        .addTag("tracker")
                        .setBackoffCriteria(BackoffPolicy.LINEAR,PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                        // Constraints
                        .build();

        WorkManager
                .getInstance(this)
                .enqueueUniquePeriodicWork(TAG, ExistingPeriodicWorkPolicy.KEEP , trackUser);


    }




    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");

        // ondestroy service not being called
//        stopService(mServiceIntent);

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(outOfMainFragment){
            outOfMainFragment = false;
            fm.beginTransaction().replace(R.id.fragment_container, mMyReportsFragment, MyReportsFragment.TAG).commit();
            mTitle.setText(getResources().getString(R.string.my_reports));
            mNavigationBar.setSelectedItemId(R.id.my_reports);
        }else {
            finish();
        }
    }
}