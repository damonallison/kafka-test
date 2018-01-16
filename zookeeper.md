# Zookeeper

* ZooKeeper provides coordination services for distributed applications.
* ZooKeeper was written by Yahoo! Research.

## Design goals

* Simple. All services are stored in a hierarchical DB, in memory (for speed).
* Replicated.
  * ZooKeeper is is replicated over a set of hosts called an ensemble.
  * Clients connect to a ZooKeeper server instance. If the TCP connection drops, is connects to a different instance.

## Data Model / Hierarchical Namespaces

Each node in the ZooKeeper namespace can have data associated with it as well as children. Each node is called a znode to make it clear the node is a "ZooKeeper Node".

Each node has an ACL associated with it to provide security / access permissions.

Clients can "watch" a znode and be notified when data changes.

All write requests from clients are forwarded to a single server called the `leader`. The leader updates `follower` servers. If `leader` dies, a `follower` is promoted to the `leader`. The messaging layer takes care of replacing leaders on sync failures.


```
/
  /app1
    /app1/p_1
    /app1/p_2
  /app2
    /app2/p_1
    /app2/p_2
```