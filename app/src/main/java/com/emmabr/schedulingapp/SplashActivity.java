package com.emmabr.schedulingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import me.emmabr.schedulingapp.R;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2500;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                Intent splashIntent;
                if(currentUser == null) {
                    splashIntent = new Intent(SplashActivity.this, LoginActivity.class);
                }else{
                    splashIntent = new Intent(SplashActivity.this, MainActivity.class);
                }
                startActivity(splashIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
