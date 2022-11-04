package com.zingit.restaurant;

public class Support {

    String campusID;
    String id;
    String phoneNumber;

    public Support(String campusID, String id, String phoneNumber) {
        this.campusID = campusID;
        this.id = id;
        this.phoneNumber = phoneNumber;
    }

    Support()
    {

    }


    public String getCampusID() {
        return campusID;
    }

    public void setCampusID(String campusID) {
        this.campusID = campusID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}
