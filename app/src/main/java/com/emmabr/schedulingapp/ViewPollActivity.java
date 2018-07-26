package com.emmabr.schedulingapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
    TextView tvOneCount;
    ImageView ivOneVote;
    ImageView ivOneMedal;
    TextView tvTwoOption;
    TextView tvTwoCount;
    ImageView ivTwoVote;
    ImageView ivTwoMedal;
    TextView tvThreeOption;
    TextView tvThreeCount;
    ImageView ivThreeVote;
    ImageView ivThreeMedal;
    TextView tvFourOption;
    TextView tvFourCount;
    ImageView ivFourVote;
    ImageView ivFourMedal;

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
        tvOneCount = findViewById(R.id.tvOneCount);
        ivOneVote = findViewById(R.id.ivOneVote);
        ivOneMedal = findViewById(R.id.ivOneMedal);
        tvTwoOption = findViewById(R.id.tvTwoOption);
        tvTwoCount = findViewById(R.id.tvTwoCount);
        ivTwoVote = findViewById(R.id.ivTwoVote);
        ivTwoMedal = findViewById(R.id.ivTwoMedal);
        tvThreeOption = findViewById(R.id.tvThreeOption);
        tvThreeCount = findViewById(R.id.tvThreeCount);
        ivThreeVote = findViewById(R.id.ivThreeVote);
        ivThreeMedal = findViewById(R.id.ivThreeMedal);
        tvFourOption = findViewById(R.id.tvFourOption);
        tvFourCount = findViewById(R.id.tvFourCount);
        ivFourVote = findViewById(R.id.ivFourVote);
        ivFourMedal = findViewById(R.id.ivFourMedal);

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
                        tvOneOption.setText(text1);
                        tvOneCount.setText(Integer.toString(votesForOne.size()));
                        if (votesForOne.contains(FirebaseAuth.getInstance().getUid()))
                            ivOneVote.setImageDrawable(getResources().getDrawable(android.R.drawable.checkbox_on_background));
                        else
                            ivOneVote.setImageDrawable(getResources().getDrawable(android.R.drawable.checkbox_off_background));
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
                        tvTwoOption.setText(text2);
                        tvTwoCount.setText(Integer.toString(votesForTwo.size()));
                        if (votesForTwo.contains(FirebaseAuth.getInstance().getUid()))
                            ivTwoVote.setImageDrawable(getResources().getDrawable(android.R.drawable.checkbox_on_background));
                        else
                            ivTwoVote.setImageDrawable(getResources().getDrawable(android.R.drawable.checkbox_off_background));
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
                        tvThreeOption.setText(text3);
                        tvThreeOption.setBackground(getResources().getDrawable(R.drawable.box));
                        tvThreeCount.setText(Integer.toString(votesForThree.size()));
                        tvThreeCount.setBackground(getResources().getDrawable(R.drawable.box));
                        if (votesForThree.contains(FirebaseAuth.getInstance().getUid()))
                            ivThreeVote.setImageDrawable(getResources().getDrawable(android.R.drawable.checkbox_on_background));
                        else
                            ivThreeVote.setImageDrawable(getResources().getDrawable(android.R.drawable.checkbox_off_background));
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
                        tvFourOption.setText(text4);
                        tvFourOption.setBackground(getResources().getDrawable(R.drawable.box));
                        tvFourCount.setText(Integer.toString(votesForThree.size()));
                        tvFourCount.setBackground(getResources().getDrawable(R.drawable.box));
                        if (votesForFour.contains(FirebaseAuth.getInstance().getUid()))
                            ivFourVote.setImageDrawable(getResources().getDrawable(android.R.drawable.checkbox_on_background));
                        else
                            ivFourVote.setImageDrawable(getResources().getDrawable(android.R.drawable.checkbox_off_background));
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

                int max = 1;
                if (votesForOne.size() >= max)
                    max = votesForOne.size();
                if (votesForTwo.size() >= max)
                    max = votesForTwo.size();
                if (votesForThree.size() >= max)
                    max = votesForThree.size();
                if (votesForFour.size() >= max)
                    max = votesForFour.size();
                if (votesForOne.size() == max)
                    ivOneMedal.setImageDrawable(getResources().getDrawable(R.drawable.gold_medal));
                else
                    ivOneMedal.setImageDrawable(null);
                if (votesForTwo.size() == max)
                    ivTwoMedal.setImageDrawable(getResources().getDrawable(R.drawable.gold_medal));
                else
                    ivTwoMedal.setImageDrawable(null);
                if (votesForThree.size() == max)
                    ivThreeMedal.setImageDrawable(getResources().getDrawable(R.drawable.gold_medal));
                else
                    ivThreeMedal.setImageDrawable(null);
                if (votesForFour.size() == max)
                    ivFourMedal.setImageDrawable(getResources().getDrawable(R.drawable.gold_medal));
                else
                    ivFourMedal.setImageDrawable(null);
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
