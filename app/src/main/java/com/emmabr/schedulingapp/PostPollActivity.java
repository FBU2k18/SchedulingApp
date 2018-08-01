package com.emmabr.schedulingapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emmabr.schedulingapp.Models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import me.emmabr.schedulingapp.R;

import static com.emmabr.schedulingapp.Models.Message.saveMessage;

public class PostPollActivity extends AppCompatActivity {

    private String mGroupID;

    private EditText mETPollTitle;
    private EditText mETAddOption;
    private EditText mETNumber;

    private ArrayList<String> mOptionsText;

    private TextView mTVPollOptions;

    private ImageView mIVPlus;
    private ImageView mIVMultiply;

    private Button mBPostPoll;
    private Button mBCancelPoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_poll);

        mGroupID = getIntent().getStringExtra("mGroupID");

        mETPollTitle = findViewById(R.id.etPollTitle);
        mETAddOption = findViewById(R.id.etAddOption);
        mETNumber = findViewById(R.id.etNumber);

        mOptionsText = new ArrayList<>();

        mTVPollOptions = findViewById(R.id.tvPollOptions);
        mTVPollOptions.setText(mOptionsText.toString());

        mIVPlus = findViewById(R.id.ivPlus);
        mIVPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mETAddOption.getText().toString().isEmpty())
                    Toast.makeText(PostPollActivity.this, "Option is empty!", Toast.LENGTH_LONG).show();
                else {
                    mOptionsText.add(mETAddOption.getText().toString());
                    mTVPollOptions.setText(mOptionsText.toString());
                    mETAddOption.setText("");
                }
            }
        });
        mIVMultiply = findViewById(R.id.ivMultiply);
        mIVMultiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mETNumber.getText().toString().isEmpty())
                    Toast.makeText(PostPollActivity.this, "No number entered!", Toast.LENGTH_LONG).show();
                else
                    try {
                        mOptionsText.remove(Integer.parseInt(mETNumber.getText().toString()) - 1);
                        mTVPollOptions.setText(mOptionsText.toString());
                        mETNumber.setText("");
                    } catch (IndexOutOfBoundsException badNumber) {
                        Toast.makeText(PostPollActivity.this, "There is no option at that position!", Toast.LENGTH_LONG).show();
                    } catch (NumberFormatException notANumber) {
                        Toast.makeText(PostPollActivity.this, "Only use numbers in this field!", Toast.LENGTH_LONG).show();
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
                        if (!mETPollTitle.getText().toString().isEmpty())
                            if (mOptionsText.size() > 1) {
                                Date date = new Date();
                                Message message = new Message(FirebaseAuth.getInstance().getUid(), dataSnapshot.getValue().toString(), null, null, "Poll: " + mETPollTitle.getText().toString(), Long.toString(date.getTime()));
                                saveMessage(message, mGroupID);
                                FirebaseDatabase.getInstance().getReference().child("groups").child(mGroupID).child("chatMessages").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot childData : dataSnapshot.getChildren())
                                            if (childData.hasChild("pollTitle") && !childData.hasChild("options"))
                                                for (String option : mOptionsText)
                                                    childData.child("options").child(option).child("text").getRef().setValue(option);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                finish();
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
}
