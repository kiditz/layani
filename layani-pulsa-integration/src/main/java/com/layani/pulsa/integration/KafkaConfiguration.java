package com.layani.pulsa.integration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class KafkaConfiguration {
    @Value("${kafka.address}")
    private String address;
    @Value("${kafka.groupId}")
    private String groupId;
    @Value("${kafka.notification}")
    private String topic;

    @Bean
    KafkaMessageListenerContainer<String, String> notificationContainer() {
        ContainerProperties properties = new ContainerProperties(this.topic);
        //properties.setAckOnError(true);
        return new KafkaMessageListenerContainer<>(consumerFactory1(), properties);
    }

    @Bean
    ConsumerFactory<String, String> consumerFactory1() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.address);
        //props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        // set more properties
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
