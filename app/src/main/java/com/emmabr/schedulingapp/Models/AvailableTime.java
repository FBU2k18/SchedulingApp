package com.emmabr.schedulingapp.Models;

import com.google.firebase.database.FirebaseDatabase;

public class AvailableTime {

    String startTime;
    String endTime;

    public AvailableTime(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
