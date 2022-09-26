package com.zingit.restaurant.remote;

import com.zingit.restaurant.model.RefundToken;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RefundCloudFunction {
    @GET("refund")
    Observable<RefundToken> getToken(@Query("orderId") String orderId,
                                     @Query("refundAmount") String refundAmount);





}
