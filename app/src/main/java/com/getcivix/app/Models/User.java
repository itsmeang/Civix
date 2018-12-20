package com.getcivix.app.Models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String userName;
    public String email;
    public String gender;
    public String interest;
    public String userUid;

    public String uploadedProfileImageKey;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String userUid,String userName, String email, String gender, String interest, String uploadedProfileImageKey) {
        this.userUid = userUid;
        this.userName = userName;
        this.email = email;
        this.gender = gender;
        this.interest = interest;

        this.uploadedProfileImageKey = uploadedProfileImageKey;
    }

    @Override
    public String toString() {
        return "User{" +
                "userUid='" + userUid + '\'' +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", interest='" + interest + '\'' +
                ", uploadedProfileImageKey='" + uploadedProfileImageKey + '\'' +
                '}';
    }
}
