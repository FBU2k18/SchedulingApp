package com.emmabr.schedulingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import me.emmabr.schedulingapp.R;

public class RegisterActivity extends AppCompatActivity {

    //android variables
    private ImageView mIVNext;
    private EditText mETUsername;
    private TextView mETEmail;
    private TextView mETPassword;

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

        mIVNext = findViewById(R.id.ivNext);
        mETUsername = findViewById(R.id.etUserName);
        mETEmail = findViewById(R.id.etEmail);
        mETPassword = findViewById(R.id.etPassword);

        mIVNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send data to firebase
                String username = mETUsername.getText().toString();
                String email = mETEmail.getText().toString();
                String password = mETPassword.getText().toString();

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
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("login", "createUserWithEmail:success");
                            // create a new User to put in the Firebase users schema

                            FirebaseUser user = mAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                            HashMap<String, String> usersMap = new HashMap<>();
                            usersMap.put("nickName", username);
                            usersMap.put("email", email);
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

}