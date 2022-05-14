package com.example.demo_project.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({"Content-Type:application/json",
            "Authorization:key=AAAAgKvZteU:APA91bFluo95GbhZ42CydA2r0ffpGNYftzT-KBC5T4ei3XpzXS2edqHfe5rr_4sVZAuTRBNzu9j1cmFelbyUIHyWKzykVqA1IP0EXZfDi5IQH5nITFCEdmzky8SuycE_R8C99PiPtUSX"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
