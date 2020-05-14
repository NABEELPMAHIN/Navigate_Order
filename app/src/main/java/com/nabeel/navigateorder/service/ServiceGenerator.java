package com.nabeel.navigateorder.service;

import com.nabeel.navigateorder.Constants;

import retrofit2.Retrofit;

public class ServiceGenerator {

    public static <S> S createRestService(Class<S> serviceClass) {
        Retrofit retrofit = RetrofitClient.getClientRequest(Constants.BASE_URL);
        return retrofit.create(serviceClass);
    }

}

