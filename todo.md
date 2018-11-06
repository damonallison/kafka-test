# TODO

## Immediate

* Kafka GUI
  * [Kafka Manager](https://github.com/yahoo/kafka-manager)

## Zookeeper

* How does zookeeper work?
* Why did Kafka use zookeeper as opposed to write their own resource manager?

* Review broker configuration.
  * Prohibit auto topic creation.
    * Default partition count / replication factor.

* Review producer delivery semantics
  * EOS (Acks == all)
  *

* Write a producer / consumer which guarantees EOS.
    * Avro messaging.
    * Consumer : the ability to `reset` via console command.

* How to prevent bad messages from being posted to a topic?
* How to version an Avro message?
* Setup a schema registry. Determine how to version schemas.

## Kafka

* How to require messages written to a topic conform to a JSON / AVRO schema?

* Write a Kafka backed "drone".
  * Able to scale up/down on demand.
	* Ensures consumer group naming uniqueness.
* Infrastructure topology strategy?
	* How many partitions per broker?
	* Can a partitions move between brokers?
* Naming conventions?
	* Topics
	* Consumer groups

* Producers
	* Producers control which partition to assign each record. Round robin or by key.

* Partitions
	* Think thru your partitioning strategy early. If you partition by key, you can't change your partitioning strategy and guarantee ordering by key.
	* Partitions are the degree of parallelism. You can't have more consumers per group than the number of partitions.
