package com.emmabr.schedulingapp.Models;

import java.util.ArrayList;

public class User {

    // data in schema
    public String email;
    public String nickName;
    public String image;
    static public ArrayList<String> users = new ArrayList<>();

    //getters
    public String getName() {
        return nickName;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    //setters
    public void setImage(String image) {
        this.image = image;
    }
}