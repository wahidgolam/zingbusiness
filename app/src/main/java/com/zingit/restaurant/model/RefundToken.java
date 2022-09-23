package com.zingit.restaurant.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RefundToken {

    @SerializedName("cf_payment_id")
    @Expose
    public String cf_refund_id;

    public RefundToken(String cf_refund_id) {
        this.cf_refund_id = cf_refund_id;
    }

    public RefundToken() {
    }

    public String getCf_refund_id() {
        return cf_refund_id;
    }

    public void setCf_refund_id(String cf_refund_id) {
        this.cf_refund_id = cf_refund_id;
    }
}
