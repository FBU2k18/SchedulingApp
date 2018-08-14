package com.emmabr.schedulingapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.emmabr.schedulingapp.Models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.emmabr.schedulingapp.Models.Message.saveMessage;

public class PostPollActivity extends AppCompatActivity {

    private String mGroupID;

    private EditText mETPollTitle;
    private EditText mETAddOption;
    private RecyclerView mRVPollOptions;
    private PostPollAdapter mAdapter;

    private Map<String, Integer> mOptionsText;

    private ImageView mIVPlus;

    private Button mBPostPoll;
    private Button mBCancelPoll;

    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_poll);

        mGroupID = getIntent().getStringExtra("mGroupID");

        getSupportActionBar().hide();

        DisplayMetrics displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .83), (int) (height * .56));

        mETPollTitle = findViewById(R.id.etPollTitle);
        mETAddOption = findViewById(R.id.etAddOption);

        mOptionsText = new HashMap<>();
        mAdapter = new PostPollAdapter(mOptionsText, mGroupID);

        mRVPollOptions = findViewById(R.id.rvPollOptions);
        mRVPollOptions.setLayoutManager(new LinearLayoutManager(this));
        mRVPollOptions.setAdapter(mAdapter);

        mIVPlus = findViewById(R.id.ivPlus);
        mIVPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mETAddOption.getText().toString().isEmpty())
                    Toast.makeText(PostPollActivity.this, "Option is empty!", Toast.LENGTH_LONG).show();
                else {

                    mOptionsText.put(mETAddOption.getText().toString(), mOptionsText.size());
                    mAdapter.notifyItemInserted(mOptionsText.size() - 1);
                    mETAddOption.setText("");
                }
            }
        });

        mBPostPoll = findViewById(R.id.bPostPoll);
        mBPostPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("nickName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //
                        if (!mETPollTitle.getText().toString().isEmpty())
                            if (mOptionsText.size() > 1) {
                                Date date = new Date();
                                Message message = new Message(FirebaseAuth.getInstance().getUid(), dataSnapshot.getValue().toString(), null, null, "Poll: " + mETPollTitle.getText().toString(), Long.toString(date.getTime()));
                                saveMessage(message, mGroupID);

                                valueEventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot childData : dataSnapshot.getChildren()) {
                                            if (childData.hasChild("pollTitle") && !childData.hasChild("options")) {
                                                for (Map.Entry entry : mOptionsText.entrySet()) {
                                                    childData.child("options").child(entry.getKey().toString()).child("text").getRef().setValue(entry.getKey().toString());
                                                }
                                            }
                                        }
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                };
                                FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("chatMessages").addValueEventListener(valueEventListener);
                            } else if (mOptionsText.size() == 0)
                                Toast.makeText(PostPollActivity.this, "No options!", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(PostPollActivity.this, "Only one option!", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(PostPollActivity.this, "A Title is needed!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        mBCancelPoll = findViewById(R.id.bCancelPoll);
        mBCancelPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (valueEventListener != null) {
            FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("chatMessages").removeEventListener(valueEventListener);
        }

    }
}
