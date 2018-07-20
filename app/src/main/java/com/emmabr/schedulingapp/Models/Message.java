package com.emmabr.schedulingapp.Models;

import com.google.firebase.database.FirebaseDatabase;

public class Message {
    String userID;
    String messageText;
    String createdAt;

    public Message(String userID, String messageText, String createdAt) {
        this.userID = userID;
        this.messageText = messageText;
        this.createdAt = createdAt;
    }

    public String getUserID() {
        return userID;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void saveMessage(Message inputMessage, String group_id) {
        FirebaseDatabase.getInstance().getReference().child("groups").child(group_id).child("chatMessages").push().setValue(inputMessage);
    }

}
