package com.example.watchteacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.watchteacher.managerfragments.MapFragment;
import com.example.watchteacher.managerfragments.MyAccountFragment;
import com.example.watchteacher.managerfragments.NotificationsFragment;
import com.example.watchteacher.managerfragments.ReportProblemFragment;
import com.example.watchteacher.managerfragments.TeachersFragment;
import com.example.watchteacher.utils.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ManagerMainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    MyAccountFragment myAccountFragment;
    TeachersFragment teachersFragment;
    NotificationsFragment notificationsFragment;
    MapFragment mapFragment;
    ReportProblemFragment reportProblemFragment;


    FloatingActionButton mAddNewCatFab;

    FragmentManager fm;

    boolean outOfMainFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        //this instruction is very important so that bottom bar show as supposed to be
        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        mAddNewCatFab = findViewById(R.id.add_new_cat_btn);

        myAccountFragment = new MyAccountFragment();
        teachersFragment = new TeachersFragment();
        notificationsFragment = new NotificationsFragment();
        mapFragment = new MapFragment();
        reportProblemFragment = new ReportProblemFragment();


        fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.content_main, notificationsFragment, NotificationsFragment.TAG).commit();
        bottomNavigationView.setSelectedItemId(R.id.placeholder);


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> setSuitableView(item));

        mAddNewCatFab.setOnClickListener(v -> {
            if(SharedPrefManager.getInstance(this).isLocationSet()){
                Toast.makeText(this, "school location is not set yet \ngo to --->\nmy account\nupdate school location button", Toast.LENGTH_SHORT).show();
            }else{
                addNewTeacher();
            }
        });


    }

    private void addNewTeacher() {
        startActivity(new Intent(this, AddTeacherActivity.class));
    }

    private boolean setSuitableView(MenuItem item) {

        switch (item.getItemId()){
            case R.id.my_account:
                fm.beginTransaction().replace(R.id.content_main, myAccountFragment, MyAccountFragment.TAG).commit();
                outOfMainFragment = true;
                return true;
            case R.id.teachers:
                fm.beginTransaction().replace(R.id.content_main, teachersFragment, TeachersFragment.TAG).commit();
                outOfMainFragment = true;
                return true;
            case R.id.map:
                fm.beginTransaction().replace(R.id.content_main, mapFragment, MapFragment.TAG).commit();
                outOfMainFragment = true;
                return true;
            case R.id.report_problem:
                fm.beginTransaction().replace(R.id.content_main, reportProblemFragment, ReportProblemFragment.TAG).commit();
                outOfMainFragment = true;
                return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {

        if(outOfMainFragment){
            outOfMainFragment = false;
            fm.beginTransaction().replace(R.id.content_main, notificationsFragment, NotificationsFragment.TAG).commit();
            bottomNavigationView.setSelectedItemId(R.id.placeholder);
        }else{
            super.onBackPressed();
        }

    }
}