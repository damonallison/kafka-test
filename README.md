# Kafka

This repository contains documentation and example scripts for working with [Apache Kafka](https://kafka.apache.org/).

## Introduction

Kafka is a distributed streaming platform. Kafka stores streams of records, called topics, in a fault tolerant, scalable way. Clients can read historical records as well as stream events in real time.

Kafka is superior to traditional messaging systems in that:

* Like with queues, messages can be stored and delivered to 1 of N clients.
* Like with pub/sub systems, messages can be broadcasted to multiple subscribers.

Kafka improves on the queuing and pub/sub systems by:

* Allowing messages to be delivered to 1 of N clients in consumer groups. In traditional queuing / pub/sub, each subscriber receives each message. There is no concept of `client group`.
* Durable storage provides playback, historical resume.

### Why Use Kafka?

* To effeciently and quickly distribute data between systems.
* Simplify system-system communication. Avoid API callback hell.

* As a messaging system.
    * Consumers can be scaled as a group.
        * Each message is delivered once per group, not per listener.
    * Maintains an ordered historical record.
        * Ordering is lost in traditional queuing systems, as messages are delivered async to consumers.
        * Consumers can reply past messages.

* As a storage system.
    * Kafka stores records with a fast, constant performance regardless of partition size.
    * Guaranteed delivery.

* As a stream processor.
    * You can build a stream of streams to control data flow.
    * Example : use the Streams API to compute aggregations from stream activity, publishing the results to a new stream.


### Architecture

* Design motivations for Kafka.
    * High throughput to support web scale log streams.
    * Support backlogs to support ETL, bursting data in / out.
    * Low latency message delivery to replace RabbitMQ / messaging systems.
    * Streaming.
    * Fault tolerant.


## Core APIs

* Producer API. Write records to topics.
* Consumer API. Read records from topics.
* Streams API. Consumes input from one or more topics. Optionally transforms data, and writes to one or more output topics.
* Connector API. Infrastructure for building / running reusable producers or consumers that connect topics to existing systems. For example, a Postgres connector which captures every change to a DB.


## Kafka Design

* Cluster
    * The top level entity of a kafka installation.
    * Each topic partition has a "leader" broker within a cluster. All partition I/O occurs with the leader.
    * The cluster manager (zookeeper?) is responsible for managing the leader, selecting a new leader when the current leader dies.

* Broker
    * Unique "server" node within a cluster.

* Topic
    * Message log.

* Partition
    * Each topic is broken into 1-n partitions.
    * Each partition is ordered only within its partition.
    * Partitions provide scale out.
        * Each partition must fit on the servers that host it.
    * Partitions provide parallelism.
        * Each partition is processed by one consumer per consumer group.
    * Partitions are replicated across N servers for fault tolerance.
    * If you need total message ordering, you must only have 1 partition.
        * This will also mean only 1 consumer process per consumer group.

* Producer
    * Publish data to topics.
    * Producers determine which partition to send each record.
        * i.e., Publish to topic based on round robin, or data element within the record.
        * If data must be ordered, it must be on the same partition.

* Message Delivery Semantics
    * Message commitment.
        * Kafka supports an idempotent message delivery option (EoS == Exactly Once Semantics).
            * The producer attaches an identifier to each message.
            * Kafka guarantees the message is only committed to the log once.
        * Producers can publish messages with two delivery semantics
            * Wait for delivery commit confirmation (10 ms)
            * Send completely async, only wait until the leader has the message.
    * Kafka guarantees a committed message will not be lost as long as there is one in-sync replica at all times.
        * Committed message == all ISRs have applied the message to the log.


* Replication
    * The partition is the unit of replication.
    * Each partition has a leader. All read / writes go to the leader.
    * When partitions > brokers (typical case), leaders are distributed among brokers.

* Log compaction
    * Simple log compaction happens by date. All data occurring before a fixed date (say 20 days) is deleted.
    * A better compaction is key based. The last log message for each key is retained.

* Quotas
    * Prevent DDoS.
    * Network based quotas - byte rate thresholds.
    * Request reate quotas - CPU utilization of network and I/O threads.
    * Quotas are enabled per client group, per server (broker).
    * The server (broker) sliently slows down the client by delaying responses.
        * By slowing down the response, the client does not need to implement backoff strategies.


## Implementation

* Log
    * For a topic `my_log`
        * Each partition has a unique directory. i.e., `my_log_0` for partition `0`, etc.
        * Each message's `id` is the byte offset of all messages ever sent to the topic.
        * Each file is named with the starting message `id` (byte offset).
    * A message's unique identifier is:
        * Node id + partition id + message id.
    * Note that the `message id` (byte offset) is hidden from the consumer.

* Consumer Offset Tracking
    * Consumer groups use a designated server called the `offset manager` to store offsets.
    * High level consumers should handle this automatically.
    * Previous versions of kafka used zookeeper to store offsets. That has been deprecated.
    * To find your consumer group offset manager, `GroupCoordinatorRequest` to look up it's offset manager.
    * Offsets are kept by partition.

## Operations

* Determine replication factor.
    * Replications are evenly distributed across servers.
    * Definitely more than 1. That will allow you to roll servers.
    * Each partition is stored with the format `topic_name-[partition]` i.e., `damon-0` for partition 0 of `damon` topic.

* Determine partition count.
    * Each partition must fit completely on a single machine.
    * Partitions determine the degree of parallelism.
        * The maximum number of concurrent consumers cannot be greater than the partition size.
    * You can repartition after creation.
        * Existing data does not move. Therefore partitions relying on hashes may not work.
        * Warning : if a topic that has a key, ordering will be affected.
        * You cannot reduce the number of partitions for a tpoic. (future plans perhaps)


## Brokers

```
//
// Do *not* allow topics to be auto-created.
// Prevents configuration bugs, ability to track topic creation and ownership.
//
auto.create.topics.enable=false

//
// Allows topics to be removed, not just marked deleted.
// This allows us to truly free log files.
delete.topic.enable=true
```

## Producer API

* Default partitioning strategy:
    * `Math.abs(Utils.murmur2(keyBytes)) % (numPartitions - 1)) + 1;`

* Schema Registry

```
props.put(KafkaAvroDeserializerConfig.SPECFIC_AVRO_READER_CONFIG, "true");`
props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://schemaregistry1:8081");
```


### Exactly Once Semantics

* Producer send is idempotent.
    * Each batch of messages sent to Kafka contains a sequence number, which is persisted to a log. Therefore, all brokers know if each incoming message has already been received.
    * Set `enable.idempotence=true` on the broker to enable message sequence numbers.
* Producers send records in TXs.
    * Set `transactional.id=[someid]`. This is needed to provide TX continuity across restarts.
* Consumers receive records and commit offsets in the same TX.
    * To read only messages delivered in transactions, set `isolation.level=read_committed`


## Consumer API

* EOS : exactly once semantics : (not in `librdkafka`)
    * Producers : Set `acks=-1` (all). EOS only works with `acks=-1`.
    * Transactional guarantees has been delivered exactly once.
    * Brokers dedupe based on `ProducerId` / `TransactionId`
    * Set `enable.idempotence=true` on brokers.
    * Producer writes atomically across partitions using "transactional" messages.
    * [Exactly Once Semantics in Kafka](https://www.confluent.io/blog/exactly-once-semantics-are-possible-heres-how-apache-kafka-does-it/)


* How can clients track their offset?
    * The client must send / read it's offset to the offset manager.
    * The client should use a `GroupCoordinatorRequest`.
    * "High level" consumers should handle this automatically.

* Update `AdvancedConsumer` to manually commit offsets on each message read (override automated defaults).
* Log each time offsets are committed.

Important configuration

```
//
// The only true required setting.
// The bootstrap server is used *only* for bootstrapping
// they will self identify all brokers in the cluster during bootstrapping.
//
// You typically want > 1 in case you want to bounce a server.
//
bootstrap.servers = localhost:9092,localhost:9093

//
// Client id helps in diagnostics, support.
//
client.id="my-client-id"

//
// Used to store offsets
//
group.id="my-group-id"

//
// Controls how often a consumer sends heartbeats to the server.
// Allows server to better determine when a client is disconnected,
// when a consumer rebalance is needed.
// Default 3s
//
heartbeat.interval.ms=3000

//
// Offset management. Default true
//
enable.auto.commit=true

//
// The auto commit interval. Default 5000
//
auto.commit.interval.ms=5000

//
// The behavior to use when no commit history is found. Default = latest
//
// earliest = the first offset
// none = throw exception on the consumer if no offset is found for the consumer group.
// latest = the latest offset (stream new events going forward)
//
auto.offset.reset=earliest

```

[Confluent - Kafka Consumers](https://docs.confluent.io/current/clients/consumer.html)

* For each consumer group, each partition has a read offset. Each consumer must periodically persist their offsets.
* New consumers, when assigned partitions to read, will start from the persisted offset.



## Streams API


-----

# Kafka Quickstart

```

# Important directories

## Binary shell wrappers
/usr/local/bin/kafka-*

## Configuration Files
/usr/local/etc/kafka

## Main Kafka directory
/usr/local/Cellar/kafka/0.11.0.1

## Logs directory
/usr/local/var/lib/kafka-logs


# Start zookeeper
$ zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties

# Start kafka

## Broker id=100 port=9092
$ kafka-server-start /usr/local/etc/kafka/server.100.properties

## Broker id=101 port=9093
$ kafka-server-start /usr/local/etc/kafka/server.101.properties

# List topics
$ kafka-topics --list --zookeeper localhost:2181

# Create a topic
$ kafka-topics --create --zookeeper localhost:2181 --if-not-exists --replication-factor 2 --partitions 1 --topic test

# Describe a topic (replication factor)
$ kafka-topics --zookeeper localhost:2181 --describe --topic com.damonallison.test

# Delete
$ kafka-topics --zookeeper localhost:2818 --delete --topic com.damonallison.test


# Start a console producer
$ kafka-console-producer --broker-list localhost:9092 --topic test

# Start a console consumer
$ kafka-console-consumer --broker-list localhost:9092 --topic test

# List Consumer Groups
$ kafka-consumer-groups --bootstrap-server localhost:9092 --list

# An example Kafka Connect file connector.
# Reads from a source file (test.txt) writes to a sink (listener) file ()
$ connect-standalone connect-standalone.properties connect-file-source.properties connect-file-sink.properties

```

## Confluent Recommendations

* Only keep 3-5 zookeeper nodes around. More than that makes things too chatty.
* Never start with less than 3 brokers in prod.
* Keep zookeeper nodes off of broker nodes.
* If you have multiple producers, ensure the producers are publishing messages for different keys.
    * Ordering problems will occur if multiple producers are producing messages with the same key.
* Use 3 or 4 digit broker numbers.
    * Makes it easier to read the logs, distinguish between partitions of `0` and `1`.
* Get off `librdkafka` (.NET, etc). It's always going to be second class.
* Ensure all brokers use the same configuration file (with the exception of ids, of course).
