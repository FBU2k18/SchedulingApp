package com.emmabr.schedulingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.emmabr.schedulingapp.R;

public class ViewPollActivity extends AppCompatActivity {

    String messageID;
    String groupID;

    ArrayList<ArrayList<String>> mOptions;
    PollAdapter mPollAdapter;

    TextView tvTitlePoll;
    RecyclerView rvOptions;

    FirebaseDatabase mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_poll);

        getSupportActionBar().hide();

        DisplayMetrics displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int)(width * .70), (int)(height * .55));


        messageID = getIntent().getStringExtra("messageID");
        groupID = getIntent().getStringExtra("groupID");

        mOptions = new ArrayList<>();
        mPollAdapter = new PollAdapter(mOptions, groupID, messageID);

        tvTitlePoll = findViewById(R.id.tvTitlePoll);
        rvOptions = findViewById(R.id.rvOptions);
        rvOptions.setLayoutManager(new LinearLayoutManager(this));
        rvOptions.setAdapter(mPollAdapter);


        //TODO: SET POLL TITLE
        //mDatabaseRef.getReference().child("groups").child("chatMessages").child("pollTitle").setValue(tvTitlePoll.getText().toString());

        //tvTitlePoll.setText();

        getOptions();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void getOptions() {
        FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("chatMessages").child(messageID).child("options").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //set to only change what has changed
                if (mOptions.size() == 0) {
                    for (DataSnapshot childData : dataSnapshot.getChildren()) {
                        ArrayList<String> temp = new ArrayList<>();
                        for (DataSnapshot babyData : childData.getChildren())
                            if (babyData.getKey().toString().equals("text"))
                                temp.add(0, babyData.getValue().toString());
                            else
                                temp.add(babyData.getValue().toString());
                        mOptions.add(temp);
                        mPollAdapter.notifyItemInserted(mOptions.size() - 1);
                    }
                    sort();
                } else {
                    for (DataSnapshot childData : dataSnapshot.getChildren()) {
                        ArrayList<String> temp = new ArrayList<>();
                        for (DataSnapshot babyData : childData.getChildren())
                            if (babyData.getKey().toString().equals("text"))
                                temp.add(0, babyData.getValue().toString());
                            else
                                temp.add(babyData.getValue().toString());
                        if (!mOptions.contains(temp)) {
                            ArrayList<String> titles = new ArrayList<>();
                            for (ArrayList<String> list : mOptions)
                                titles.add(list.get(0));
                            mOptions.remove(titles.indexOf(temp.get(0)));
                            mPollAdapter.notifyItemRemoved(titles.indexOf(temp.get(0)));
                            int i = 0;
                            while (i < mOptions.size() && mOptions.get(i).size() > temp.size())
                                i++;
                            mOptions.add(i, temp);
                            mPollAdapter.notifyItemInserted(i);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sort() {
        for (int i = 0; i < mOptions.size() - 1; i++) {
            int max = mOptions.get(i).size();
            int maxPos = i;
            for (int j = i + 1; j < mOptions.size(); j++) {
                if (mOptions.get(j).size() > max) {
                    max = mOptions.get(j).size();
                    maxPos = j;
                }
            }
            ArrayList<String> temp = mOptions.get(maxPos);
            mOptions.remove(maxPos);
            mPollAdapter.notifyItemRemoved(maxPos);
            mOptions.add(i, temp);
            mPollAdapter.notifyItemInserted(i);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, GroupActivity.class);
                intent.putExtra("groupID", groupID);
                intent.putExtra("up", true);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
}
