package com.zingit.restaurant;

import com.google.firebase.Timestamp;

public class Earnings {

    private double totalAmount;
    private boolean paidStatus;
    String outletID;
    Timestamp date;

    public Earnings(double totalAmount, boolean paidStatus, String outletID, Timestamp date) {
        this.totalAmount = totalAmount;
        this.paidStatus = paidStatus;
        this.outletID = outletID;
        this.date = date;
    }

    public Earnings()
    {

    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(boolean paidStatus) {
        this.paidStatus = paidStatus;
    }

    public String getOutletID() {
        return outletID;
    }

    public void setOutletID(String outletID) {
        this.outletID = outletID;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }






}
