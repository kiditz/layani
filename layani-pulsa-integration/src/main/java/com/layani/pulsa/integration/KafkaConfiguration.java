package com.layani.pulsa.integration;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.TopicPartitionInitialOffset;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class KafkaConfiguration {
    @Value("${kafka.address}")
    private String address;
    @Value("${kafka.groupId}")
    private String groupId;
    @Value("${kafka.notification}")
    private String notification;
    @Value("${kafka.order_pulsa}")
    private String orderPulsa;
    @Value("${kafka.post_paid_check}")
    private String postPaidCheck;
    @Value("${kafka.post_paid_pay}")
    private String postPaidPay;

    @Bean
    KafkaMessageListenerContainer<String, String> notificationContainer() {
        ContainerProperties properties = new ContainerProperties(this.notification);

        return new KafkaMessageListenerContainer<>(consumerFactory(), properties);
    }

    @Bean
    KafkaMessageListenerContainer<String, String> transactionContainer() {
        ContainerProperties properties = new ContainerProperties(orderPulsa);
        properties.setAckMode(AbstractMessageListenerContainer.AckMode.RECORD);
        properties.setAckOnError(true);
        return new KafkaMessageListenerContainer<>(consumerFactory(), properties);
    }

    @Bean
    KafkaMessageListenerContainer<String, String> postPaidCheckContainer() {
        ContainerProperties properties = new ContainerProperties(this.postPaidCheck);
        properties.setAckMode(AbstractMessageListenerContainer.AckMode.RECORD);
        properties.setAckOnError(true);
        return new KafkaMessageListenerContainer<>(consumerFactory(), properties);
    }

    @Bean
    KafkaMessageListenerContainer<String, String> postPaidPayContainer() {
        ContainerProperties properties = new ContainerProperties(this.postPaidPay);
        properties.setAckMode(AbstractMessageListenerContainer.AckMode.RECORD);
        properties.setAckOnError(true);
        return new KafkaMessageListenerContainer<>(consumerFactory(), properties);
    }

    @Bean
    ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, this.address);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, this.groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        // set more properties
        return new DefaultKafkaConsumerFactory<>(props);
    }
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<String, String>(producerFactory());
    }

    @Bean
    ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, address);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }


}
