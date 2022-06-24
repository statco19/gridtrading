package com.ybigtaconference.gridtrading.controller;

import com.ybigtaconference.gridtrading.domain.UserInput;
import com.ybigtaconference.gridtrading.producer.KafkaProducer;
import com.ybigtaconference.gridtrading.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
@CrossOrigin
public class KafkaController {

    private final KafkaProducer producer;
    private final OrderService orderService;
    private final UpbitImpl upbit;
    private final UtilService utilService;
    private final BotServiceImpl botService;

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
            String res = upbit.order("KRW-BTC", 30000000., 0.0002,"bid", "limit");
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

    @PostMapping("/grid-trading")
    public String gridTrading(@RequestBody UserInput userInput) throws Exception {
//        BotServiceImpl botService = new BotServiceImpl(userInput.getAccessKey(), userInput.getSecretKey());
        botService.setAccessKey(userInput.getAccessKey());
        botService.setSecretKey(userInput.getSecretKey());

//        botService.setAccessKey(userInput.getAccessKey());
//        botService.setSecretKey(userInput.getSecretKey());

        Params params = new Params();
        params.setMode("Arithmetic");
        params.setCoin("BTC");
        params.setGrids(userInput.getGridNum());
        params.setBudget(userInput.getBudget());
        params.setLower(userInput.getLower());
        params.setStop_loss(userInput.getStopLoss());

        Envr env;
        try {
            env = botService.set_env(params);
            botService.trade(env);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "trading on process";
    }

}
