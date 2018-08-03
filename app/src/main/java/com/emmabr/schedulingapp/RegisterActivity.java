package com.emmabr.schedulingapp;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.emmabr.schedulingapp.Models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import com.emmabr.schedulingapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    // Google Auth
    private final static int RC_SIGN_IN = 34;
    private GoogleSignInClient mGoogleSignInClient;

    //android variables
    private ImageView ivNext;
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

        ivNext = findViewById(R.id.ivNext);
        etUsername = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestScopes(new Scope("https://www.googleapis.com/auth/calendar"))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        ivNext.setOnClickListener(new View.OnClickListener() {
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

                    // OAuth confirmation when user creates group (in order to access calendar)
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
                            FirebaseUser user = mAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                            HashMap<String, String> usersMap = new HashMap<>();
                            usersMap.put("nickName", username);
                            usersMap.put("email", email);
                            usersMap.put("image", "default");
                            //usersMap.put("calendar", email);
                            usersMap.put("uid", uid);
                            mDatabase.setValue(usersMap);
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
        if (requestCode == RC_SIGN_IN) {
            GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                        @Override
                        public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                            if (task.isSuccessful()) {
                                try {
                                    GoogleSignInAccount account = task.getResult(ApiException.class);
                                    FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("token").setValue(account.getIdToken());
                                    @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> firebaseTask = new AsyncTask<Void, Void, String>() {
                                        @Override
                                        protected String doInBackground(Void... voids) {
                                            String tempCalHolder = null;
                                            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(),
                                                    Collections.singleton("https://www.googleapis.com/auth/calendar"));
                                            GoogleSignInAccount accountGoogle = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                                            if (accountGoogle != null) {
                                                credential.setSelectedAccount(new Account(accountGoogle.getEmail().toString(), "com.emmabr.schedulingapp"));
                                            }
                                            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                                            final Calendar service = new Calendar.Builder(httpTransport, JacksonFactory.getDefaultInstance(), credential)
                                                    .setApplicationName("SchedulingApp").build();
                                            try {
                                                com.google.api.services.calendar.model.Calendar tempCalendar = service.calendars().get("primary").execute();
                                                tempCalHolder = findFreeTime(tempCalendar, service);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            return tempCalHolder;
                                        }

                                        @Override
                                        protected void onPostExecute(String s) {
                                            super.onPostExecute(s);
                                            if (s != null) {
                                                FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid())
                                                        .child("calendar").setValue(s);
                                            }
                                        }
                                    };
                                    firebaseTask.execute();
                                } catch (ApiException e) {
                                    Toast.makeText(RegisterActivity.this, "It doesn't work", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
            updateUI(mAuth.getCurrentUser());
        }
    }

    private void updateUI(FirebaseUser userInfo) {
        if (userInfo != null) {
            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(i);
        }
    }

    public String findFreeTime(com.google.api.services.calendar.model.Calendar userCalendar, Calendar service) throws Exception {
        ArrayList<FreeBusyRequestItem> totalCalendars = new ArrayList<>();
        totalCalendars.add(new FreeBusyRequestItem().setId(userCalendar.getId()));
        String testStartTime = "2018-04-10 08:00:00";
        String testEndTime = "2018-04-17 08:00:00";

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = df.parse(testStartTime);
        DateTime startTime = new DateTime(d, TimeZone.getDefault());

        Date de = df.parse(testEndTime);
        DateTime endTime = new DateTime(de, TimeZone.getDefault());

        FreeBusyRequest req = new FreeBusyRequest();
        req.setItems(totalCalendars);
        req.setTimeZone("America/Los_Angeles");
        req.setTimeMin(startTime);
        req.setTimeMax(endTime);

        FreeBusyResponse fbresponse = service.freebusy().query(req).setFields("calendars").execute();
        String tempResponse = fbresponse.toString();
        return tempResponse;
    }
}