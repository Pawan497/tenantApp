package com.pawanyadav497.tenantapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.pawanyadav497.tenantapp.fragments.HomeFragment;
import com.pawanyadav497.tenantapp.fragments.HomeSignFragment;

public class MainActivity extends AppCompatActivity {

    private boolean isUserSignedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Hide the status bar and navigation bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        // Hide the title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        isUserSignedIn = mAuth.getCurrentUser() != null;

        if( isUserSignedIn){
            showHomeFragement();
        } else {
            showHomeSignInFragment();
        }
    }

    private void showHomeFragement(){
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, homeFragment);
        ft.commit();
    }

    private void showHomeSignInFragment(){
        HomeSignFragment homeSignFragment = new HomeSignFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, homeSignFragment);
        ft.commit();
    }


    public void onSignInSuccessful(){
        isUserSignedIn = true;
        showHomeFragement();
    }
}