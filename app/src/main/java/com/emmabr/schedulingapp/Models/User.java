package com.emmabr.schedulingapp.Models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class User {

    // data in schema
    //public String uid;
    public FirebaseUser user;
    public String personName;
    public String calendar;
    public ArrayList<Groups> group;

    public User(FirebaseUser user, String calendar, ArrayList<Groups> group) {
        //uid = FirebaseDatabase.getInstance().getReference("appUsers").push().getKey();
        this.user = user;
        this.calendar = calendar;
        this.group = group;
    }

    public User(FirebaseUser user, String personName, String calendar, ArrayList<Groups> group) {
        this.user = user;
        this.personName = personName;
        this.calendar = calendar;
        this.group = group;
        //uid = FirebaseDatabase.getInstance().getReference("appUsers").push().getKey();
    }

    public static void addUser(User inputUser) {
        FirebaseDatabase.getInstance().getReference("appUsers").push().setValue(inputUser);
    }

    public void addGroup(Groups groupInput) {
        group.add(0, groupInput);
    }
}
