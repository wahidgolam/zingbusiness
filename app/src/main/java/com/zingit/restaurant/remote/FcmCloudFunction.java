package com.zingit.restaurant.remote;

import com.zingit.restaurant.model.FcmToken;
import com.zingit.restaurant.model.RefundToken;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface FcmCloudFunction {
    @GET("send")
    Observable<FcmToken> sendNotification(@Query("token") String token,
                                  @Query("title") String title,@Query("body") String body);
}
