package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

public class SuspiciousTransactionMonitor {
    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "transaction-monitor");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> inputStream = builder.stream("payments-queue");

        inputStream
                .mapValues(value -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        return mapper.readValue(value, EventRecord.class);
                    } catch (Exception e) {
                        return null; // Skip bad records
                    }
                })
                .filter((key, event) -> event != null && event.getPaymentAmount() >= 15000.0)
                .mapValues(event -> {
                    System.out.println("Suspicious Payment Detected: " + event.toString());
                    return event.toString();
                })
                .to("alerts-stream");

        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        System.out.println("SuspiciousTransactionMonitor is running...");
    }
}

