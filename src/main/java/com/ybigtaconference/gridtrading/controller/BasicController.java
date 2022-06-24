package com.ybigtaconference.gridtrading.controller;

import com.ybigtaconference.gridtrading.db.entity.Order;
import com.ybigtaconference.gridtrading.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Controller
@RequestMapping
@RequiredArgsConstructor
@CrossOrigin
public class BasicController {

    private final OrderService orderService;

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello world";
    }

    @PostMapping("/addorder")
    @ResponseBody
    public String add(@RequestBody Order order) {
        orderService.saveOrder(order);
        return "new order saved.";
    }

    @PostMapping("/recent-trade")
    @ResponseBody
    public String show(@RequestParam String coin) throws IOException {
        return orderService.getCoinTradePrice(coin);
    }

    @GetMapping("/recent-trade")
    @ResponseBody
    public String show2() throws IOException {
        return orderService.getCoinTradePrice("KRW-BTC");
    }

    @GetMapping("/candle")
    @ResponseBody
    public String candle() throws IOException {
        return orderService.getCandle();
    }

//    @PostConstruct
//    public void init() {
//        orderService.saveOrder(new Order(25000000.0, 0.002, "buy"));
//        orderService.saveOrder(new Order(26000000.0, 0.004, "buy"));
//    }
}
