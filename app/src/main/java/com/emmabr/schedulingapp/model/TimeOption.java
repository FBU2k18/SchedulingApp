package com.emmabr.schedulingapp.model;

import android.support.annotation.NonNull;

import com.emmabr.schedulingapp.TimeOptionAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TimeOption implements Comparable {

    String id;
    String startTime;
    String endTime;
    int votes;
    ArrayList<String> upVoters; //possible way to keep track of who votes up
    ArrayList<String> downVoters; //possible way to keep track of who votes down
    DataSnapshot self;
    TimeOptionAdapter.ViewHolder holder;

    public TimeOption(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TimeOption(String startTime, String endTime, DataSnapshot self) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.self = self;
        upVoters = new ArrayList<>();
        downVoters = new ArrayList<>();
        findUpVotes();
        findDownVotes();
    }

    public String getId() {
        return id;
    }

    public void setHolder(TimeOptionAdapter.ViewHolder holder) {
        this.holder = holder;
    }

    public String getTime() {
//        int startT = startTime.indexOf("T");
//        int endT = endTime.indexOf("T");
//        int startPeriod = startTime.indexOf(".");
//        int endPeriod = endTime.indexOf(".");
//        startTime = startTime.substring(startT + 1, startPeriod);
//        endTime = endTime.substring(endT + 1, endPeriod);
        String finalTime = startTime + " - " + endTime;
        return finalTime;
    }

    public void findUpVotes() {
        self.child("upVoters").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> temp = new ArrayList<>();
                for (DataSnapshot upVoter : dataSnapshot.getChildren())
                    temp.add(upVoter.getValue().toString());
                upVoters = (ArrayList<String>) temp.clone();
                setVotes();
                if (holder != null)
                    holder.move(TimeOption.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void findDownVotes() {
        self.child("downVoters").getRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> temp = new ArrayList<>();
                for (DataSnapshot downVoter : dataSnapshot.getChildren())
                    temp.add(downVoter.getValue().toString());
                downVoters = (ArrayList<String>) temp.clone();
                setVotes();
                if (holder != null)
                    holder.move(TimeOption.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes() {
        votes = upVoters.size() - downVoters.size();
    }

    public String getStartTime() {
        return startTime;
    }

    public ArrayList<String> getUpVoters() {
        return upVoters;
    }

    public void upVote(String user) {
        if (!upVoters.contains(user)) {
            self.child("upVoters").child(user).getRef().setValue(user);
            if (downVoters.contains(user))
                self.child("downVoters").child(user).getRef().removeValue();
        } else
            self.child("upVoters").child(user).getRef().removeValue();
    }

    public ArrayList<String> getDownVoters() {
        return downVoters;
    }

    public void downVote(String user) {
        if (!downVoters.contains(user)) {
            self.child("downVoters").child(user).getRef().setValue(FirebaseAuth.getInstance().getUid());
            if (upVoters.contains(user))
                self.child("upVoters").child(user).getRef().removeValue();
        } else
            self.child("downVoters").child(user).getRef().removeValue();
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return Integer.parseInt(o.toString()) - Integer.parseInt(toString());
    }

    @Override
    public String toString() {
        return Integer.toString(getVotes());
    }

}
