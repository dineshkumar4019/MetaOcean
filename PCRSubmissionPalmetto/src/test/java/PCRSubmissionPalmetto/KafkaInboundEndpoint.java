package PCRSubmissionPalmetto;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import logger.FunctionalBlockLogger;


public class KafkaInboundEndpoint<T> {
	private static final FunctionalBlockLogger logger = new FunctionalBlockLogger(KafkaInboundEndpoint.class);
	private KafkaConsumer<String, T> consumer;
	private ObjectMapper objectMapper = new ObjectMapper()
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.enable(SerializationFeature.INDENT_OUTPUT).findAndRegisterModules();
	AtomicBoolean stop = new AtomicBoolean();
	
	public KafkaInboundEndpoint(Properties kafkaProperties) throws SecurityException, IOException {
		consumer = new KafkaConsumer<String, T>(kafkaProperties);
	}
	
	public void start(ITaskHandler<List<T>> microworker,int count, String topic, int currentInput) {
		consumer.subscribe(Arrays.asList(topic));
		for (int i = 0; i < count; i++) {
			stop.set(false);
			while (!stop.get()) {
				try {
					ConsumerRecords<String, T> records = consumer.poll(Duration.ofMillis(20000));
					List<T> events = new ArrayList<>();
					for (ConsumerRecord<String, T> consumerRecord : records) {
						events.add(consumerRecord.value());
						logger.info("offset = " +  consumerRecord.offset());
						
					}
					if (!events.isEmpty()) {
						logger.info("Specific Input " + i + " to start");
						microworker.perform(events, currentInput);
						stop();
					}
				} catch (Exception e) {
					logger.error(e.getStackTrace().toString());
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
