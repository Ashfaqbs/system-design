# MongoDB: A Distributed Systems Architecture Brief

## 1. Foundations

MongoDB is a distributed NoSQL document database designed for high developer productivity, scalability, and flexible schema modeling. It stores data in BSON (Binary JSON) documents, allowing nested and hierarchical data structures. MongoDB is well-suited for applications that demand rapid iterations and complex object modeling without the constraints of rigid schemas.

---

## 2. MongoDB Architecture

### 2.1 Cluster Hierarchy

* **Cluster**: Logical grouping of servers forming a MongoDB deployment.
* **Sharded Cluster**: Consists of multiple shards, each a replica set, enabling horizontal scalability.
* **Replica Set**: A group of `mongod` instances that maintain the same data set, ensuring redundancy and high availability.

### 2.2 Core Components

* **mongod**: Primary database process managing data, requests, and replication.
* **mongos**: Query router in a sharded cluster.
* **Config Servers**: Maintain metadata and configuration for sharded clusters.

### 2.3 Data Model

* **Database** → **Collection** → **Document**
* Documents are BSON-encoded JSON objects.
* Collections are schema-flexible and can contain documents with varied structures.

---

## 3. Sharding and Data Distribution

### 3.1 Shard Keys

* A shard key determines the partitioning strategy across shards.
* Choice of shard key affects data distribution, query targeting, and performance.

### 3.2 Range-Based vs Hash-Based Sharding

* **Range-Based**: Shard key values are partitioned into contiguous chunks.
* **Hash-Based**: Shard key values are hashed, distributing data evenly.

### 3.3 Chunk Migration and Balancing

* The balancer redistributes chunks across shards to maintain balanced data distribution.
* Chunk migrations are automatic and occur without downtime.

---

## 4. Replication and High Availability

### 4.1 Replica Set Components

* **Primary**: Receives all write operations.
* **Secondaries**: Replicate operations from the primary’s oplog.
* **Arbiters**: Participate in elections but do not store data.

### 4.2 Write Concern

* Configurable per operation to control acknowledgment level:

  * `w: 1` – Acknowledged by the primary.
  * `w: majority` – Acknowledged by majority of replica set members.
  * `w: all` – Acknowledged by all nodes.

### 4.3 Read Preference

* Determines from which replica a read operation is served:

  * `primary`, `primaryPreferred`, `secondary`, `secondaryPreferred`, `nearest`

---

## 5. CRUD Operation Path

### 5.1 Insert

* Client sends document to primary node.
* Document is written to the storage engine (WiredTiger).
* Operation is logged to the oplog for replication.

### 5.2 Read

* Routed based on read preference.
* Query planner chooses optimal index.
* In-memory cache and index traversal used.
* Projection and sorting applied.

### 5.3 Update

* Document updates are in-place where possible.
* Updates are atomic at the document level.
* If indexed fields are modified, indexes are updated accordingly.

### 5.4 Delete

* Document matching the query is removed.
* If no index is present on query filter, collection scan is performed.
* Deletions are also recorded in the oplog for replication.

---

## 6. Indexing Internals

### 6.1 Index Types

* **B-tree Index**: Default index structure.
* **Hashed Index**: Hash of field values; used in hashed sharding.
* **Compound Index**: On multiple fields; preserves sort order.
* **Wildcard Index**: Indexes dynamic fields across documents.

### 6.2 Index Mechanics

* Indexes are implemented as B-trees (with prefix compression).
* Insert/update triggers index updates.
* Queries utilize indexes based on selectivity and query shape.
* Index intersections and covered queries optimize execution.

---

## 7. Transactions and Consistency

### 7.1 Write Durability

* Controlled via `writeConcern` and journaling.
* Journaling ensures durability across crashes.

### 7.2 ACID Transactions

* Multi-document ACID transactions supported within replica sets (and across shards since v4.2).
* Two-phase commit protocol ensures atomicity.

### 7.3 Consistency Model

* Strong consistency on primary reads.
* Causal consistency and snapshot reads available for secondary reads using sessions.

---

## 8. Internal Optimizations

### 8.1 WiredTiger Storage Engine

* Document-level concurrency control.
* Compression (snappy/zlib/zstd) to reduce storage.
* Memory-mapped data files for fast access.

### 8.2 Query Planner and Execution Engine

* Multiple query plans generated and scored.
* Caching of query plans for repeated execution.

### 8.3 Aggregation Pipeline

* Transforms documents via stages (match, group, sort, project).
* Operates on collections or indexes.

### 8.4 TTL Indexes

* Automatically deletes documents after a specified time.
* Efficient for time-series or ephemeral data.

---

## 9. Best Practices

* Choose shard keys based on query access patterns and cardinality.
* Avoid unbounded document growth to prevent fragmentation.
* Use compound indexes for multi-field filtering and sorting.
* Prefer `w: majority` and `readPreference: primary` for strong consistency.
* Employ schema validation for consistency without losing flexibility.

---

## 10. Summary Table

| Feature        | Details                                                  |
| -------------- | -------------------------------------------------------- |
| Data Model     | BSON Documents                                           |
| Sharding       | Hash/Range-based, user-defined shard key                 |
| Replication    | Replica sets with automatic failover                     |
| Transactions   | Multi-document, distributed ACID transactions            |
| Indexing       | B-tree (default), compound, wildcard, hashed             |
| Storage Engine | WiredTiger (concurrent, compressed, memory-mapped)       |
| Query Model    | Ad-hoc, aggregation pipelines, secondary reads supported |
| Consistency    | Tunable via writeConcern and readPreference              |
| Durability     | Journaling and acknowledgment levels                     |
