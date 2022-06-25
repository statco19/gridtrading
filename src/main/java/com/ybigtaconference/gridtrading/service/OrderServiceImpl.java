package com.ybigtaconference.gridtrading.service;

import com.ybigtaconference.gridtrading.db.entity.Order;
import com.ybigtaconference.gridtrading.db.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
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

    @Override
    public void modify_trade_price(String uuid, String trade_price) {

        Order foundOrder = orderRepository.findByUuid(uuid);
        log.info("modify_trade_price: {}",foundOrder.toString());
        log.info("modify_trade_price.order_price: {}",foundOrder.getOrder_price().toString());
        log.info("modify_trade_price.trade_price: {}",foundOrder.getTrade_price().toString());
        foundOrder.setTrade_price(trade_price);
        log.info("modify_trade_price.order_price: {}",foundOrder.getOrder_price().toString());
        log.info("modify_trade_price.trade_price: {}",foundOrder.getTrade_price().toString());

    }

    @Override
    public Order findOrderByUuid(String uuid) {
        return orderRepository.findByUuid(uuid);
    }

}
