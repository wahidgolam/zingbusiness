package com.zingit.restaurant.model;

public class OrderType {

    String orderType;
    String paymentOrderID;
    public OrderType(String orderType, String paymentOrderID) {
        this.orderType = orderType;
        this.paymentOrderID = paymentOrderID;
    }

    public OrderType()
    {

    }



    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPaymentOrderID() {
        return paymentOrderID;
    }

    public void setPaymentOrderID(String paymentOrderID) {
        this.paymentOrderID = paymentOrderID;
    }
}
