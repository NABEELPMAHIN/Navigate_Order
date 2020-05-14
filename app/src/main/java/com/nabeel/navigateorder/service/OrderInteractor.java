package com.nabeel.navigateorder.service;

import com.nabeel.navigateorder.model.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderInteractor {

    public void getOrders(Call<List<Order>> call, final OrderListner listener)
    {
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {

                if (response.isSuccessful())
                {

                    listener.getOrderResponse(response.body());
                }
                else {
                    listener.onFailure("Something went worng..\n Please try again");
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                listener.onFailure("Server Problem");
            }
        });
    }

}
