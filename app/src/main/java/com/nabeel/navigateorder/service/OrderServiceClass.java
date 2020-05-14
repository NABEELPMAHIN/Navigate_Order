package com.nabeel.navigateorder.service;

import com.nabeel.navigateorder.model.Order;

import java.util.List;

import retrofit2.Call;

public class OrderServiceClass {

    OrderListner listener;
    OrderService service;
    OrderInteractor interactor;

    public OrderServiceClass(OrderListner orderListner)
    {
        listener = orderListner;
        service = ServiceGenerator.createRestService(OrderService.class);
        interactor = new OrderInteractor();
    }

    public void getOrders()
    {
        Call<List<Order>> call = service.getOrders();
        interactor.getOrders(call,listener);
    }
}
