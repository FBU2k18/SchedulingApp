package com.emmabr.schedulingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.emmabr.schedulingapp.Models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import com.emmabr.schedulingapp.R;

public class RegisterActivity extends AppCompatActivity {

    private final static int RC_SIGN_IN = 34;
    GoogleSignInClient mGoogleSignInClient;

    //android variables
    private Button btnNext;
    private EditText etUsername;
    private TextView etEmail;
    private TextView etPassword;

    //progress bar
    private ProgressDialog mRegProgress;

    //firebase auth
    private FirebaseAuth mAuth;

    //firbase database reference
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        btnNext = findViewById(R.id.btnNext);
        etUsername = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send data to firebase
                String username = etUsername.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (!TextUtils.isEmpty(username) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create you account!");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    registerUser(username, email, password);
                }
            }
        });

    }

    public void registerUser(final String username, final String email, final String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // OAuth confirmation when user creates group (in order to access calendar)
                            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken("698336983204-hntnifsf7pgoaje95ce99d51ruh0j9b4.apps.googleusercontent.com")
                                    .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"))
                                    .build();
                            mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
                            signIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            mRegProgress.hide();
                            Log.w("login", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Sign in success, update UI with the signed-in user's information
                Log.d("login", "createUserWithEmail:success");
                // create a new User to put in the Firebase users schema

                FirebaseUser user = mAuth.getInstance().getCurrentUser();
                String uid = user.getUid();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                HashMap<String, String> usersMap = new HashMap<>();
                usersMap.put("nickName", account.getDisplayName());
                usersMap.put("email", account.getEmail());
                usersMap.put("image", "default");
                usersMap.put("calendar", "");
                usersMap.put("id", uid);

                mDatabase.setValue(usersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(RegisterActivity.this, "Account Created!.",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
                // Google Sign In was successful
                Toast.makeText(getApplicationContext(), "Sign in Worked!", Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("GoogleLogIn", "Google sign in failed", e);
            }
        }
    }

}