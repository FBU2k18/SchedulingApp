//package com.emmabr.schedulingapp;
//
//import android.accounts.Account;
//import android.widget.TextView;
//
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.DateTime;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.model.FreeBusyRequest;
//import com.google.api.services.calendar.model.FreeBusyRequestItem;
//import com.google.api.services.calendar.model.FreeBusyResponse;
//
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.TimeZone;
//
//public class MeetingTime {
//
//    private FreeBusyResponse fbresponse;
//
//
//    // return user's primary calendars
//    public com.google.api.services.calendar.model.Calendar getUserCalendar(Calendar service) throws IOException {
//        com.google.api.services.calendar.model.Calendar calendar =
//                service.calendars().get("primary").execute();
//        return calendar;
//    }
//
//    // returns the times when the calendars are all free
//    public String findFreeTime(ArrayList<String> groupUsersID, Calendar service) throws Exception {
//        ArrayList<FreeBusyRequestItem> totalCalendars = new ArrayList<>();
//        for (String uniqueID: groupUsersID) {
//            totalCalendars.add(new FreeBusyRequestItem().setId(uniqueID));
//        }
//        String testStartTime = "2018-04-10 8:00:00";
//        String testEndTime = "2018-04-10 20:00:00";
//
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date d = df.parse(testStartTime);
//        DateTime startTime = new DateTime(d, TimeZone.getDefault());
//
//        Date de = df.parse(testEndTime);
//        DateTime endTime = new DateTime(de, TimeZone.getDefault());
//
//        FreeBusyRequest req = new FreeBusyRequest();
//        req.setItems(totalCalendars);
//        req.setTimeMin(startTime);
//        req.setTimeMax(endTime);
//
//        this.fbresponse = service.freebusy().query(req).execute();
//        return this.fbresponse.toString();
//    }
//}
