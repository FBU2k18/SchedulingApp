package com.emmabr.schedulingapp;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.emmabr.schedulingapp.model.Message;
import com.emmabr.schedulingapp.model.TimeOption;
import com.emmabr.schedulingapp.model.User;

import java.util.ArrayList;
import java.util.Collections;

import com.emmabr.schedulingapp.R;


public class GroupActivity extends AppCompatActivity {


    ArrayList<TimeOption> times;
    TimeOptionAdapter timeAdapter;
    RecyclerView rvTimes;
    ArrayList<Message> messages;
    MessageAdapter messsageAdapter;
    RecyclerView rvMessageDisplay;
    EditText etMessage;
    FrameLayout flPeeker;
    BottomSheetBehavior behavior;
    TextView tvTitle;
    Button bSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        times = new ArrayList<>();
        timeAdapter = new TimeOptionAdapter(times, this);
        rvTimes = findViewById(R.id.rvTimes);
        rvTimes.setAdapter(timeAdapter);
        rvTimes.setLayoutManager(new LinearLayoutManager(this));
        rvTimes.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        messages = new ArrayList<>();
        messsageAdapter = new MessageAdapter(messages);
        rvMessageDisplay = findViewById(R.id.rvMessageDisplay);
        rvMessageDisplay.setAdapter(messsageAdapter);
        rvMessageDisplay.setLayoutManager(new LinearLayoutManager(this));

        etMessage = findViewById(R.id.etMessage);
        flPeeker = findViewById(R.id.flPeeker);
        behavior = BottomSheetBehavior.from(flPeeker);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getTimes();

        getMessages();
    }

    public void getTimes() {
        //will pull times from Firebase, but testing for now
        for (int i = 0; i < 10; i++)
            times.add(TimeOption.newTime());
        Collections.sort(times);
        timeAdapter.notifyDataSetChanged();
    }

    public void getMessages() {
        //will pull messages from Firebase, but testing for now
        for (int i = 0; i < 3; i++) {
            messages.add(new Message(new User("123abc"), "Not Me"));
            messsageAdapter.notifyItemInserted(messages.size() - 1);
        }
        messages.add(new Message(new User("abc123"), "Me"));
        messsageAdapter.notifyItemInserted(messages.size() - 1);
        for (int i = 0; i < 3; i++) {
            messages.add(new Message(new User("123abc"), "Not Me"));
            messsageAdapter.notifyItemInserted(messages.size() - 1);
        }
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
