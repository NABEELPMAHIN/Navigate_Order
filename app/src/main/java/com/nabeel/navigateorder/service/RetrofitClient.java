package com.nabeel.navigateorder.service;

import com.nabeel.navigateorder.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nabeel.navigateorder.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static Retrofit getClientRequest(String baseUrl) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okClient = new OkHttpClient.Builder()
                .connectTimeout(Constants.TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(Constants.TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response response=chain.proceed(chain.request());
                        Response modified=response.newBuilder()
                                .addHeader("Content-Type", "application/json; charset=utf-8")
                                .build();
                        return modified;
                    }
                });

        if (BuildConfig.DEBUG) {
            okClient.addInterceptor(interceptor);
        }

        Gson gson = new GsonBuilder().serializeNulls().create();
        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return client;
    }

}

