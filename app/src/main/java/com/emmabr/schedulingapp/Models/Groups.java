package com.emmabr.schedulingapp.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Groups {

    // data stored in schema
    public String groupName;
    public String imageURL;
    public ArrayList<User> users;
    public ArrayList<Messages> messagesList;
    public ArrayList<AvailableTimes> availableTimes;

    public Groups (String groupName, String imageURL, ArrayList<User> users,
                   ArrayList<Messages> messagesList, ArrayList<AvailableTimes> availableTimes) {
        this.groupName = groupName;
        this.imageURL = imageURL;
        this.users = users;
        this.messagesList = messagesList;
        this.availableTimes = availableTimes;
    }

    public void setGroup(Groups inputGroup) {
        FirebaseDatabase.getInstance().getReference("groups").setValue(inputGroup);
        for (int i = 0; i < users.size(); i++) {
            users.get(i).addGroup(inputGroup);
        }
    }


    // subclass for keeping track of messages
    public static class Messages {

        DatabaseReference messagesDatabase;
        User sentUser;
        String messageSent;
        String sentAt;

        // creating a Messages object
        public Messages(User sentUser, String messageSent, String sentAt) {
            this.sentUser = sentUser;
            this.messageSent = messageSent;
            this.sentAt = sentAt;
        }

        public void setMessages(Messages message) {
            FirebaseDatabase.getInstance().getReference("groups").child("messages").setValue(message);
        }


    }

    // subclass for available times
    public static class AvailableTimes {
        int ranking;
        String time;
        int upVotes;
        int downVotes;

        public AvailableTimes (int ranking, String time, int upVotes, int downVotes) {
            this.ranking = ranking;
            this.time = time;
            this.upVotes = upVotes;
            this.downVotes = downVotes;
        }

        public void setAvailableTime(AvailableTimes availableTime) {
            FirebaseDatabase.getInstance().getReference("groups").child("available_times").setValue(availableTime);
        }

    }
}
