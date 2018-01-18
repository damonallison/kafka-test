package com.damonallison.kafka.simple;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.Properties;

public class ExampleConsumer {

    private static final String TOPIC = "com.damonallison.test";

    public static void main(String[] args) {


        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093");

        //
        // Must be the same among all clients in same group.
        //
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "example-consumer-group");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "example-client");

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        //
        // Where to start if the client can't find offsets.
        //
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        try (KafkaConsumer<Integer, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Arrays.asList(TOPIC));

            // Retrieves partition assignments, metadata.
            consumer.poll(0);

            consumer.beginningOffsets(consumer.assignment()).forEach((topicPartition, offset) -> {
                System.out.println(String.format("Partition info: topic=%s partition=%d offset=%d", topicPartition.topic(), topicPartition.partition(), offset));
            });

            //
            // Reset to beginning.
            //
            // consumer.seekToBeginning(consumer.assignment());

            while (true) {

                ConsumerRecords<Integer, String> records = consumer.poll(100);
                for (ConsumerRecord<Integer, String> record : records) {
                    System.out.println(String.format("%d : %s", record.key(), record.value()));
                }

            }
        }
    }
}
