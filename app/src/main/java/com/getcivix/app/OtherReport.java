package com.getcivix.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.getcivix.app.R;

public class OtherReport extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {



    //getting bottom navigation view
    BottomNavigationView navigation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_report);


        navigation = findViewById(R.id.main_nav);
        navigation.setOnNavigationItemSelectedListener(this);


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.map:
                fragment = new MapFragment();
                ((MapFragment) fragment).setThisActivity(this);
                break;

            case R.id.notifications:
                fragment = new ProfileFragment();
                break;

            case R.id.voice:
                fragment = new ProfileFragment();
                break;

            case R.id.profile:
                fragment = new ProfileFragment();
                break;

            case R.id.othermenu:
                fragment = new ProfileFragment();
                break;
        }

        return loadFragment(fragment);
    }



    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
