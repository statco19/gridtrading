package com.ybigtaconference.gridtrading.controller;

import com.ybigtaconference.gridtrading.db.entity.Order;
import com.ybigtaconference.gridtrading.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
@RequiredArgsConstructor
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
}
