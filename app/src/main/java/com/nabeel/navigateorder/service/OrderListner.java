package com.nabeel.navigateorder.service;

import com.nabeel.navigateorder.model.Order;

import java.util.List;

public interface OrderListner {
    void getOrderResponse(List<Order> body);

    void onFailure(String s);
}
