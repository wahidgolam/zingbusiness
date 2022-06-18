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


    public Outlet(String name, String openStatus, String outletImage, String description, int category, String campusID, String id, String zingTime) {
        this.name = name;
        this.openStatus = openStatus;
        this.outletImage = outletImage;
        this.description = description;
        this.category = category;
        this.campusID = campusID;
        this.id = id;
        this.zingTime = zingTime;
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
}
