package com.emmabr.schedulingapp.Models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class User {

    // data in schema
    public String email;
    public String nickName;
    public String calendar;
    public String image;
    public String userId;
    static public ArrayList<String> users = new ArrayList<>();

    //neccessary no arg const
    public User() {
    }
    // create a new User
    public User(FirebaseUser user, String nickName, String image) {
        this.email = user.getEmail();
        this.calendar = "";
        this.nickName = nickName;
        this.image = image;

        this.userId = "";
    }

    //getters
    public String getName() {
        return nickName;
    }

    public String getCalendar() {
        return calendar;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    //setters
    public void setImage(String image) {
        this.image = image;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    // add user to database
    public static void saveUser(User inputUser) {
        inputUser.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(inputUser);
        // TODO - remove hardcoded group data
        GroupData hardCodedGroup = new GroupData("It works!!!", "","",users);
        ArrayList<String> userListTemp = new ArrayList<>();
        userListTemp.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
        GroupData.saveGroup(hardCodedGroup, userListTemp);
    }
}