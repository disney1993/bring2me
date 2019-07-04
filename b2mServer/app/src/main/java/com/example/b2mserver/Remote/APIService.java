package com.example.b2mserver.Remote;


import com.example.b2mserver.Model.DataMessage;
import com.example.b2mserver.Model.MyResponse;
import com.example.b2mserver.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAG4P0cDk:APA91bFlE6Tkikguhwen_Eh7sQvxRdugLzxpu2rYhQfWWA9NM2dtxwsJ2M3jUqgk40U6coCV9tzbaVaDzFV3Fxbigf9KgiYzAqxj7X1C35I9Pm94hQyzCLLk8tNTt7T-v69OxEfTMHts"
            }
    )

    @POST("fcm/send")
    retrofit2.Call<MyResponse> sendNotification(@Body DataMessage body);

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

