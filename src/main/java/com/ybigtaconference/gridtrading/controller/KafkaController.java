package com.ybigtaconference.gridtrading.controller;

import com.ybigtaconference.gridtrading.domain.UserInput;
import com.ybigtaconference.gridtrading.producer.KafkaProducer;
import com.ybigtaconference.gridtrading.service.OrderService;
import com.ybigtaconference.gridtrading.service.UpbitImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducer producer;
    private final OrderService orderService;
    private final UpbitImpl upbit;

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

        upbit.setAccessKey(userInput.getAccessKey());
        upbit.setSecretKey(userInput.getSecretKey());

        upbit.get_balances();

        return "UserInput accepted.";
    }

    @PostMapping("/order-test")
    public String order(@RequestBody UserInput userInput) {
        upbit.setAccessKey(userInput.getAccessKey());
        upbit.setSecretKey(userInput.getSecretKey());

        try {
            String res = upbit.order("KRW-BTC", 30000000f, 0.0002f,"bid", "limit");
//            String res = EntityUtils.toString(orderResponse, "UTF-8");
            log.info("order response {}", res);
            // while loop until wait
            // when done, send to broker
            producer.send(res);
        } catch (Exception e) {
            log.info("error at Kafka Controller");
            e.printStackTrace();
        }

        return "success";
    }

//    @PostMapping
//    public String saveOrder(@RequestBody Order order) {
//        orderService.saveOrder(order);
//        return "new order saved.";
//    }
}
