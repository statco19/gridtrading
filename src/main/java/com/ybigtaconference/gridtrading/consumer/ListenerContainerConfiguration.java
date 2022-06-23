package com.ybigtaconference.gridtrading.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class ListenerContainerConfiguration {

//    @Autowired
//    private KafkaTemplate<String, String> template;  // injected for handling error while consuming records

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> customContainerFactory() {
        Map<String, Object> props = new HashMap<>();
        String BOOTSTRAP_SERVERS = "kafka:9092";
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "grid-demo");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // disable auto commit of offsets
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");

        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(props);
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.getContainerProperties().setConsumerRebalanceListener(new ConsumerAwareRebalanceListener() {
            @Override
            public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
//                ConsumerAwareRebalanceListener.super.onPartitionsRevokedBeforeCommit(consumer, partitions);
                log.info("onPartitionsRevokedBeforeCommit - partitions: {}", formatPartitions(partitions));
            }

            @Override
            public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
//                ConsumerAwareRebalanceListener.super.onPartitionsRevokedAfterCommit(consumer, partitions);
                log.info("onPartitionsRevokedAfterCommit - partitions: {}", formatPartitions(partitions));
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
//                ConsumerAwareRebalanceListener.super.onPartitionsAssigned(partitions);
                log.info("onPartitionsAssigned - partitions: {}", formatPartitions(partitions));
            }

            @Override
            public void onPartitionsLost(Collection<TopicPartition> partitions) {
//                ConsumerAwareRebalanceListener.super.onPartitionsLost(partitions);
                log.info("onPartitionsLost - partitions: {}", formatPartitions(partitions));
            }
        });
        factory.setBatchListener(false);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.setConsumerFactory(cf);

//        // handling error while consuming records in a batch
//        factory.setCommonErrorHandler(new DefaultErrorHandler(new DeadLetterPublishingRecoverer(
//                template
//        )));

        return factory;
    }

    private static List<String> formatPartitions(Collection<TopicPartition> partitions) {
        return partitions.stream().map(topicPartition ->
                        String.format("topic: %s, partition: %s", topicPartition.topic(), topicPartition.partition()))
                .collect(Collectors.toList());
    }
}
