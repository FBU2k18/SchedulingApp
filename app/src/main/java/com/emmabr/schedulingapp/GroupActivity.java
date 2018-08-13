package com.emmabr.schedulingapp;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
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
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;

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
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.emmabr.schedulingapp.Models.Message.saveMessage;

public class GroupActivity extends AppCompatActivity implements LeaveGroupDialogFragment.LeaveGroupDialogFragmentListener {

    private String mGroupID;

    private ArrayList<String> mDays;
    private DayAdapter mDayAdapter;
    private RecyclerView mRVDays;

    private ArrayList<Message> mMessages;
    private MessageAdapter mMessageAdapter;
    private RecyclerView mRVMessageDisplay;
    private PagerSnapHelper mHelper;

    private TextView mTVGroupName;
    private EditText mETMessage;
    private FrameLayout mFLPeeker;
    private BottomSheetBehavior mPeekerBehavior;
    private ImageView mIVAddOther;
    private boolean mAddOtherIsPlus;
    private TextView mTVTitle;
    private Button mBSend;

    // getting users ids for email
    ArrayList<String> userBusyTimes;
    ArrayList<String> userEmails;

    // populating available times
    private FrameLayout mFLOtherTypes;
    private BottomSheetBehavior mOtherTypesBehavior;
    private Button mBAddPoll;
    private Button mBAddPic;

    private boolean mRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mGroupID = getIntent().getStringExtra("mGroupID");
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("userGroup").child(mGroupID).child("unreadMessages").removeValue();

        mDays = new ArrayList<>();
        mDayAdapter = new DayAdapter(mDays, this, mGroupID);
        mRVDays = findViewById(R.id.rvDays);
        mRVDays.setAdapter(mDayAdapter);
        mRVDays.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mHelper = new PagerSnapHelper();
        mHelper.attachToRecyclerView(mRVDays);

        mMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(mMessages, mGroupID);
        mRVMessageDisplay = findViewById(R.id.rvMessageDisplay);
        mRVMessageDisplay.setAdapter(mMessageAdapter);
        mRVMessageDisplay.setLayoutManager(new LinearLayoutManager(this));


