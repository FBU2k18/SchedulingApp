package com.emmabr.schedulingapp.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GroupData {

    // data stored in schema
    public String groupId;
    public String groupName;
    public String imageURL;

    //public ArrayList<Messages> messagesList;
    //public ArrayList<AvailableTimes> availableTimes;

    // create new group
    public GroupData(String groupName, String imageURL) {
        //groupId = "";
        this.groupName = groupName;
        this.imageURL = imageURL;
    }

    // set groupID
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    // get groupID
    public String getGroupId() {
        return groupId;
    }

    // save group to Firebase
    public static void saveGroup(GroupData inputGroup) {
        DatabaseReference tempHolder = FirebaseDatabase.getInstance().getReference("groups").push();
        tempHolder.setValue(inputGroup);
        String groupTempID = tempHolder.getKey();
        inputGroup.setGroupId(groupTempID);
    }

    // getters
    public String getGroupName() {
        return groupName;
    }

    public String getImageURL() {
        return imageURL;
    }

//    // subclass for keeping track of messages
//    public static class Messages {
//        User sentUser;
//        String messageSent;
//        String sentAt;
//
//        // creating a Messages object
//        public Messages(User sentUser, String messageSent, String sentAt) {
//            this.sentUser = sentUser;
//            this.messageSent = messageSent;
//            this.sentAt = sentAt;
//        }
//
//        public void setMessages(Messages message) {
//            FirebaseDatabase.getInstance().getReference("groups").child("messages").setValue(message);
//        }
//
//
//    }
//
//    // subclass for available times
//    public static class AvailableTimes {
//        int ranking;
//        String time;
//        int upVotes;
//        int downVotes;
//
//        public AvailableTimes (int ranking, String time, int upVotes, int downVotes) {
//            this.ranking = ranking;
//            this.time = time;
//            this.upVotes = upVotes;
//            this.downVotes = downVotes;
//        }
//
//        public void setAvailableTime(AvailableTimes availableTime) {
//            FirebaseDatabase.getInstance().getReference("groups").child("available_times").setValue(availableTime);
//        }
//
//    }
}
