// package org.slerpio.oauth.test;

// import java.util.HashMap;
// import java.util.Map;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.TimeUnit;

// import org.apache.kafka.clients.consumer.ConsumerConfig;
// import org.apache.kafka.clients.consumer.ConsumerRecord;
// import org.apache.kafka.clients.producer.ProducerConfig;
// import org.apache.kafka.common.serialization.StringDeserializer;
// import org.apache.kafka.common.serialization.StringSerializer;
// import org.junit.Test;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
// import org.springframework.kafka.core.DefaultKafkaProducerFactory;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.kafka.core.ProducerFactory;
// import org.springframework.kafka.listener.KafkaMessageListenerContainer;
// import org.springframework.kafka.listener.MessageListener;
// import org.springframework.kafka.listener.config.ContainerProperties;


// public class KafkaTestConsumer {
// 	Logger log = LoggerFactory.getLogger(getClass());

// 	@Test
// 	public void testKafkaConsumer() throws InterruptedException {
// 		CountDownLatch latch = new CountDownLatch(1);
// 		ContainerProperties properties = new ContainerProperties("slerp_register");
// 		properties.setMessageListener(new MessageListener<String, String>() {

// 			@Override
// 			public void onMessage(ConsumerRecord<String, String> record) {
// 				log.info("Message : {}", record);
// 				latch.countDown();
// 			}
// 		});
// 		KafkaMessageListenerContainer<String, String> listener = createContainer(properties);
// 		listener.setBeanName("slerp-kafka");
// 		listener.start();
// 		Thread.sleep(1000); // wait a bit for the container to start
// 		KafkaTemplate<String, String> template = createTemplate();
// 		template.setDefaultTopic("slerp_register");
// 		template.sendDefault("hello", "world");
// 		template.flush();
// 		latch.await(60, TimeUnit.SECONDS);
// 		listener.stop();
// 		log.info("Stop auto");
// 	}

// 	private KafkaMessageListenerContainer<String, String> createContainer(ContainerProperties containerProps) {
// 		Map<String, Object> props = consumerProps();
// 		DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<String, String>(props);
// 		KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(cf,
// 				containerProps);
// 		return container;
// 	}

// 	private KafkaTemplate<String, String> createTemplate() {
// 		Map<String, Object> senderProps = senderProps();
// 		ProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<String, String>(senderProps);
// 		KafkaTemplate<String, String> template = new KafkaTemplate<>(pf);
// 		return template;
// 	}

// 	private Map<String, Object> consumerProps() {
// 		Map<String, Object> props = new HashMap<>();
// 		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.17.0.1:9092");
// 		props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
// 		//props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
// 		//props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
// 		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
// 		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
// 		return props;
// 	}

// 	private Map<String, Object> senderProps() {
// 		Map<String, Object> props = new HashMap<>();
// 		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
// 		props.put(ProducerConfig.RETRIES_CONFIG, 0);
// 		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
// 		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
// 		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
// 		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
// 		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
// 		return props;
// 	}
// }
