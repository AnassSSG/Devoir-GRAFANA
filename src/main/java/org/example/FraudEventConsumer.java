package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class FraudEventConsumer {
    private static final String ALERT_TOPIC = "alerts-stream";
    private static final String DB_URL = "http://localhost:8086";
    private static final String DB_TOKEN = "admin1234";
    private static final String DB_ORG = "org";
    private static final String DB_BUCKET = "fraud_alerts";
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static void main(String[] args) {
        // Kafka Consumer Configurations
        Properties consumerConfig = new Properties();
        consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "fraud-event-consumer");
        consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // InfluxDB Client Initialization
        InfluxDBClient influxClient = InfluxDBClientFactory.create(
                DB_URL, DB_TOKEN.toCharArray(), DB_ORG, DB_BUCKET
        );

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig);
             WriteApi writeApi = influxClient.getWriteApi()) {

            consumer.subscribe(Collections.singletonList(ALERT_TOPIC));
            System.out.println("FraudEventConsumer is listening for fraud alerts...");

            while (true) {
                // Poll Kafka for messages
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));

                records.forEach(record -> {
                    try {
                        // Deserialize JSON into EventRecord
                        EventRecord event = JSON_MAPPER.readValue(record.value(), EventRecord.class);

                        // Create InfluxDB data point
                        Point point = Point.measurement("fraudulent_payments")
                                .addTag("customerCode", event.getCustomerCode())
                                .addField("paymentAmount", event.getPaymentAmount())
                                .time(event.getTimestamp(), WritePrecision.MS);

                        // Write data point to InfluxDB
                        writeApi.writePoint(point);

                        System.out.println("Saved Fraud Event: " + event);
                    } catch (Exception e) {
                        System.err.println("Error processing message: " + e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Error in FraudEventConsumer: " + e.getMessage());
        } finally {
            influxClient.close();
            System.out.println("InfluxDB connection closed.");
        }
    }
}