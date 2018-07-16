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

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    //if we implement changing name from Gmail name
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    //if we implement changing photo from Gmail photo
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public void removeGroup(Group group) {
        groups.remove(group);
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return getId();
    }
}