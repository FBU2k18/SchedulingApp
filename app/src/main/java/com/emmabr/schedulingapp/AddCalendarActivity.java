package com.emmabr.schedulingapp;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.io.IOException;
import java.util.Collections;

public class AddCalendarActivity extends AppCompatActivity {

    EditText mEventName;
    Button mCreateEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_calendar);

        DisplayMetrics displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getSupportActionBar().hide();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .80), (int) (height * .20));

        mEventName = findViewById(R.id.etEventName);
        mCreateEvent = findViewById(R.id.btCreateEvent);
        final String busyTime = (String) getIntent().getExtras().get("mBusyTime");

        mCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(),
                        Collections.singleton("https://www.googleapis.com/auth/calendar"));
                GoogleSignInAccount accountGoogle = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if (accountGoogle != null) {
                    credential.setSelectedAccount(new Account(accountGoogle.getEmail().toString(), "com.emmabr.schedulingapp"));
                }
                HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                final Calendar service = new Calendar.Builder(httpTransport, JacksonFactory.getDefaultInstance(), credential)
                        .setApplicationName("SchedulingApp").build();
                final Event eventToCal = new Event().setSummary(mEventName.getText().toString());
                String totalTime = busyTime;
                int cutOff = totalTime.indexOf(" ");
                int endCutOff = totalTime.lastIndexOf(" ");
                String startTime = totalTime.substring(0, cutOff);
                String endTime = totalTime.substring(endCutOff + 1);
                DateTime startEventDT = new DateTime("2018-04-10T" + startTime + "-07:00");
                EventDateTime start = new EventDateTime()
                        .setDateTime(startEventDT)
                        .setTimeZone("America/Los_Angeles");
                eventToCal.setStart(start);
                DateTime endEventDT = new DateTime("2018-04-10T" + endTime + "-07:00");
                EventDateTime end = new EventDateTime()
                        .setDateTime(endEventDT)
                        .setTimeZone("America/Los_Angeles");
                eventToCal.setEnd(end);

                @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Event> eventCreation = new AsyncTask<Void, Void, Event>() {
                    @Override
                    protected Event doInBackground(Void... voids) {
                        try {
                            return service.events().insert("primary", eventToCal).execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Event event) {
                        if (event != null) {
                            Toast.makeText(getApplicationContext(), "Event added to personal calendar!", Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                eventCreation.execute();

            }
        });
    }
}
