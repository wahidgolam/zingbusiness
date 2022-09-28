package com.zingit.restaurant;

import com.google.firebase.Timestamp;

public class Order {

    String orderID;
    //seconds
    //-1 if does not exist

    private String outletID;
    private String studentID;
    private String paymentOrderID;

    private Timestamp zingTime;
    private Timestamp placedTime;
    private Timestamp reactionTime;
    private Timestamp preparedTime;
    private Timestamp collectedTime;

    private int paymentStatus;
    private int statusCode;

    String itemID;
    String itemName;
    String itemImage;
    int quantity;
    int otp;
    private long totalAmount;

    public long getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(long discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    private long discountedAmount;

    //status code for orders
    //placed - 1
    //accepted - 2
    //prepared - 3
    //collected, feedback pending - 4
    //5 - feedback given
    //6 - feedback closed
    //declined : 0
    //user acknowledges decline : 5

    //payment status - 0 : fail, 1 - pass
    //active if statusCode>0 and <4

    //history if status == 4
    //request i status ==1


    public Order() {
    }

    public Order(String outletID, String studentID, Timestamp placedTime, int statusCode, String itemID, int quantity, long totalAmount, String itemName, String itemImage) {
        this.outletID = outletID;
        this.studentID = studentID;
        this.placedTime = placedTime;
        this.statusCode = statusCode;
        this.itemID = itemID;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.itemName = itemName;
        this.itemImage = itemImage;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public Timestamp getZingTime() {
        return zingTime;
    }

    public String getOutletID() {
        return outletID;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getPaymentOrderID() {
        return paymentOrderID;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public Timestamp getPlacedTime() {
        return placedTime;
    }

    public Timestamp getReactionTime() {
        return reactionTime;
    }

    public Timestamp getPreparedTime() {
        return preparedTime;
    }

    public Timestamp getCollectedTime() {
        return collectedTime;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getItemID() {
        return itemID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setZingTime(Timestamp zingTime) {
        this.zingTime = zingTime;
    }

    public void setPaymentOrderID(String paymentOrderID) {
        this.paymentOrderID = paymentOrderID;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setCollectedTime(Timestamp collectedTime) {
        this.collectedTime = collectedTime;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public void setReactionTime(Timestamp reactionTime) {
        this.reactionTime = reactionTime;
    }

    public void setPreparedTime(Timestamp preparedTime) {
        this.preparedTime = preparedTime;
    }

    public void setPlacedTime(Timestamp placedTime) {
        this.placedTime = placedTime;
    }
}
