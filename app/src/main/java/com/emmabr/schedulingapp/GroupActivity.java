package com.emmabr.schedulingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmabr.schedulingapp.model.Message;
import com.emmabr.schedulingapp.model.TimeOption;
import com.emmabr.schedulingapp.model.User;

import java.util.ArrayList;
import java.util.Collections;

import me.emmabr.schedulingapp.R;


public class GroupActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mTimes = new ArrayList<>();
        mTimeAdapter = new TimeOptionAdapter(mTimes, this);
        rvTimes = findViewById(R.id.rvTimes);
        rvTimes.setAdapter(mTimeAdapter);
        rvTimes.setLayoutManager(new LinearLayoutManager(this));
        rvTimes.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mMessages = new ArrayList<>();
        mMesssageAdapter = new MessageAdapter(mMessages);
        rvMessageDisplay = findViewById(R.id.rvMessageDisplay);
        rvMessageDisplay.setAdapter(mMesssageAdapter);
        rvMessageDisplay.setLayoutManager(new LinearLayoutManager(this));

        etMessage = findViewById(R.id.etMessage);
        flPeeker = findViewById(R.id.flPeeker);
        peekerBehavior = BottomSheetBehavior.from(flPeeker);
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
                //send message from etMessage.getText().toString()
                Log.i("Message", "Message Sent");
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
            public void onSlide(@NonNull View view, float v) {}
        });
        bAddPoll = findViewById(R.id.bAddPoll);
        bAddPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        bTakePic = findViewById(R.id.bTakePic);
        bTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        bAddPic = findViewById(R.id.bAddPic);
        bAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        //will pull messages from Firebase, but testing for now
        for (int i = 0; i < 3; i++) {
            mMessages.add(new Message(new User("123abc"), "Not Me"));
            mMesssageAdapter.notifyItemInserted(mMessages.size() - 1);
        }
        mMessages.add(new Message(new User("abc123"), "Me"));
        mMesssageAdapter.notifyItemInserted(mMessages.size() - 1);
        //try picture message after doing message pushing
        for (int i = 0; i < 2; i++) {
            mMessages.add(new Message(new User("123abc"), "Not Me"));
            mMesssageAdapter.notifyItemInserted(mMessages.size() - 1);
        }

        //keep this line, scrolls to most recent message after messages are retrieved
        rvMessageDisplay.scrollToPosition(mMessages.size() - 1);
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
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.miAddMember:
                //replace with intent
                Log.i("Menu", "Add Member");
                break;
            case R.id.miRefresh:
                //replace with intent
                Log.i("Menu", "Refresh");
                break;
            case R.id.miLeaveGroup:
                //replace with log out and intent
                Log.i("Menu", "Leave Group");
                break;
        }
        return true;
    }

    public void scrollToPosition(int position) {
        rvTimes.scrollToPosition(position);
    }
}
