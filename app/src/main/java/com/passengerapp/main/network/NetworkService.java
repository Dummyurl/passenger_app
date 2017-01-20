package com.passengerapp.main.network;

import com.passengerapp.BuildConfig;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by adventis on 10/15/15.
 */
public class NetworkService {
    private final String END_POINT = BuildConfig.URL;

    public NetworkApi getApi() {
        return api;
    }

    private NetworkApi api;

    public NetworkService() {

        Retrofit service = new Retrofit.Builder()
                .baseUrl(END_POINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        api = service.create(NetworkApi.class);
    }

    public NetworkService(String endPoint) {

        Retrofit service = new Retrofit.Builder()
                .baseUrl(endPoint)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        api = service.create(NetworkApi.class);
    }
}
