package com.emmabr.schedulingapp;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.emmabr.schedulingapp.Models.AvailableTime;
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
    ArrayList<String> userBusyTimes;

    // populating available times
    ArrayList<AvailableTime> mAvailTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        groupID = getIntent().getStringExtra("groupID");
        mAvailTimes = new ArrayList<>();

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
        final ArrayList<String> usersIDs = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("Recipients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getMessages();
                try {
                    final ArrayList<ArrayList<JSONObject>> totalFreeTimes = createCalendar();
                    for (DataSnapshot childData : dataSnapshot.getChildren()) {
                        String userID = (String) childData.getValue();
                        usersIDs.add(userID);
                    }
                        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    for (String user : usersIDs) {
                                        for(DataSnapshot childUser : dataSnapshot.getChildren()) {
                                            if (user.contentEquals(childUser.getKey())) {
                                                String userCalendar = (String) childUser.child("calendar").getValue();
                                                userBusyTimes.add(userCalendar);
                                            }
                                        }
                                    }
                                    ArrayList<ArrayList<JSONObject>> updatedTimes = deleteBusyTimes(totalFreeTimes, userBusyTimes, usersIDs);
                                    updateAvailTimes(updatedTimes, 0);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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


    public ArrayList<ArrayList<JSONObject>> createCalendar() throws JSONException {
        ArrayList<ArrayList<JSONObject>> masterCalendar = new ArrayList<>();
        for (int k = 10; k < 18; k++) {
            ArrayList<JSONObject> calendar = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                JSONObject singleHour = new JSONObject();
                String time = null;
                String nextTime = null;
                String dateTime = null;
                String startTime = null;
                String endTime = null;
                String currDate = "2018-04-" + Integer.toString(k) + "T";
                String currNext = "2018-04-" + Integer.toString(k+1) + "T";
                if (i < 10) {
                    time = "0" + Integer.toString(i);
                    if (i + 1 == 10) {
                        nextTime = Integer.toString(i + 1);
                        endTime = currDate + nextTime + ":00:00.000-07:00";

                    } else {
                        nextTime = "0" + Integer.toString(i + 1);
                        endTime = currDate + nextTime + ":00:00.000-07:00";
                    }
                } else {
                    time = Integer.toString(i);
                    if (i + 1 == 24) {
                        endTime = currNext + "00:00:00.000-07:00";
                    } else {
                        nextTime = Integer.toString(i + 1);
                        endTime = currDate + nextTime + ":00:00.000-07:00";
                    }
                }
                startTime = currDate + time + ":00:00.000-07:00";
                singleHour.put("start", startTime);
                singleHour.put("end", endTime);
                calendar.add(singleHour);
            }
            masterCalendar.add(calendar);
        }
        return masterCalendar;
    }

    public ArrayList<ArrayList<JSONObject>> deleteBusyTimes(ArrayList<ArrayList<JSONObject>> calendar, ArrayList<String> uBusyTimes, ArrayList<String> userIds) throws JSONException {
        final ArrayList<JSONObject> totalBusyTimes = new ArrayList<>();
        // adding all users' busy times into an array of JSON objects
        for (int i = 0; i < uBusyTimes.size(); i++) {
            JSONObject userUniqTime = new JSONObject(uBusyTimes.get(i));
            JSONArray busyTimes = new JSONArray();
            if (i == 0) {
                busyTimes = (JSONArray) ((JSONObject) ((JSONObject) userUniqTime.get("calendars"))
                        .get("e2.cornish@gmail.com")).get("busy");
                for (int j = 0; j < busyTimes.length(); j++) {
                    totalBusyTimes.add(busyTimes.getJSONObject(j));
                }
            } else {
                busyTimes = (JSONArray) ((JSONObject) ((JSONObject) userUniqTime.get("calendars"))
                        .get("krithikai@gmail.com")).get("busy");
                for (int j = 0; j < busyTimes.length(); j++) {
                    totalBusyTimes.add(busyTimes.getJSONObject(j));
                }
            }
        }
        for (JSONObject userBusy : totalBusyTimes) {
                for (int m = 0; m < calendar.size(); m++) {
                    ArrayList<JSONObject> finalUpdatedCal = calendar.get(m);
                    int startIndex = 0;
                    int endIndex = 0;
                    String start = userBusy.getString("start");
                    String end = userBusy.getString("end");
                    for (int i = 0; i < finalUpdatedCal.size(); i++) {
                        if (start.contentEquals(finalUpdatedCal.get(i).getString("start"))) {
                            startIndex = i;
                        } else if (end.contentEquals(finalUpdatedCal.get(i).getString("start"))) {
                            endIndex = i;
                            for (int j = startIndex; j < endIndex; j++) {
                                finalUpdatedCal.remove(startIndex);
                            }
                        }
                    }
                    calendar.remove(m);
                    calendar.add(m, finalUpdatedCal);
                }
            }
        return calendar;
    }

    // updating the adapter to populate the current times
    public void updateAvailTimes(ArrayList<ArrayList<JSONObject>> allTimes, int index) throws JSONException {
        ArrayList<JSONObject> times = allTimes.get(index);
        for (JSONObject time : times) {
            String start = time.getString("start");
            String end = time.getString("end");
            TimeOption newTime = new TimeOption(start, end);
            mTimes.add(newTime);
        }
        mTimeAdapter.notifyDataSetChanged();
    }
}
