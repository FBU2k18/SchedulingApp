package com.emmabr.schedulingapp.Models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class User {


    // data in schema
    public FirebaseUser user;
    public String personName;
    public String calendar;
    public ArrayList<Groups> group;

    public User(FirebaseUser user, String personName, String calendar, ArrayList<Groups> group) {
        this.user = user;
        this.personName = personName;
        this.calendar = calendar;
        this.group = group;
    }

    public static void addUser(User inputUser) {
        FirebaseDatabase.getInstance().getReference("users").setValue(inputUser);
    }

    public void addGroup(Groups groupInput) {
        group.add(0, groupInput);
    }
}
