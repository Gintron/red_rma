package com.marijan.red.Fragments;

import com.marijan.red.Notifications.MyResponse;
import com.marijan.red.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAARMKLxf0:APA91bGHTpCcny_fPwoprKHAkCuWQcdU8W8qdzjLndIllnekOipgm-QscPFalLl4dpx5ROAcHT0tYlc_b9EeIdtKG1CzW8V231IyT85yCP-donhB9WKoVGIjQz9KIz6JkT7tDvuaFzbf"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}