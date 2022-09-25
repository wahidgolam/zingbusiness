package com.zingit.restaurant;

class StudentWithFCM {
    String FCMToken;
    String campusId;
    String email;
    String firstName;
    String fullName;
    String phoneNumber;
    String userID;
    String userImage;

    public StudentWithFCM(String FCMToken,String campusId, String email, String firstName, String fullName, String phoneNumber, String userID, String userImage) {
        this.FCMToken = FCMToken;
        this.campusId = campusId;
        this.email = email;
        this.firstName = firstName;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.userID = userID;
        this.userImage = userImage;
    }
    public StudentWithFCM(){};

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }

    public String getCampusId() {
        return campusId;
    }

    public void setCampusId(String campusId) {
        this.campusId = campusId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }




}
