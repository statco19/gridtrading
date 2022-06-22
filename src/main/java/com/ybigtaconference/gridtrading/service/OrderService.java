package com.ybigtaconference.gridtrading.service;

import com.ybigtaconference.gridtrading.db.entity.Order;

import java.io.IOException;

public interface OrderService {
    public Order saveOrder(Order order);
    public String getCoinTradePrice(String coin) throws IOException;
    public String getCandle() throws IOException;
}
