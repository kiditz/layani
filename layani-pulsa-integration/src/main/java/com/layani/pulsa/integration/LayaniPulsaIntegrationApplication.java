package com.layani.pulsa.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
@EnableDiscoveryClient
@EnableScheduling
@ImportResource({"classpath*:spring-context.xml"})
@ComponentScan

public class LayaniPulsaIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(LayaniPulsaIntegrationApplication.class, args);
	}

}

