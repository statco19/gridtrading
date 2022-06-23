package com.ybigtaconference.gridtrading.consumer;

import com.ybigtaconference.gridtrading.db.entity.Order;
import com.ybigtaconference.gridtrading.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "grid-test",
            groupId = "grid-demo",
            containerFactory = "customContainerFactory")
    public void consume(String message) throws IOException {
        System.out.println(String.format("Consumed message : %s", message));
        Order order = new Order(
                Integer.parseInt("26000000"),
                Double.parseDouble("0.0002"),
                "buy"); // price, volume, market

        // gson library json parsing code

        orderService.saveOrder(order);
    }
}
