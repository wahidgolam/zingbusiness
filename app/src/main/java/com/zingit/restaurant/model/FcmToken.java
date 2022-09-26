package com.zingit.restaurant.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FcmToken {

    @SerializedName("multicast_id")
    @Expose
    String multicast_id;
    @SerializedName("success")
    @Expose
    int success;
    @SerializedName("failure")
    @Expose
    int failure;

    public FcmToken(String multicast_id, int success, int failure) {
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
    }
    public FcmToken(){}




    public String getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(String multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }









}
