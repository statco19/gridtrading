package com.ybigtaconference.gridtrading.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {


    private static final String TOPIC = "grid-test";
    private final KafkaTemplate<String, String> customKafkaTemplate;

    public void send(String message) {
        log.info("\n\n\n\n\n\n {} \n\n\n\n\n\n", message);
        customKafkaTemplate.send(TOPIC, message);

    }
}
