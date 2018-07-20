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

    // create a new User
    public User(FirebaseUser user, String nickName) {
        email = user.getEmail();
        this.calendar = "";
        this.nickName = nickName;
    }

    // add user to database
    public static void saveUser(User inputUser) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(inputUser);
        // TODO - remove hardcoded group data
        GroupData hardCodedGroup = new GroupData("It works!!!", "","");
        ArrayList<String> userListTemp = new ArrayList<>();
        userListTemp.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
        GroupData.saveGroup(hardCodedGroup, userListTemp);
    }
}