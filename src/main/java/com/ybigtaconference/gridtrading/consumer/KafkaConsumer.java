package com.ybigtaconference.gridtrading.consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.ybigtaconference.gridtrading.db.entity.Order;
import com.ybigtaconference.gridtrading.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final OrderService orderService;
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @KafkaListener(topics = "grid-test",
            groupId = "grid-demo",
            containerFactory = "customContainerFactory")
    public void consume(String message) throws IOException {
        System.out.println(String.format("Consumed message : %s", message));

        try {
            String uuid = gson.fromJson(message, JsonElement.class)
                    .getAsJsonObject()
                    .get("uuid")
                    .getAsString();

            String price = gson.fromJson(message, JsonElement.class)
                    .getAsJsonObject()
                    .get("price")
                    .getAsString();

            String volume = gson.fromJson(message, JsonElement.class)
                    .getAsJsonObject()
                    .get("volume")
                    .getAsString();

            String side = gson.fromJson(message, JsonElement.class)
                    .getAsJsonObject()
                    .get("side")
                    .getAsString();

            String state = gson.fromJson(message, JsonElement.class)
                    .getAsJsonObject()
                    .get("state")
                    .getAsString();


            if(state.equals("wait")) {
                if(orderService.findOrderByUuid(uuid) == null) {
                    Order order = new Order(
                            uuid,
                            price,
                            "0.0",
                            volume,
                            side); // price, volume, market

                    orderService.saveOrder(order);
                }
            } else if (state.equals("done")) {
               orderService.modify_trade_price(uuid, price);
            }

        } catch (Exception e) {
            log.info("error at Kafka Consumer");
            e.printStackTrace();
        }
    }
}
