package com.emmabr.schedulingapp.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Message {
    String userID;
    String nickName;
    String messageText;
    String imageURL;
    String pollTitle;
    String createdAt;
    String messageID;

    //unused strings left null
    public Message(String userID, String nickName, String messageText, String imageURL, String pollTitle, String createdAt) {
        this.userID = userID;
        this.nickName = nickName;
        this.messageText = messageText;
        this.imageURL = imageURL;
        this.pollTitle = pollTitle;
        this.createdAt = createdAt;
    }

    public String getUserID() {
        return userID;
    }

    public String getNickName() {
        return nickName;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getPollTitle() {
        return pollTitle;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public static DatabaseReference saveMessage(Message inputMessage, String group_id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("groups").child(group_id).child("chatMessages").push();
        ref.setValue(inputMessage);
        return ref;
    }

}
