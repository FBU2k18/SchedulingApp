package com.emmabr.schedulingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // shared Firebase object
    private FirebaseAuth mAuth;

    private Button mLogin;
    private TextView mTVRegister;
    private TextView mLIEmail;
    private TextView mLIPassword;
    private AnimationDrawable mAnimationDrawable;
    private ConstraintLayout mRelativeLayout;


    private ProgressDialog mLoginProgress;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currUser = mAuth.getCurrentUser();
        updateUI(currUser);

        //start the gradient animation
        mAnimationDrawable.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        // gradient stuff
        mRelativeLayout = findViewById(R.id.relativeLayout);
        mAnimationDrawable = (AnimationDrawable) mRelativeLayout.getBackground();
        mAnimationDrawable.setEnterFadeDuration(5000);
        mAnimationDrawable.setExitFadeDuration(2000);


        mLogin = findViewById(R.id.btLogIn);
        mLIEmail = findViewById(R.id.tvEmail);
        mLIPassword = findViewById(R.id.tvPassword);
        mTVRegister = findViewById(R.id.tvRegister);

        mAuth = FirebaseAuth.getInstance();

        mLoginProgress = new ProgressDialog(this);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = mLIEmail.getText().toString();
                final String password = mLIPassword.getText().toString();

                //mLogin a user
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    loginUser(email, password);
                }
            }
        });

        mTVRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send to another activity to create an account
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateUI(FirebaseUser userInfo) {
        if (userInfo != null) {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    mLoginProgress.dismiss();
                    updateUI(mAuth.getCurrentUser());
                } else {
                    mLoginProgress.hide();
                    Toast.makeText(LoginActivity.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}