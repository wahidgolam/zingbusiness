package com.zingit.restaurant;

import com.google.firebase.Timestamp;

public class Earning {
    private int totalAmount;
    private boolean paidStatus;
    private int ordersProcessed;
    String outletID;
    Timestamp date;

    public Earning(int totalAmount, boolean paidStatus, int ordersProcessed, String outletID, Timestamp date) {
        this.totalAmount = totalAmount;
        this.paidStatus = paidStatus;
        this.ordersProcessed = ordersProcessed;
        this.outletID = outletID;
        this.date = date;
    }

    public Earning() {
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public boolean isPaidStatus() {
        return paidStatus;
    }

    public int getOrdersProcessed() {
        return ordersProcessed;
    }

    public String getOutletID() {
        return outletID;
    }

    public Timestamp getDate() {
        return date;
    }
    public void addOrder(Order order){
        totalAmount+=order.getTotalAmount();
        ordersProcessed++;
    }
}
