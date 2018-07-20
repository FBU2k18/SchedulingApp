package com.emmabr.schedulingapp.Models;

import com.google.firebase.database.FirebaseDatabase;

public class AvailableTime {

    int ranking;
    String timeGiven;
    int upVoteCount;
    int downVoteCount;

    public AvailableTime(int ranking, String timeGiven) {
        this.ranking = ranking;
        this.timeGiven = timeGiven;
        upVoteCount = 0;
        downVoteCount = 0;
    }

    public void saveAvailableTime(AvailableTime availableTime, String group_id) {
        FirebaseDatabase.getInstance().getReference().child("GroupData").child(group_id).child("AvailableTime").push().setValue(availableTime);
    }


}
