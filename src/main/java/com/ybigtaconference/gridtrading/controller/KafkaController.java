package com.ybigtaconference.gridtrading.controller;

import com.ybigtaconference.gridtrading.db.entity.Order;
import com.ybigtaconference.gridtrading.domain.UserInput;
import com.ybigtaconference.gridtrading.producer.KafkaProducer;
import com.ybigtaconference.gridtrading.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducer producer;
    private final OrderService orderService;

    @PostMapping
    public String sendMessage(@RequestParam("message") String message) {
        producer.send(message);
        return "success";
    }

    @PostMapping("/userInput")
    public String get_balance(@RequestBody UserInput userInput) {
        System.out.println(userInput.getAccessKey());
        System.out.println(userInput.getSecretKey());
        System.out.println(userInput.getBudget());
        System.out.println(userInput.getStopLoss());
        System.out.println(userInput.getGridNum());
        return "UserInput accepted.";
    }

//    @PostMapping
//    public String saveOrder(@RequestBody Order order) {
//        orderService.saveOrder(order);
//        return "new order saved.";
//    }
}
