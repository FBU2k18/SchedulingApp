package com.emmabr.schedulingapp.model;

import java.util.ArrayList;

public class Group {

    String id;
    String name;
    String photoPath;
    ArrayList<User> members;

    //use if group already exists
    public Group(String id) {
        this.id = id;
        //pull name, photoPath, and members from Firebase
    }

    //use to create new group without picture
    public Group(String name, ArrayList<User> members) {
        this.name = name;
        this.members = members;
    }

    //use to create new group with picture
    public Group(String name, String photoPath, ArrayList<User> members) {
        this.name = name;
        this.photoPath = photoPath;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<User> members) {
        this.members = members;
    }

    public void addMember(User user) {
        members.add(user);
    }

    public void removeMember(User user) {
        members.remove(user);
    }

    public void create() {
        //create new group in Firebase
    }

    public void update() {
        //update group in Firebase
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return getId();
    }

    //delete this method later, only for testing
    public static Group randomGroup() {
        return new Group("GroupData " + (int) (Math.random() * 10), new ArrayList<User>());
    }
}
