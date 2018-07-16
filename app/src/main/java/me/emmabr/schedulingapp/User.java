package me.emmabr.schedulingapp;

import java.util.ArrayList;

public class User {

    String id;
    String name;
    String email;
    String photoPath; //not sure that we're gonna use this
    ArrayList<Group> groups; //or this

    public User(String id) {
        this.id = id;
        //pull name, email, photoPath, and groups from Firebase
    }

}