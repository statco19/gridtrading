package com.ybigtaconference.gridtrading.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private static final String TOPIC = "grid-test";
    private final KafkaTemplate<String, String> customKafkaTemplate;

    public void send(String message) {
        customKafkaTemplate.send(TOPIC, message);
    }
}
