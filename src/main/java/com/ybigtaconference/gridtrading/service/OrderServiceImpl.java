package com.ybigtaconference.gridtrading.service;

import com.ybigtaconference.gridtrading.db.entity.Order;
import com.ybigtaconference.gridtrading.db.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public String getCoinTradePrice(String coin) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String URL = "https://api.upbit.com/v1/trades/ticks?market=" + coin + "&count=1";

        Request request = new Request.Builder()
                .url(URL)
                .get()
                .addHeader("Accept", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    public String getCandle() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.upbit.com/v1/candles/minutes/30?market=KRW-BTC&count=1")
                .get()
                .addHeader("Accept", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
