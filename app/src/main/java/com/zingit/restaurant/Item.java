package com.zingit.restaurant;

public class Item {
    private String name;
    private int price;
    private String outletID;
    private int maxAllowed;
    private String itemImage;
    private boolean vegOrNot;
    private boolean availableOrNot;
    private String category;
    private String id;

    /* only fetching allowed. immutable */

    public Item() {
    }

    public void setAvailableOrNot(boolean availableOrNot) {
        this.availableOrNot = availableOrNot;
    }

    public Item(String name, int price, String outletID, int maxAllowed, String itemImage, boolean vegOrNot, boolean availableOrNot, String category, String id) {
        this.name = name;
        this.price = price;
        this.outletID = outletID;
        this.maxAllowed = maxAllowed;
        this.itemImage = itemImage;
        this.vegOrNot = vegOrNot;
        this.availableOrNot = availableOrNot;
        this.category = category;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getOutletID() {
        return outletID;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }

    public String getItemImage() {
        return itemImage;
    }

    public boolean isVegOrNot() {
        return vegOrNot;
    }

    public boolean isAvailableOrNot() {
        return availableOrNot;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }
}
