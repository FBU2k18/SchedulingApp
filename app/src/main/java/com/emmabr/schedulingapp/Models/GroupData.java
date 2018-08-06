package com.emmabr.schedulingapp.Models;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
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
    ArrayList<String> users = new ArrayList<>();

    // create new group
    public GroupData(String groupName, String imageURL, String groupId, ArrayList<String> users) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.imageURL = imageURL;
        this.seenStatus = "false";
        this.users = users;
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

    public ArrayList<String> getUsers() {
        return users;
    }

    public String getGroupMembers(String groupId) {
        String memberString = "";
        final ArrayList<String> members = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("groups").child(groupId).child("Recipients").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot member : dataSnapshot.getChildren()) {
                    member.getRef().setPriority(null).continueWithTask(new Continuation<Void, Task<DataSnapshot>>() {
                        @Override
                        public Task<DataSnapshot> then(@NonNull Task<Void> task) {
                            FirebaseDatabase.getInstance().getReference().child("users").child(member.getKey().toString()).child("nickName").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    members.add(dataSnapshot.getValue().toString());
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                            return null;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        for (int i = 0; i < members.size(); ++i) {
            memberString = memberString + members.get(i) + " ";
        }

        return memberString;
    }
}
