package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.util.Properties;
import java.util.Random;

public class EventPublisher {
    public static void main(String[] args) throws InterruptedException {
        Properties config = new Properties();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.serializer", StringSerializer.class.getName());
        config.put("value.serializer", StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(config);
        ObjectMapper mapper = new ObjectMapper();
        Random random = new Random();

        System.out.println("Starting EventPublisher...");

        // Generating and sending data manually
        while (true) {
            String clientCode = "user" + random.nextInt(10000);
            double payment = random.nextInt(30000 - 1000) + 1000 + random.nextDouble();
            long currentTime = System.currentTimeMillis();

            EventRecord record = new EventRecord(clientCode, payment, currentTime);
            String recordAsJson;

            try {
                recordAsJson = mapper.writeValueAsString(record);
            } catch (Exception e) {
                System.err.println("Error converting to JSON: " + e.getMessage());
                continue;
            }

            ProducerRecord<String, String> event =
                    new ProducerRecord<>("payments-queue", clientCode, recordAsJson);

            producer.send(event);
            System.out.println("Event Sent: " + record);

            Thread.sleep(random.nextInt(500) + 1000);
        }
    }
}

