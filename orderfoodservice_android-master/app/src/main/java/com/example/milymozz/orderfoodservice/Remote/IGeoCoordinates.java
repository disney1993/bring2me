package com.example.milymozz.orderfoodservice.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface IGeoCoordinates {
    @GET("maps/api/geocode/json")
    Call<String> getGeocode(@Query("address") String address);

    @GET("maps/api/direction/json")
    Call<String> getDirection(@Query("origin") String origin, @Query("destination") String destination);

}
