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
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.emmabr.schedulingapp.R;

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

        // get calendar data
        calendarUserIds = new ArrayList<>();
        userCalendars = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("Recipients")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childData : dataSnapshot.getChildren()) {
                            String tempHolder = childData.getKey();
                            String userCalendar = FirebaseDatabase.getInstance().getReference().child("users").child(tempHolder).child("calendar").getKey();
                            userCalendars.add(userCalendar);
                            String email = FirebaseDatabase.getInstance().getReference().child("users").child(tempHolder).child("email").getKey();
                            calendarUserIds.add(email);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("GroupActivity", "Something went wrong");
                    }
                });

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
            String freetime = findFreeTime(calendarUserIds, service);
        } catch (Exception e) {
            e.printStackTrace();
        }


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

    public String findFreeTime(ArrayList<String> groupUsersID, Calendar service) throws Exception {
        ArrayList<FreeBusyRequestItem> totalCalendars = new ArrayList<>();
        for (String uniqueID : groupUsersID) {
            totalCalendars.add(new FreeBusyRequestItem().setId(uniqueID));
        }
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

        FreeBusyResponse fbresponse = service.freebusy().query(req).execute();
        return fbresponse.toString();
    }

    public com.google.api.services.calendar.model.Calendar createMasterCal(ArrayList<String> allCalendars, Calendar service) throws IOException, JSONException {
        com.google.api.services.calendar.model.Calendar masterCalendar = new com.google.api.services.calendar.model.Calendar();
        masterCalendar.setSummary("Master Calendar");

        for (String uniqCal : allCalendars) {
            JSONObject tempCalHolder = new JSONObject(uniqCal);
            com.google.api.services.calendar.model.Calendar calFromJSON = new com.google.api.services.calendar.model.Calendar();
            calFromJSON.set("Test Calendar", tempCalHolder);
//            calFromJSON.putAll(Json.getDefaultInstance().);
//            masterCalendar = service.calendars().insert(tempCalHolder).execute();
        }
            return masterCalendar;
    }

//    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
//        // Load client secrets.
//        InputStream in = CalendarQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
//
//        // Build flow and trigger user authorization request.
//        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
//                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
//                .setAccessType("offline")
//                .build();
//        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
//    }

}
