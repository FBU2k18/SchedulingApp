package com.emmabr.schedulingapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.emmabr.schedulingapp.R;

public class ViewPollActivity extends AppCompatActivity {

    String messageID;
    String groupID;

    TextView tvTitlePoll;
    TextView tvOneOption;
    TextView tvTwoOption;
    TextView tvThreeOption;
    TextView tvFourOption;

    ArrayList<String> votesForOne;
    ArrayList<String> votesForTwo;
    ArrayList<String> votesForThree;
    ArrayList<String> votesForFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_poll);

        messageID = getIntent().getStringExtra("messageID");
        groupID = getIntent().getStringExtra("groupID");

        tvTitlePoll = findViewById(R.id.tvTitlePoll);
        tvOneOption = findViewById(R.id.tvOneOption);
        tvTwoOption = findViewById(R.id.tvTwoOption);
        tvThreeOption = findViewById(R.id.tvThreeOption);
        tvFourOption = findViewById(R.id.tvFourOption);

        votesForOne = new ArrayList<>();
        votesForTwo = new ArrayList<>();
        votesForThree = new ArrayList<>();
        votesForFour = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupID).child("chatMessages").child(messageID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                String text1 = "";
                String text2 = "";
                String text3 = "";
                String text4 = "";

                for (final DataSnapshot childData : dataSnapshot.getChildren()) {
                    if (childData.getKey().toString().equals("pollTitle"))
                        tvTitlePoll.setText(childData.getValue().toString());
                    else if (childData.getKey().toString().equals("optionOne")) {
                        if (!childData.hasChildren())
                            childData.child("text").getRef().setValue(childData.getValue().toString());
                        votesForOne.clear();
                        for (DataSnapshot babyData : childData.getChildren())
                            if (babyData.getKey().toString().equals("text"))
                            text1 = babyData.getValue().toString();
                            else
                                votesForOne.add(babyData.getKey().toString());
                        tvOneOption.setText(text1 + ": " + votesForOne.size());
                        if (votesForOne.contains(FirebaseAuth.getInstance().getUid()))
                            tvOneOption.setTextColor(Color.GREEN);
                        else
                            tvOneOption.setTextColor(Color.BLACK);
                        tvOneOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (votesForOne.contains(FirebaseAuth.getInstance().getUid()))
                                    childData.child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();
                                else {
                                    if (votesForTwo.contains(FirebaseAuth.getInstance().getUid()))
                                        dataSnapshot.child("optionTwo").child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();
                                    else if (votesForThree.contains(FirebaseAuth.getInstance().getUid()))
                                        dataSnapshot.child("optionThree").child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();
                                    else if (votesForFour.contains(FirebaseAuth.getInstance().getUid()))
                                        dataSnapshot.child("optionFour").child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();

                                    childData.child(FirebaseAuth.getInstance().getUid()).getRef().setValue(FirebaseAuth.getInstance().getUid());
                                }
                            }
                        });
                    } else if (childData.getKey().toString().equals("optionTwo")) {
                        if (!childData.hasChildren())
                            childData.child("text").getRef().setValue(childData.getValue().toString());
                        votesForTwo.clear();
                        for (DataSnapshot babyData : childData.getChildren())
                            if (babyData.getKey().toString().equals("text"))
                            text2 = babyData.getValue().toString();
                            else
                                votesForTwo.add(babyData.getKey().toString());
                        tvTwoOption.setText(text2 + ": " + votesForTwo.size());
                        if (votesForTwo.contains(FirebaseAuth.getInstance().getUid()))
                            tvTwoOption.setTextColor(Color.GREEN);
                        else
                            tvTwoOption.setTextColor(Color.BLACK);
                        tvTwoOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (votesForTwo.contains(FirebaseAuth.getInstance().getUid()))
                                    childData.child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();
                                else {
                                    if (votesForOne.contains(FirebaseAuth.getInstance().getUid()))
                                        dataSnapshot.child("optionOne").child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();
                                    else if (votesForThree.contains(FirebaseAuth.getInstance().getUid()))
                                        dataSnapshot.child("optionThree").child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();
                                    else if (votesForFour.contains(FirebaseAuth.getInstance().getUid()))
                                        dataSnapshot.child("optionFour").child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();

                                    childData.child(FirebaseAuth.getInstance().getUid()).getRef().setValue(FirebaseAuth.getInstance().getUid());
                                }
                            }
                        });
                    } else if (childData.getKey().toString().equals("optionThree")) {
                        if (!childData.hasChildren())
                            childData.child("text").getRef().setValue(childData.getValue().toString());
                        votesForThree.clear();
                        for (DataSnapshot babyData : childData.getChildren())
                            if (babyData.getKey().toString().equals("text"))
                            text3 = babyData.getValue().toString();
                            else
                                votesForThree.add(babyData.getKey().toString());
                        tvThreeOption.setText(text3 + ": " + votesForThree.size());
                        if (votesForThree.contains(FirebaseAuth.getInstance().getUid()))
                            tvThreeOption.setTextColor(getResources().getColor(R.color.colorAccent));
                        else
                            tvThreeOption.setTextColor(Color.BLACK);
                        tvThreeOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (votesForThree.contains(FirebaseAuth.getInstance().getUid()))
                                    childData.child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();
                                else {
                                    if (votesForTwo.contains(FirebaseAuth.getInstance().getUid()))
                                        dataSnapshot.child("optionTwo").child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();
                                    else if (votesForOne.contains(FirebaseAuth.getInstance().getUid()))
                                        dataSnapshot.child("optionOne").child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();
                                    else if (votesForFour.contains(FirebaseAuth.getInstance().getUid()))
                                        dataSnapshot.child("optionFour").child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();

                                    childData.child(FirebaseAuth.getInstance().getUid()).getRef().setValue(FirebaseAuth.getInstance().getUid());
                                }
                            }
                        });
                    } else if (childData.getKey().toString().equals("optionFour")) {
                        if (!childData.hasChildren())
                            childData.child("text").getRef().setValue(childData.getValue().toString());
                        votesForFour.clear();
                        for (DataSnapshot babyData : childData.getChildren())
                            if (babyData.getKey().toString().equals("text"))
                            text4 = babyData.getValue().toString();
                            else
                                votesForFour.add(babyData.getKey().toString());
                        tvFourOption.setText(text4 + ": " + votesForFour.size());
                        if (votesForFour.contains(FirebaseAuth.getInstance().getUid()))
                            tvFourOption.setTextColor(Color.GREEN);
                        else
                            tvFourOption.setTextColor(Color.BLACK);
                        tvFourOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (votesForFour.contains(FirebaseAuth.getInstance().getUid()))
                                    childData.child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();
                                else {
                                    if (votesForTwo.contains(FirebaseAuth.getInstance().getUid()))
                                        dataSnapshot.child("optionTwo").child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();
                                    else if (votesForThree.contains(FirebaseAuth.getInstance().getUid()))
                                        dataSnapshot.child("optionThree").child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();
                                    else if (votesForOne.contains(FirebaseAuth.getInstance().getUid()))
                                        dataSnapshot.child("optionOne").child(FirebaseAuth.getInstance().getUid()).getRef().removeValue();

                                    childData.child(FirebaseAuth.getInstance().getUid()).getRef().setValue(FirebaseAuth.getInstance().getUid());
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, GroupActivity.class);
                intent.putExtra("groupID", groupID);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
}
