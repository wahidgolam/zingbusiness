package com.zingit.restaurant;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Payment {
    String id;
    String paymentOrderID;
    String userID;
    private String outletID;
    String couponID;

    ArrayList<OrderItem> orderItems;

    String userName;

    private int statusCode;

    double basePrice;
    double taxesAndCharges;
    double couponDiscount;
    double  totalAmountPaid;

    Timestamp zingTime;
    Timestamp placedTime;
    Timestamp reactionTime;
    Timestamp preparedTime;
    Timestamp collectedTime;

    public Payment(String userID, String userName, String outletID, String couponID, ArrayList<OrderItem> orderItems, int statusCode, double basePrice, double taxesAndCharges, double couponDiscount, double totalAmountPaid, Timestamp placedTime) {
        this.userID = userID;
        this.userName = userName;
        this.outletID = outletID;
        this.couponID = couponID;
        this.orderItems = orderItems;
        this.statusCode = statusCode;
        this.basePrice = basePrice;
        this.taxesAndCharges = taxesAndCharges;
        this.couponDiscount = couponDiscount;
        this.totalAmountPaid = totalAmountPaid;
        this.placedTime = placedTime;
    }

    public Payment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentOrderID() {
        return paymentOrderID;
    }

    public void setPaymentOrderID(String paymentOrderID) {
        this.paymentOrderID = paymentOrderID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getOutletID() {
        return outletID;
    }

    public void setOutletID(String outletID) {
        this.outletID = outletID;
    }

    public String getCouponID() {
        return couponID;
    }

    public void setCouponID(String couponID) {
        this.couponID = couponID;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getTaxesAndCharges() {
        return taxesAndCharges;
    }

    public void setTaxesAndCharges(double taxesAndCharges) {
        this.taxesAndCharges = taxesAndCharges;
    }

    public double getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(double couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public double getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(double totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public Timestamp getZingTime() {
        return zingTime;
    }

    public void setZingTime(Timestamp zingTime) {
        this.zingTime = zingTime;
    }

    public Timestamp getPlacedTime() {
        return placedTime;
    }

    public void setPlacedTime(Timestamp placedTime) {
        this.placedTime = placedTime;
    }

    public Timestamp getReactionTime() {
        return reactionTime;
    }

    public void setReactionTime(Timestamp reactionTime) {
        this.reactionTime = reactionTime;
    }

    public Timestamp getPreparedTime() {
        return preparedTime;
    }

    public void setPreparedTime(Timestamp preparedTime) {
        this.preparedTime = preparedTime;
    }

    public Timestamp getCollectedTime() {
        return collectedTime;
    }

    public void setCollectedTime(Timestamp collectedTime) {
        this.collectedTime = collectedTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}

