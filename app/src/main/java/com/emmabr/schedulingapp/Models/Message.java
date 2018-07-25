package com.emmabr.schedulingapp.Models;

import com.google.firebase.database.FirebaseDatabase;

public class Message {
    String userID;
    String nickName;
    String messageText;
    String imageURL;
    String pollTitle;
    String optionOne;
    String optionTwo;
    String optionThree;
    String optionFour;
    String createdAt;

    //unused strings left null
    public Message(String userID, String nickName, String messageText, String imageURL, String pollTitle, String optionOne, String optionTwo, String optionThree, String optionFour, String createdAt) {
        this.userID = userID;
        this.nickName = nickName;
        this.messageText = messageText;
        this.imageURL = imageURL;
        this.pollTitle = pollTitle;
        this.optionOne = optionOne;
        this.optionTwo = optionTwo;
        this.optionThree = optionThree;
        this.optionFour = optionFour;
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

    public String getOptionOne() {
        return optionOne;
    }

    public String getOptionTwo() {
        return optionTwo;
    }

    public String getOptionThree() {
        return optionThree;
    }

    public String getOptionFour() {
        return optionFour;
    }

    public static void saveMessage(Message inputMessage, String group_id) {
        FirebaseDatabase.getInstance().getReference().child("groups").child(group_id).child("chatMessages").push().setValue(inputMessage);
    }

}
