package com.emmabr.schedulingapp.model;

public class Message {

    String id;
    User sender;
    String text;

    //use if message already exists
    public Message(String id) {
        this.id = id;
        //pull sender and text from Firebase
    }

    //use to create a new message
    public Message(User sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }
}
