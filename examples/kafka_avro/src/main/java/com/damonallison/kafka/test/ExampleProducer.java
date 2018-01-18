package com.damonallison.kafka.simple;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * An example which publishes messages to a Kafka topic serialized using Apache Avro.
 *
 * The goals of this example are:
 *
 * 1. Describe interesting / relevant configuration options.
 * 2. Show how to enable EoS (Exactly Once Semantics).
 * 3. Show how to manually enable offset persistence (and describe *why* you'd do this).
 *
 *
 * **************************
 * On Avro serialization:
 * **************************
 *
 * Avro serialization requires a schema registry to be running.
 * Schema registry is available in source form on github or installed via Confluent.
 *
 * [Kafka Schema Registry](https://github.com/confluentinc/schema-registry)
 *
 * * Avro is a very simple typed schema format. See: /src/main/avro/*.avsc
 * * Compile the .avsc files into java classes using Maven. There is a `generate-sources` command.
 *
 *
 * This example will *not* use Avro to avoid having to install a Kafka Schema Registry instance from source.
 *
 * TODO:
 *  * Implement EoS / Transactions.
 *  * Understand retries.
 */
public class ExampleProducer {

    private static final String TOPIC = "com.damonallison.test";

    public static void main(String[] args) throws Exception {

        try (KafkaProducer<Integer, String> producer = new KafkaProducer<>(getProperties())) {

            System.out.println("Producer running.");
            System.out.println("\"q\" to quit");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                int i = 0;
                while (true) {
                    System.out.println(">");
                    String s = br.readLine().toLowerCase().trim();
                    if (s.equals("q")) {
                        System.out.println("Exiting. Good bye");
                        return;
                    }

                    ProducerRecord<Integer, String> record = new ProducerRecord<>(TOPIC, i, s);
                    RecordMetadata rm = producer.send(record).get();
                    System.out.println(String.format("Sent record: %s partition=%d offset=%d", rm.topic(), rm.partition(), rm.offset()));
                    i++;
                }
            }
        }
    }

    private static Properties getProperties() {

        Properties props = new Properties();

        //
        // Bootstrap servers is the only *required* configuration setting.
        // Always include > 1 server for redundancy purposes.
        // Bootstrap servers are truly for bootstrapping the client. Once connected to a
        // bootstrap server, the client will retrieve the full broker list.
        //
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093");

        //
        // Wait until all in-sync replicas acknowledge the record.
        // This guarantees the record will not be lost as long as at least one in-sync replica remains alive.
        //
        props.put(ProducerConfig.ACKS_CONFIG, "1");


        // TODO: Determine if we want to allow retries. If so, what should these values be set to and why?
        //        props.put(ProducerConfig.RETRIES_CONFIG, "10");
        //        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, "1000");

        //
        // Ensures that if a message send fails and is retried, messages do not
        // get sent out of order.
        //
        //        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "1");

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return props;
    }




}
