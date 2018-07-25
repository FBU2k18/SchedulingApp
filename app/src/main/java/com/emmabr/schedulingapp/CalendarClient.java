package com.emmabr.schedulingapp;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
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

import me.emmabr.schedulingapp.R;


public class CalendarClient extends AppCompatActivity {

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    //ArrayList<com.google.api.services.calendar.model.Calendar> allCalendars;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_client);
        NetHttpTransport httpTransport = null;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                    .setApplicationName("applicationName").build();
            com.google.api.services.calendar.model.Calendar newCalender = getUserCalendar(service);
            findFreeTime(newCalender, service);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
        // Load client secrets.
        InputStream in = Calendar.class.getResourceAsStream("client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singletonList(CalendarScopes.CALENDAR))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public com.google.api.services.calendar.model.Calendar getUserCalendar(Calendar service) throws IOException {
            com.google.api.services.calendar.model.Calendar calendar =
                    service.calendars().get("primary").execute();
            return calendar;
    }

    public void findFreeTime(com.google.api.services.calendar.model.Calendar inputCalender, Calendar service) throws Exception {
        ArrayList<FreeBusyRequestItem> totalCalendars = new ArrayList<>();
        totalCalendars.add(new FreeBusyRequestItem().setId(inputCalender.getId()));
        String testStartTime = "2018-04-05 8:00:00";
        String testEndTime = "2015-04-05 20:00:00";

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = df.parse(testStartTime);
        DateTime startTime = new DateTime(d, TimeZone.getDefault());

        Date de = df.parse(testEndTime);
        DateTime endTime = new DateTime(de, TimeZone.getDefault());

        FreeBusyRequest req = new FreeBusyRequest();
        req.setItems(totalCalendars);
        req.setTimeMin(startTime);
        req.setTimeMax(endTime);

        Calendar.Freebusy.Query fbresponse = service.freebusy().query(req);
        System.out.println(fbresponse.toString());



    }
}
