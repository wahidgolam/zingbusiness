package com.zingit.restaurant;

import java.util.ArrayList;

public class Dataholder {
    public static OwnerUser ownerUser;
    public static ArrayList<Order> orderList;
    public static Outlet outlet;
    public static String studentID;
    public static String FCMToken="";
    public static ArrayList<Payment> recentOrderList = new ArrayList<>();
    public static ArrayList<Payment> preparingOrderList= new ArrayList<>();
    public static ArrayList<Integer> totalAmount;
    public static ArrayList<Payment> historyOrderList = new ArrayList<>();
    public static Earnings earnings = new Earnings();
    public static Support support ;
}
