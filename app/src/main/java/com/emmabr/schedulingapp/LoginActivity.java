package com.emmabr.schedulingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.emmabr.schedulingapp.R;

public class LoginActivity extends AppCompatActivity {

    // Google Auth
    private static final String TAG = "LogInActivity";
    private final static int RC_SIGN_IN = 34;
    private GoogleSignInClient mGoogleSignInClient;

    // shared Firebase object
    private FirebaseAuth mAuth;


    private Button login;
    private TextView tvRegister;
    private TextView liEmail;
    private TextView liPassword;
    private AnimationDrawable animationDrawable;
    private RelativeLayout relativeLayout;

    private ProgressDialog mLoginProgress;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currUser = mAuth.getCurrentUser();
        updateUI(currUser);

        //start the gradient animation
        animationDrawable.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        // gradient stuff
        relativeLayout = findViewById(R.id.relativeLayout);
        animationDrawable =(AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);

        login = findViewById(R.id.btLogIn);
        liEmail = findViewById(R.id.tvEmail);
        liPassword = findViewById(R.id.tvPassword);
        tvRegister = findViewById(R.id.tvRegister);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("698336983204-hntnifsf7pgoaje95ce99d51ruh0j9b4.apps.googleusercontent.com")
                .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

//        mLoginProgress = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String email = liEmail.getText().toString();
                final String password = liPassword.getText().toString();

//                login a user
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
//                    mLoginProgress.setTitle("Logging In");
//                    mLoginProgress.setMessage("Please wait while we check your credentials.");
//                    mLoginProgress.setCanceledOnTouchOutside(false);
//                    mLoginProgress.show();
                    signIn();
//                    loginUser(email, password);
                }
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
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
            startActivity(i);
        }
    }

//    private void loginUser(String email, String password) {
//
//        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                if (task.isSuccessful()) {
//
//                    mLoginProgress.dismiss();
//                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
//                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(mainIntent);
//                    finish();
//
//                } else {
//                    mLoginProgress.hide();
//                    Toast.makeText(LoginActivity.this, "Cannot Sign in. Please check the form and try again.", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
//            try {
            // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//                mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(i);
////                            updateUI(mAuth.getCurrentUser());
//                        }
//                    }
//                });
//            } catch (ApiException e) {
//                Log.w(TAG, "Google sign in failed", e);
//            }
//        }
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}

