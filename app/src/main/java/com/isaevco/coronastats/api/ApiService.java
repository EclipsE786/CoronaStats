package com.isaevco.coronastats.api;

import com.isaevco.coronastats.pojo.Response;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("statistics")
    Observable<Response> getResponseOfAllCountries();

    @GET("statistics")
    Observable<Response> getResponseForCountry(@Query("country") String country);

    @GET("history")
    Observable<Response> getHistoryUsingDate(@Query("country") String country, @Query("day") String date);
}
