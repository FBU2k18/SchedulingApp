package com.emmabr.schedulingapp.Models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class User {

    // data in schema
    private String email;
    private String nickName;
    private String calendar;
    private String userId;

    // create a new User
    public User(FirebaseUser user, String nickName) {
        email = user.getEmail();
        this.calendar = "";
        this.nickName = nickName;
        this.userId = "";
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    // add user to database
<<<<<<< HEAD
    public static void saveUser(User inputUser) {
=======
    public static void addUser(FirebaseUser userCurr, User inputUser) {
        inputUser.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
>>>>>>> 73f70a3267eba2ed9d88bf88ffd223ea190d1316
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(inputUser);
        // TODO - remove hardcoded group data
        GroupData hardCodedGroup = new GroupData("It works!!!", "","");
        ArrayList<String> userListTemp = new ArrayList<>();
        userListTemp.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
        GroupData.saveGroup(hardCodedGroup, userListTemp);
    }
}