package com.emmabr.schedulingapp.Models;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupData {

    // data stored in schema
    String groupId;
    String groupName;
    String imageURL;
    String seenStatus;


    // create new group
    public GroupData(String groupName, String imageURL, String groupId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.imageURL = imageURL;
        this.seenStatus = "false";
    }

    // creating group to put in user data schema
    public GroupData(String groupName, String imageURL) {
        this.groupName = groupName;
        this.imageURL = imageURL;
        this.seenStatus = "true";
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
    public static void saveGroup(GroupData inputGroup, ArrayList<String> usersInChat) {
        DatabaseReference tempHolder = FirebaseDatabase.getInstance().getReference("groups").push();
        tempHolder.setValue(inputGroup);
        String groupTempID = tempHolder.getKey();
        inputGroup.setGroupId(groupTempID);
        GroupData userGroupHolder = new GroupData(inputGroup.getGroupName(), inputGroup.getImageURL());
        for (String oneUser : usersInChat) {
            FirebaseDatabase.getInstance().getReference().child("users").child(oneUser).child("userGroup").child(groupTempID).setValue(userGroupHolder);
            FirebaseDatabase.getInstance().getReference().child("groups").child(groupTempID).child("Recipients").child(oneUser).setValue(oneUser);
        }
    }

    // getters
    public String getGroupName() {
        return groupName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getSeenStatus() {
        return seenStatus;
    }
}
