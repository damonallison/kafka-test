# Kafka Commands

## Installation

* `brew install zookeeper`
* `brew install kafka`

## Environment

```bash

# Zookeeper
/user/local/etc/kafka/
## Binary shell wrappers
/usr/local/bin/kafka-*

## Configuration Files
/usr/local/etc/kafka

## Main Kafka directory
/usr/local/Cellar/kafka/[version]

## Zooper data directory (where the snapshot is stored)
/usr/local/var/lib/zookeeper

## Kafka data (log) directory (where topics are stored)
/usr/local/var/lib/kafka-logs
```

## Launching

```bash
# Start zookeeper
$ zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties

# Start kafka

## Broker id=100 port=9092
$ kafka-server-start /usr/local/etc/kafka/server.100.properties

## Broker id=101 port=9093
$ kafka-server-start /usr/local/etc/kafka/server.101.properties
```

## Kafka

```bash

# List topics
$ kafka-topics --list --zookeeper localhost:2181

# Create a topic
$ kafka-topics --create --zookeeper localhost:2181 --if-not-exists --replication-factor 2 --partitions 1 --topic test

# Describe a topic (replication factor)
$ kafka-topics --zookeeper localhost:2181 --describe --topic com.damonallison.test

# Delete
$ kafka-topics --zookeeper localhost:2818 --delete --topic com.damonallison.test
```

## Simple Producer / Consumer

```bash
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