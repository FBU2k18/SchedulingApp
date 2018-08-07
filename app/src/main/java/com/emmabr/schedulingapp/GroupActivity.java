package com.emmabr.schedulingapp;

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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import me.emmabr.schedulingapp.R;

import static com.emmabr.schedulingapp.Models.Message.saveMessage;

public class GroupActivity extends AppCompatActivity implements LeaveGroupDialogFragment.LeaveGroupDialogFragmentListener {

    private String mGroupID;

    private ArrayList<TimeOption> mTimes;
    private TimeOptionAdapter mTimeAdapter;
    private RecyclerView mRVTimes;

    private ArrayList<Message> mMessages;
    private MessageAdapter mMessageAdapter;
    private RecyclerView mRVMessageDisplay;

    private TextView mTVGroupName;
    private EditText mETMessage;
    private FrameLayout mFLPeeker;
    private BottomSheetBehavior mPeekerBehavior;
    private ImageView mIVAddOther;
    private boolean mAddOtherIsPlus;
    private TextView mTVTitle;
    private Button mBSend;

    private FrameLayout mFLOtherTypes;
    private BottomSheetBehavior mOtherTypesBehavior;
    private Button mBAddPoll;
    private Button mBAddPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mGroupID = getIntent().getStringExtra("mGroupID");
        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("userGroup").child(mGroupID).child("unreadMessages").removeValue();

        mTimes = new ArrayList<>();
        mTimeAdapter = new TimeOptionAdapter(mTimes, this);
        mRVTimes = findViewById(R.id.rvTimes);
        mRVTimes.setAdapter(mTimeAdapter);
        mRVTimes.setLayoutManager(new LinearLayoutManager(this));
        mRVTimes.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(mMessages, mGroupID);
        mRVMessageDisplay = findViewById(R.id.rvMessageDisplay);
        mRVMessageDisplay.setAdapter(mMessageAdapter);
        mRVMessageDisplay.setLayoutManager(new LinearLayoutManager(this));

        mTVGroupName = findViewById(R.id.tvGroupName);
        FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("groupName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTVGroupName.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

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
            case R.id.miRefresh:
                //replace with intent
                Log.i("Menu", "Refresh");
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
        finish();
    }

    public void scrollToPosition(int position) {
        mRVTimes.scrollToPosition(position);
    }

    //for use in calendar pulling
    private ArrayList<String> mCalendars;
    public void test() {
        mCalendars = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("Recipients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot member : dataSnapshot.getChildren())
                    member.getRef().setPriority(null).continueWithTask(new Continuation<Void, Task<DataSnapshot>>() {
                        @Override
                        public Task<DataSnapshot> then(@NonNull Task<Void> task) throws Exception {
                            FirebaseDatabase.getInstance().getReference().child("users").child(member.getKey().toString()).child("calendar").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    mCalendars.add(dataSnapshot.getValue().toString());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            return null;
                        }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
