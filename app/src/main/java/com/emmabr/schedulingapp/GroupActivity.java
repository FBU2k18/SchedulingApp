package com.emmabr.schedulingapp;

import android.accounts.Account;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmabr.schedulingapp.Models.Message;
import com.emmabr.schedulingapp.model.TimeOption;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.emmabr.schedulingapp.Models.Message.saveMessage;


public class GroupActivity extends AppCompatActivity implements LeaveGroupDialogFragment.LeaveGroupDialogFragmentListener {

    private String groupID;

    private ArrayList<TimeOption> mTimes;
    private TimeOptionAdapter mTimeAdapter;
    private RecyclerView rvTimes;

    private ArrayList<Message> mMessages;
    private MessageAdapter mMesssageAdapter;
    private RecyclerView rvMessageDisplay;

    private EditText etMessage;
    private FrameLayout flPeeker;
    private BottomSheetBehavior peekerBehavior;
    private ImageView ivAddOther;
    private boolean mAddOtherIsPlus;
    private TextView tvTitle;
    private Button bSend;

    private FrameLayout flOtherTypes;
    private BottomSheetBehavior otherTypesBehavior;
    private Button bAddPoll;
    private Button bTakePic;
    private Button bAddPic;
    ArrayList<TimeOption> times;
    TimeOptionAdapter timeAdapter;
    ArrayList<Message> messages;
    MessageAdapter messsageAdapter;
    BottomSheetBehavior behavior;

    // creating credentials
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "credentials.json";
    private static final String APPLICATION_NAME = "ScheduleMe";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // getting users ids for email
    ArrayList<String> calendarUserIds;
    ArrayList<String> userCalendars;
    ArrayList<String> userBusyTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        groupID = getIntent().getStringExtra("groupID");

        mTimes = new ArrayList<>();
        mTimeAdapter = new TimeOptionAdapter(mTimes, this);
        rvTimes = findViewById(R.id.rvTimes);
        rvTimes.setAdapter(mTimeAdapter);
        rvTimes.setLayoutManager(new LinearLayoutManager(this));
        rvTimes.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mMessages = new ArrayList<>();
        mMesssageAdapter = new MessageAdapter(mMessages, groupID);
        rvMessageDisplay = findViewById(R.id.rvMessageDisplay);
        rvMessageDisplay.setAdapter(mMesssageAdapter);
        rvMessageDisplay.setLayoutManager(new LinearLayoutManager(this));

