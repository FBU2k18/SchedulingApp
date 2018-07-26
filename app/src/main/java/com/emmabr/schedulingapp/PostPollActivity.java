package com.emmabr.schedulingapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.emmabr.schedulingapp.Models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import me.emmabr.schedulingapp.R;

import static com.emmabr.schedulingapp.Models.Message.saveMessage;

public class PostPollActivity extends AppCompatActivity {

    String groupID;

    EditText etPollTitle;
    EditText etOptionOne;
    EditText etOptionTwo;
    EditText etOptionThree;
    EditText etOptionFour;

    Button bPostPoll;
    Button bCancelPoll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_poll);

        groupID = getIntent().getStringExtra("groupID");

        etPollTitle = findViewById(R.id.etPollTitle);
        etOptionOne = findViewById(R.id.etOptionOne);
        etOptionTwo = findViewById(R.id.etOptionTwo);
        etOptionThree = findViewById(R.id.etOptionThree);
        etOptionFour = findViewById(R.id.etOptionFour);

        bPostPoll = findViewById(R.id.bPostPoll);
        bPostPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid()).child("nickName").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((!etPollTitle.getText().toString().equals("")) && (!etOptionOne.getText().toString().equals("")) && (!etOptionTwo.getText().toString().equals("")) && (!((!etOptionFour.getText().toString().equals("")) && etOptionThree.getText().toString().equals("")))) {
                            Date date = new Date();
                            Message message = new Message(FirebaseAuth.getInstance().getUid(), dataSnapshot.getValue().toString(), null, null, "Poll: " + etPollTitle.getText().toString(), etOptionOne.getText().toString(), etOptionTwo.getText().toString(), etOptionThree.getText().toString().equals("")?null:etOptionThree.getText().toString(), etOptionFour.getText().toString().equals("")?null:etOptionFour.getText().toString(), Long.toString(date.getTime()));
                            saveMessage(message, groupID);
                            Intent intent = new Intent(PostPollActivity.this, GroupActivity.class);
                            intent.putExtra("groupID", groupID);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
        bCancelPoll = findViewById(R.id.bCancelPoll);
        bCancelPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostPollActivity.this, GroupActivity.class);
                intent.putExtra("groupID", groupID);
                startActivity(intent);
                finish();
            }
        });
    }
}
