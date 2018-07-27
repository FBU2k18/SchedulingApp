package com.emmabr.schedulingapp;
import android.accounts.Account;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.util.ArrayList;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import com.emmabr.schedulingapp.R;


public class CalendarClient extends AppCompatActivity {

    FreeBusyResponse fbresponse;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    //private final String TOKENS_DIRECTORY_PATH = GoogleSignIn.getLastSignedInAccount(this).getIdToken();
    //ArrayList<com.google.api.services.calendar.model.Calendar> allCalendars;

    // calendar Test
    TextView mCalendarText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_client);

        mCalendarText = findViewById(R.id.tvTestCalendar);
        try {
            AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                String finalCalDetails = null;

                @Override
                protected String doInBackground(Void... voids) {
                    try {
                        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(),
                                Collections.singleton("https://www.googleapis.com/auth/calendar"));
                        GoogleSignInAccount accountGoogle = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                        if (accountGoogle != null) {
                            credential.setSelectedAccount(new Account(accountGoogle.getEmail().toString(), "com.emmabr.schedulingapp"));
                        }
                        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                        final Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                                .setApplicationName("SchedulingApp").build();
                        com.google.api.services.calendar.model.Calendar newCalender = getUserCalendar(service);
                        finalCalDetails = findFreeTime(newCalender, service);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return finalCalDetails;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if (s != null) {
                        mCalendarText.setText(s);
                    }
                }

//            mCalendarText.setText(temp);
//        } catch (IOException e) {
//            e.printStackTrace();
            };
            task.execute();
        } catch(Exception e){
                e.printStackTrace();
            }
    }

    public com.google.api.services.calendar.model.Calendar getUserCalendar(Calendar service) throws IOException {
            com.google.api.services.calendar.model.Calendar calendar =
                    service.calendars().get("primary").execute();
            return calendar;
    }

    public String findFreeTime(com.google.api.services.calendar.model.Calendar inputCalender, Calendar service) throws Exception {
        ArrayList<FreeBusyRequestItem> totalCalendars = new ArrayList<>();
        totalCalendars.add(new FreeBusyRequestItem().setId(inputCalender.getId()));
        String testStartTime = "2018-04-10 8:00:00";
        String testEndTime = "2018-04-10 20:00:00";

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = df.parse(testStartTime);
        DateTime startTime = new DateTime(d, TimeZone.getDefault());

        Date de = df.parse(testEndTime);
        DateTime endTime = new DateTime(de, TimeZone.getDefault());

        FreeBusyRequest req = new FreeBusyRequest();
        req.setItems(totalCalendars);
        req.setTimeMin(startTime);
        req.setTimeMax(endTime);

        fbresponse = service.freebusy().query(req).execute();
        return fbresponse.toString();



    }
}

// TODO list below
// connect everyone's firebase with correct authentication
//
// switch log in and registration to include Google + Firebase Authentication (finish this before lunch)
//
// Look into maybe putting everything into one master calendar to find the free busy times? (try to do this tomorrow)
//
// Change the findUserCalendar and findFreeTime function by getting a list of emails from the group,
// for each email, getting the credentials and calling findUserCalendar, adding that calendar as a
// FreeBusyRequestItem in an array list of FreeBusyRequestItems, adding the FBI array into the FB request,
// and calling the request (next week task should be populating Available Times)