        etMessage = findViewById(R.id.etMessage);
        flPeeker = findViewById(R.id.flPeeker);
        peekerBehavior = BottomSheetBehavior.from(flPeeker);
        if (getIntent().getBooleanExtra("up", false))
            peekerBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        ivAddOther = findViewById(R.id.ivAddOther);
        ivAddOther.setImageDrawable(getDrawable(android.R.drawable.ic_input_add));
        mAddOtherIsPlus = true;
        ivAddOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAddOtherIsPlus) {
                    ivAddOther.setImageDrawable(getDrawable(android.R.drawable.ic_delete));
                    otherTypesBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    ivAddOther.setImageDrawable(getDrawable(android.R.drawable.ic_input_add));
                    otherTypesBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                mAddOtherIsPlus = !mAddOtherIsPlus;
            }
        });
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (peekerBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    peekerBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else if (peekerBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    peekerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        bSend = findViewById(R.id.bSend);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etMessage.getText().toString().equals("")) {
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("nickName").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Date date = new Date();
                            Message message = new Message(FirebaseAuth.getInstance().getUid(), dataSnapshot.getValue().toString(), etMessage.getText().toString(), null, null, Long.toString(date.getTime()));
                            saveMessage(message, groupID);
                            etMessage.setText("");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });

        flOtherTypes = findViewById(R.id.flOtherTypes);
        otherTypesBehavior = BottomSheetBehavior.from(flOtherTypes);
        otherTypesBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        otherTypesBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (otherTypesBehavior.getState() == BottomSheetBehavior.STATE_DRAGGING)
                    otherTypesBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });
        bAddPoll = findViewById(R.id.bAddPoll);
        bAddPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, PostPollActivity.class);
                intent.putExtra("groupID", groupID);
                startActivity(intent);
            }
        });
        bTakePic = findViewById(R.id.bTakePic);
        bTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, PostPhotoActivity.class);
                intent.putExtra("Taking a new picture?", true);
                intent.putExtra("groupID", groupID);
                startActivity(intent);
            }
        });
        bAddPic = findViewById(R.id.bAddPic);
        bAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, PostPhotoActivity.class);
                intent.putExtra("Taking a new picture?", false);
                intent.putExtra("groupID", groupID);
                startActivity(intent);
            }
        });

        // get busy times from Firebase
        userBusyTimes = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("Recipients")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childData : dataSnapshot.getChildren()) {
                            String userUniqID = childData.getKey();
                            String busyTimes = FirebaseDatabase.getInstance().getReference().child("users")
                                    .child(userUniqID).child("calendar").toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("GroupActivity", "User BusyTime could not be retrieved from Firebase");
                    }
                });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getTimes();

        getMessages();
    }

    public void getTimes() {
        //will pull times from Firebase, but testing for now
        for (int i = 0; i < 10; i++)
            mTimes.add(TimeOption.newTime());
        Collections.sort(mTimes);
        mTimeAdapter.notifyDataSetChanged();
    }

    public void getMessages() {
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("chatMessages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessages.clear();
                mMesssageAdapter.notifyDataSetChanged();
                for (DataSnapshot childData : dataSnapshot.getChildren()) {
                    String userID = childData.child("userID").getValue().toString();
                    String nickName = childData.child("nickName").getValue().toString();
                    String messageText = null;
                    String imageURL = null;
                    String pollTitle = null;
                    String createdAt = childData.child("createdAt").getValue().toString();
                    if (childData.child("messageText").getValue() != null)
                        messageText = childData.child("messageText").getValue().toString();
                    else if (childData.child("imageURL").getValue() != null)
                        imageURL = childData.child("imageURL").getValue().toString();
                    else if (childData.child("pollTitle").getValue() != null)
                        pollTitle = childData.child("pollTitle").getValue().toString();
                    String messageID = childData.getKey().toString();
                    Message message = new Message(userID, nickName, messageText, imageURL, pollTitle, createdAt);
                    message.setMessageID(messageID);
                    mMessages.add(message);
                    mMesssageAdapter.notifyItemInserted(mMessages.size() - 1);
                    rvMessageDisplay.scrollToPosition(mMessages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intentHome = new Intent(this, MainActivity.class);
                startActivity(intentHome);
                finish();
                break;
            case R.id.miAddMember:
                Intent intentAdd = new Intent(this, AddMemberActivity.class);
                intentAdd.putExtra("groupID", groupID);
                startActivity(intentAdd);
                break;
            case R.id.miRefresh:
                //replace with intent
                Log.i("Menu", "Refresh");
                break;
            case R.id.miLeaveGroup:
                LeaveGroupDialogFragment leaveGroup = new LeaveGroupDialogFragment();
                leaveGroup.show(getSupportFragmentManager(), "LeaveGroup");
                break;
        }
        return true;
    }

    @Override
    public void leaveGroup() {
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("Recipients").child(FirebaseAuth.getInstance().getUid()).removeValue();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("userGroup").child(groupID).removeValue();
        Intent intentLeave = new Intent(this, MainActivity.class);
        startActivity(intentLeave);
        finish();
    }

    public void scrollToPosition(int position) {
        rvTimes.scrollToPosition(position);
    }

    public ArrayList<JSONObject> getFreeTimes(ArrayList<String> freeBusyTimes) throws JSONException, IOException, ParseException {
        ArrayList<com.google.api.services.calendar.model.Calendar> storeCal = new ArrayList<>();
        ArrayList<JSONObject> totalBusyTimes = new ArrayList<>();
        ArrayList<JSONObject> totalFreeTimes = new ArrayList<>();
        // adding all users' busy times into an array of JSON objects
        for (String eachTime : freeBusyTimes) {
            JSONObject userUniqTime = new JSONObject(eachTime);
            JSONArray busyTimes = (JSONArray) ((JSONObject) ((JSONObject) userUniqTime.get("calendars"))
                    .get("krithikai@gmail.com")).get("busy");
            for (int i = 0; i < busyTimes.length(); i++) {
                totalBusyTimes.add(busyTimes.getJSONObject(i));
            }
        }
        // creating calendar connection
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getApplicationContext(),
                Collections.singleton("https://www.googleapis.com/auth/calendar"));
        GoogleSignInAccount accountGoogle = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (accountGoogle != null) {
            credential.setSelectedAccount(new Account(accountGoogle.getEmail().toString(), "com.emmabr.schedulingapp"));
        }
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        final Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName("SchedulingApp").build();

        com.google.api.services.calendar.model.Calendar dayCal = new com.google.api.services.calendar.model.Calendar();

        // adding an event at each minute to the 7 days of calendars
            for (int k = 10; k < 18; k++) {
                String currDate = "2018-04-" + Integer.toString(k) + "T";
                String currNextDate = "2018-04-" + Integer.toString(k+1) +"T";
                String iString = null;
                String nextString = null;
                String jString = null;
                String jPlusOne = null;
                for (int i = 0; i < 24; i++) {
                    if (i < 10) {
                        iString = "0" + Integer.toString(i);
                        if (i + 1 < 10) {
                            nextString = "0" + Integer.toString(i + 1);
                        } else {
                            nextString = Integer.toString(i + 1);
                        }
                    } else {
                        iString = Integer.toString(i);
                        nextString = Integer.toString(i+1);
                    }
                    for (int j = 0; j < 60; j++) {
                        DateTime endDateTime;
                        Event event = new Event();
                        if (j < 10) {
                            jString = "0" + Integer.toString(j);
                            if (j+1 < 10) {
                                jPlusOne = "0" + Integer.toString(j + 1);
                            } else {
                                jPlusOne = Integer.toString(j + 1);
                            }
                        } else {
                            jString = Integer.toString(j);
                            jPlusOne = Integer.toString(j + 1);
                        }
                        // setting start time for each event
                        DateTime startDateTime = new DateTime(currDate + iString + ":" + jString + ":00-07:00");
                        EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone("America/Los_Angeles");
                        event.setStart(start);
                        if (i < 10) {
                            iString = "0" + Integer.toString(i);
                        }

                        event.setId(currDate + iString + ":" + jString + ":00-07:00");
                        // setting end time for each event
                        if (j + 1 == 60) {
                            if (i + 1 == 24) {
                                endDateTime = new DateTime(currNextDate + "00:00:00-07:00");
                            } else {
                                endDateTime = new DateTime(currDate + nextString + ":" + "00:00-07:00");
                            }
                        } else {
                            endDateTime = new DateTime("2018-04-10T" + iString + ":" + jPlusOne + ":00-07:00");
                        }
                        EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone("America/Los_Angeles");
                        event.setEnd(end);
                        // adding the event to the calendar
                        service.events().insert(dayCal.getId(), event).execute();
                    }
                }
            }

            // get an arraylist of all the events in the new calendar
            Events eventList = service.events().list(dayCal.getId()).execute();
            ArrayList<Event> listEvents = new ArrayList<>();
            for (Event oneEvent : eventList.getItems()) {
                listEvents.add(oneEvent);
            }

        // delete an event any time a user is busy
        for (JSONObject userBusy : totalBusyTimes) {
                int startPlace = 0;
                int endPlace = 0;
            String startTime = (String) userBusy.get("start");
            String endTime = (String) userBusy.get("end");
            for (Event currEvent : listEvents) {
                if (startTime.contentEquals(currEvent.getId())) {
                    startPlace = listEvents.indexOf(currEvent);
                }
                if (endTime.contentEquals(currEvent.getId())) {
                    endPlace = listEvents.indexOf(currEvent);
                }
                for (int l = startPlace; l < endPlace; l++) {
                    service.events().delete(dayCal.getId(), listEvents.get(l).getId());
                }
            }
        }

        String finalStartTime = "2018-04-10 08:00:00";
        String finalEndTime = "2018-04-17 08:00:00";

        DateFormat dfFinal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dFinal = dfFinal.parse(finalStartTime);
        DateTime startTimeFinal = new DateTime(dFinal, TimeZone.getDefault());

        Date deFinal = dfFinal.parse(finalEndTime);
        DateTime endTimeFinal = new DateTime(deFinal, TimeZone.getDefault());

        FreeBusyRequest fbreq = new FreeBusyRequest();
        ArrayList<FreeBusyRequestItem> finalFreeTime = new ArrayList<>();
        for (com.google.api.services.calendar.model.Calendar eachDayCal : storeCal) {
            finalFreeTime.add(new FreeBusyRequestItem().setId(eachDayCal.getId()));
        }
        fbreq.setItems(finalFreeTime);
        fbreq.setTimeZone("America/Los_Angeles");
        fbreq.setTimeMin(startTimeFinal);
        fbreq.setTimeMax(endTimeFinal);

        FreeBusyResponse fbReponseFinal = service.freebusy().query(fbreq).setFields("calendars").execute();
        String finalResponse = fbReponseFinal.toString();
        JSONObject fbResponseFinalObject = new JSONObject(finalResponse);
        for (com.google.api.services.calendar.model.Calendar freeTimeCals : storeCal) {
            JSONArray tempArray = (JSONArray) (((JSONObject) ((JSONObject) fbResponseFinalObject.get("calendars")).get(freeTimeCals.getId())).get("busy"));
            for (int i = 0; i < tempArray.length(); i++) {
                totalFreeTimes.add(tempArray.getJSONObject(i));
            }
        }

        return totalFreeTimes;
    }
}

// TODO - following list
// hardcode in correct dates
// test everything works correctly
// function to delete the 7 calendars at the end of calling the freebusy function