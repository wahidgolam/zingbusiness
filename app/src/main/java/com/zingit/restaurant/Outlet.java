package com.zingit.restaurant;

public class Outlet {
    private String name;
    private String openStatus;
    private String outletImage;
    private String description;
    private int category;
    //category 0: snacks (non-qsr)
    //category 1: qsr chain
    //category 2: fine dine
    private String campusID;
    private String id;
    private String zingTime;
    private int numOrders;

    private int deliveryCharges;
    private Boolean isDelivery;



    private Boolean isTakeAway;
    private Boolean isDinein;
    private int takeAwayCharges;



    private Boolean isPrinterAvailable;


    public Outlet(String name, String openStatus, String outletImage, String description, int category, String campusID, String id, String zingTime,int deliveryCharges,boolean isDelivery,boolean isTakeAway,boolean isDinein, int takeAwayCharges,Boolean isPrinterAvailable) {
        this.name = name;
        this.openStatus = openStatus;
        this.outletImage = outletImage;
        this.description = description;
        this.category = category;
        this.campusID = campusID;
        this.id = id;
        this.zingTime = zingTime;
        this.deliveryCharges = deliveryCharges;
        this.isDelivery = isDelivery;
        this.isTakeAway = isTakeAway;
        this.isDinein = isDinein;
        this.takeAwayCharges = takeAwayCharges;
        this.isPrinterAvailable = isPrinterAvailable;

    }

    public Outlet() {
    }

    public void setOpenStatus(String openStatus) {
        this.openStatus = openStatus;
    }

    public String getName() {
        return name;
    }

    public String getOpenStatus() {
        return openStatus;
    }

    public String getOutletImage() {
        return outletImage;
    }

    public String getDescription() {
        return description;
    }

    public int getCategory() {
        return category;
    }

    public String getCampusID() {
        return campusID;
    }

    public String getId() {
        return id;
    }

    public String getZingTime() {
        return zingTime;
    }

    public void setZingTime(String zingTime) {
        this.zingTime = zingTime;
    }

    public int getNumOrders() {
        return numOrders;
    }

    public void setNumOrders(int numOrders) {
        this.numOrders = numOrders;
    }
    public int getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(int deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public Boolean getDelivery() {
        return isDelivery;
    }

    public void setDelivery(Boolean delivery) {
        isDelivery = delivery;
    }

    public Boolean getTakeAway() {
        return isTakeAway;
    }

    public void setTakeAway(Boolean takeAway) {
        isTakeAway = takeAway;
    }

    public Boolean getDinein() {
        return isDinein;
    }

    public void setDinein(Boolean dinein) {
        isDinein = dinein;
    }

    public int getTakeAwayCharges() {
        return takeAwayCharges;
    }

    public void setTakeAwayCharges(int takeAwayCharges) {
        this.takeAwayCharges = takeAwayCharges;
    }

    public Boolean getisPrinterAvailable() {
        return isPrinterAvailable;
    }

    public void setisPrinterAvailable(Boolean printerAvailable) {
        isPrinterAvailable = printerAvailable;
    }
}