        mTVGroupName = findViewById(R.id.tvGroupName);
        FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("groupName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                    mTVGroupName.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mTVGroupName.setSelected(true);
        mETMessage = findViewById(R.id.etMessage);
        mFLPeeker = findViewById(R.id.flPeeker);
        mPeekerBehavior = BottomSheetBehavior.from(mFLPeeker);
        mIVAddOther = findViewById(R.id.ivAddOther);
        mIVAddOther.setImageDrawable(getDrawable(android.R.drawable.ic_input_add));
        mAddOtherIsPlus = true;
        mIVAddOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAddOtherIsPlus) {
                    mIVAddOther.setImageDrawable(getDrawable(android.R.drawable.ic_delete));
                    mOtherTypesBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    mIVAddOther.setImageDrawable(getDrawable(android.R.drawable.ic_input_add));
                    mOtherTypesBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                mAddOtherIsPlus = !mAddOtherIsPlus;
            }
        });
        mTVTitle = findViewById(R.id.tvTitle);
        mTVTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPeekerBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    mPeekerBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else if (mPeekerBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    mPeekerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        mBSend = findViewById(R.id.bSend);
        mBSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mETMessage.getText().toString().equals("")) {
                    FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("nickName").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Date date = new Date();
                            Message message = new Message(FirebaseAuth.getInstance().getUid(), dataSnapshot.getValue().toString(), mETMessage.getText().toString(), null, null, Long.toString(date.getTime()));
                            saveMessage(message, mGroupID);
                            mETMessage.setText("");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });

        mFLOtherTypes = findViewById(R.id.flOtherTypes);
        mOtherTypesBehavior = BottomSheetBehavior.from(mFLOtherTypes);
        mOtherTypesBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mOtherTypesBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (mOtherTypesBehavior.getState() == BottomSheetBehavior.STATE_DRAGGING)
                    mOtherTypesBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });
        mBAddPoll = findViewById(R.id.bAddPoll);
        mBAddPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, PostPollActivity.class);
                intent.putExtra("mGroupID", mGroupID);
                mAddOtherIsPlus = true;
                mIVAddOther.setImageDrawable(getDrawable(android.R.drawable.ic_input_add));
                mOtherTypesBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                startActivity(intent);
            }
        });

        mBAddPic = findViewById(R.id.bAddPic);
        mBAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, PostPhotoActivity.class);
                intent.putExtra("mGroupID", mGroupID);
                mAddOtherIsPlus = true;
                mIVAddOther.setImageDrawable(getDrawable(android.R.drawable.ic_input_add));
                mOtherTypesBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                startActivity(intent);
            }
        });

        // get busy times from Firebase
        userBusyTimes = new ArrayList<>();
        userEmails = new ArrayList<>();
        final ArrayList<String> usersIDs = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("seenStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().equals("false"))
                    mRefresh = true;
                else
                    mRefresh = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("Recipients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                                        for (DataSnapshot childUser : dataSnapshot.getChildren()) {
                                            if (user.contentEquals(childUser.getKey())) {
                                                String email = (String) childUser.child("email").getValue();
                                                userEmails.add(email);
                                                String userCalendar = (String) childUser.child("calendar").getValue();
                                                userBusyTimes.add(userCalendar);
                                            }
                                        }
                                    }
                                    ArrayList<ArrayList<JSONObject>> updatedTimes = deleteBusyTimes(totalFreeTimes, userBusyTimes, userEmails);
                                    updateAvailTimes(updatedTimes);
                                    //getDays();
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        getMessages();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

    }


    public void getMessages() {
        FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("chatMessages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessages.clear();
                mMessageAdapter.notifyDataSetChanged();
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
                    mMessageAdapter.notifyItemInserted(mMessages.size() - 1);
                    mRVMessageDisplay.scrollToPosition(mMessages.size() - 1);
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
                finish();
                break;
            case R.id.miAddMember:
                Intent intentAdd = new Intent(this, AddMemberActivity.class);
                intentAdd.putExtra("mGroupID", mGroupID);
                startActivity(intentAdd);
                break;
            case R.id.miEditGroup:
                Intent intentEdit = new Intent(this, GroupProfile.class);
                intentEdit.putExtra("mGroupID", mGroupID);
                startActivity(intentEdit);
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
        FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("Recipients").child(FirebaseAuth.getInstance().getUid()).removeValue();
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("userGroup").child(mGroupID).removeValue();
        FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("Recipients"))
                    dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        finish();
    }

    public void scrollToPosition(int position) {
        mRVDays.scrollToPosition(position);
    }


    public ArrayList<ArrayList<JSONObject>> createCalendar() throws JSONException {
        ArrayList<ArrayList<JSONObject>> masterCalendar = new ArrayList<>();
        for (int k = 14; k < 22; k++) {
            ArrayList<JSONObject> calendar = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                JSONObject singleHour = new JSONObject();
                String time = null;
                String nextTime = null;
                String dateTime = null;
                String startTime = null;
                String endTime = null;
                String currDate = "2018-04-" + Integer.toString(k) + "T";
                String currNext = "2018-04-" + Integer.toString(k + 1) + "T";
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
            String userEmail = userIds.get(i);
            busyTimes = (JSONArray) ((JSONObject) ((JSONObject) userUniqTime.get("calendars"))
                    .get(userEmail)).get("busy");
            for (int j = 0; j < busyTimes.length(); j++) {
                totalBusyTimes.add(busyTimes.getJSONObject(j));
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
    public void updateAvailTimes(ArrayList<ArrayList<JSONObject>> allTimes) throws JSONException {
        for (int index = 0; index < 8; index++) {
            String date = null;
            ArrayList<JSONObject> times = allTimes.get(index);
            for (JSONObject time : times) {
                String start = time.getString("start");
                String end = time.getString("end");
                int startT = start.indexOf("T");
                int endT = end.indexOf("T");
                int startPeriod = start.indexOf(".");
                int endPeriod = end.indexOf(".");
                date = start.substring(0, startT);
                String startTime = start.substring(startT + 1, startPeriod);
                String endTime = end.substring(endT + 1, endPeriod);
                TimeOption newTime = new TimeOption(startTime, endTime);
                if (mRefresh)
                    FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("timeOptions")
                        .child(Integer.toString(index)).child(date).child(newTime.getStartTime()).setValue(newTime);
            }
            mDays.add(date);
        }
        mDayAdapter.notifyDataSetChanged();
        FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("seenStatus").setValue("true");
    }
}
