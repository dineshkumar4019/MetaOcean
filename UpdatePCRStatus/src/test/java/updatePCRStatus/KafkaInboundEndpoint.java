package updatePCRStatus;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.FileHandler;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import logger.FunctionalBlockLogger;


public class KafkaInboundEndpoint<T> {
	//System.setProperty("log4j.configurationFile","log4j2-test.xml");
	private static final FunctionalBlockLogger logger = new FunctionalBlockLogger(KafkaInboundEndpoint.class);
	private KafkaConsumer<String, T> consumer;
	private ObjectMapper objectMapper = new ObjectMapper()
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.enable(SerializationFeature.INDENT_OUTPUT).findAndRegisterModules();
	AtomicBoolean stop = new AtomicBoolean();
	
	public KafkaInboundEndpoint(Properties kafkaProperties) throws SecurityException, IOException {
		consumer = new KafkaConsumer<String, T>(kafkaProperties);
	}
	public void start(ITaskHandler<List<T>> microworker,int count, String topic) {
		consumer.subscribe(Arrays.asList(topic));
		for (int i = 0; i < count; i++) {
			stop.set(false);
			while (!stop.get()) {
				try {
					ConsumerRecords<String, T> records = consumer.poll(Duration.ofMillis(20000));
					List<T> events = new ArrayList<>();
					for (ConsumerRecord<String, T> consumerRecord : records) {
						logger.info("Consumer record: "+ consumerRecord.toString());
						events.add(consumerRecord.value());
						logger.info("offset = " +  consumerRecord.offset()
								+ " key = " +  consumerRecord.key()
								+ " value = " + consumerRecord.value());
						
					}
					if (!events.isEmpty()) {
						logger.info("Input " + i + " to start");
						microworker.perform(events);
						stop();
					}
				} catch (Exception e) {
					logger.error(e.getMessage());
				} finally {
					consumer.commitSync();
				}
			}
		}
	}
	public void stop() {
		stop.set(true);
	}
}
