package com.example.myproject.Models;

import java.io.Serializable;

public class UserModel implements Serializable
{
    String username,email,mobile,address,imageURL,uid;

    public UserModel(String username, String email, String mobile, String address, String imageURL, String uid) {
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.imageURL = imageURL;
        this.uid = uid;
    }

    public UserModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
