package com.nabeel.navigateorder.service;

import com.nabeel.navigateorder.model.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface OrderService {

    @GET("navigate/orders")
    Call<List<Order>> getOrders();

}
