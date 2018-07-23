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
    public String imgURL;
    //private String userId;

    // create a new User
    public User(FirebaseUser user, String nickName, String imgURL) {
        email = user.getEmail();
        this.calendar = "";
        this.nickName = nickName;
        this.imgURL = imgURL;
        //this.userId = "";
    }

    //public void setUserId(String userId) {
       // this.userId = userId;
    //}

    //public String getUserId() {
        //return userId;
   // }

}