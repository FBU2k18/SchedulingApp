package com.emmabr.schedulingapp.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;

public class TimeOption implements Comparable{

    String id;
    String time;
    int votes;
    ArrayList<User> upVoters; //possible way to keep track of who votes up
    ArrayList<User> downVoters; //possible way to keep track of who votes down

    public TimeOption(String id) {
        this.id = id;
        //pull time, votes, and voters from Firebase
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes() {
        votes = upVoters.size() - downVoters.size();
    }

    public ArrayList<User> getUpVoters() {
        return upVoters;
    }

    public void upVote(User user) {
        if (!upVoters.contains(user)) {
            upVoters.add(user);
            if (downVoters.contains(user))
                downVoters.remove(user);
        } else
            upVoters.remove(user);
        setVotes();
    }

    public ArrayList<User> getDownVoters() {
        return downVoters;
    }

    public void downVote(User user) {
        if (!downVoters.contains(user)) {
            downVoters.add(user);
            if (upVoters.contains(user))
                upVoters.remove(user);
        } else
            downVoters.remove(user);
        setVotes();
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return Integer.parseInt(o.toString()) - Integer.parseInt(toString());
    }

    @Override
    public String toString() {
        return Integer.toString(getVotes());
    }

    //just for testing, will be removed later
    public static TimeOption newTime() {
        TimeOption time = new TimeOption("");
        time.upVoters = new ArrayList<>();
        time.downVoters = new ArrayList<>();
        time.time = "TimeOption " + (int) (Math.random()*10);
        return time;
    }
}
