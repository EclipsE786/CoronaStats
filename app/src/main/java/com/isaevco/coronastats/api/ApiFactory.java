package com.isaevco.coronastats.api;

import com.isaevco.coronastats.screens.info.CoronaInfoPresenter;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {

    private static ApiFactory apiFactory;
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://covid-193.p.rapidapi.com/";

    private ApiFactory() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();

        client.addInterceptor(chain -> {
            Request request = chain.request().newBuilder().addHeader("x-rapidapi-key", CoronaInfoPresenter.API_KEY).addHeader("x-rapidapi-host", CoronaInfoPresenter.HOST).build();
            return chain.proceed(request);
        });

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .client(client.build())
                .build();
    }

    public static ApiFactory getInstance(){
        if (apiFactory == null){
            apiFactory = new ApiFactory();
        }
        return apiFactory;
    }

    public ApiService getApiService(){
        return retrofit.create(ApiService.class);
    }
}
