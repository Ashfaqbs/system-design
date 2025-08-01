# Apache Cassandra: A Distributed Systems Perspective

## 1. Foundations

### 1.1 Overview of Apache Cassandra

Apache Cassandra is a distributed, wide-column NoSQL database designed for high availability, linear scalability, and fault tolerance. In the context of NoSQL, a wide-column store organizes data into rows and dynamic columns grouped into column families (similar to tables), enabling denormalized and efficient read/write paths. For example, an event log might store user actions with different column sets per user. Originating from Facebook and later open-sourced, Cassandra implements a decentralized peer-to-peer architecture, where all nodes are equal (no master), and each can handle read and write requests independently. This model eliminates single points of failure and enables fault-tolerant data replication.

Cassandra is optimized for write-intensive workloads because it uses append-only writes to a commit log and in-memory memtables. These are later flushed to immutable SSTables, avoiding random disk I/O. This model supports high-throughput ingestion, such as time-series, telemetry, or log data.

### 1.2 MongoDB Summary and Contrast

MongoDB is a document-oriented NoSQL database that stores data in flexible, JSON-like BSON documents. It supports dynamic schemas and allows rich, nested data models. MongoDB emphasizes developer productivity and ease of querying, including support for ad-hoc queries and indexing.

#### 1.3 Cassandra vs MongoDB – Key Differences

* **Storage Model**: Cassandra uses a wide-column format with rows organized into partitions and sorted by clustering keys. MongoDB uses a document-based model storing data as BSON documents.
* **Query Model**: Cassandra supports a limited set of query patterns tied closely to the data model and requires prior schema planning. MongoDB provides a rich query language with ad-hoc capabilities.
* **Consistency Guarantees**: Cassandra offers tunable consistency per operation (ranging from eventual to strong consistency). MongoDB defaults to strong consistency within a replica set.
* **Deployment Patterns**: Cassandra uses a decentralized architecture with no single point of failure. MongoDB traditionally uses a primary-secondary architecture with optional sharding.

---

## 2. Cassandra Architecture

### 2.1 Cluster Hierarchy

* **Cluster**: Logical grouping of all nodes managing a dataset. Each cluster manages a distributed and replicated dataset.
* **Data Center**: Logical grouping of nodes, often mapped to a geographic or cloud region, allowing for optimized data locality and consistency.
* **Node**: A single machine that stores a subset of the data. Each node is symmetric in function.

### 2.2 Data Distribution

* **Partition Key**: The key that determines the token value for a row. All rows with the same partition key are stored together. It is used to calculate the token using a hash function.
* **Token Ranges**: The full hash space is divided into contiguous token ranges, each assigned to nodes. For example, if the hash space is from 0 to 2^127, each node holds a fraction of this range.
* **Consistent Hashing**: Enables even data distribution and supports cluster scaling with minimal data shuffling. Adding a node results in token reassignment for a fraction of the data.

### 2.3 Replication and Consistency

* **Replication Factor (RF)**: The number of nodes that will receive a copy of each partition. For RF=3, each partition is stored on three different nodes.

Replication ensures fault tolerance for both read and write operations. For instance, if one node is down, another replica can fulfill the request.

* **Consistency Levels**: Define how many replica nodes must acknowledge an operation:

  * `ONE`, `TWO`, `THREE`: Requires acknowledgment from 1, 2, or 3 nodes respectively.
  * `QUORUM`: Requires acknowledgment from a majority of replicas (`⌈(RF / 2) + 1⌉`).
  * `ALL`: Requires acknowledgment from all replicas.
  * `LOCAL_QUORUM`, `EACH_QUORUM`: Confine quorum to the local or every data center in multi-DC setups.

Consistency is required because data across nodes may temporarily diverge. Each node stores only a portion of the data and may be temporarily unavailable. A write at lower consistency might not reach all replicas immediately, causing temporary inconsistency.

---


### 2.4 Supporting Mechanisms

* **Gossip Protocol**: Peer-to-peer metadata exchange about node health.
* **Hinted Handoff**: Temporary storage of writes for unavailable replicas.
* **Read Repair**: Background mechanism for replica synchronization.
* **Memtable**: In-memory write buffer.
* **SSTable**: Immutable on-disk storage files.
* **Compaction**: Merges SSTables, removing tombstones and duplicates.
* **Bloom Filters**: Probabilistic data structures to avoid unnecessary disk reads.

---

## 3. Eventual Consistency

### 3.1 Definition

Eventual consistency guarantees that, in the absence of new updates, all replicas will converge to the same state over time.

### 3.2 Motivation in Cassandra

* Maximizes availability and partition tolerance.
* Allows for flexible replication across geographic regions.
* Enables high write throughput by decoupling availability from immediate consistency.

### 3.3 Manifestation in CRUD

| Operation | Eventual Consistency Manifestation                        | Affected by Consistency Level |
| --------- | --------------------------------------------------------- | ----------------------------- |
| Create    | Writes can succeed with partial acknowledgment            | Yes                           |
| Read      | May return stale data if low consistency is chosen        | Yes                           |
| Update    | Behaves like Create with potential for stale replicas     | Yes                           |
| Delete    | Implemented via tombstones; eventual purge via compaction | Yes                           |

---

## 4. CRUD Walkthrough (Cluster → Node)

### 4.1 Create/Insert

* **Coordinator Node**: Receives client request.
* **Token Routing**: Uses partition key to determine replica nodes.
* **Commit Log**: Durable append for recovery.
* **Memtable**: In-memory write buffer updated.
* **Hinted Handoff**: Hints stored if replicas are down.

### 4.2 Read

* **Coordinator**: Handles query and chooses replicas based on consistency level.
* **Digest Read**: Light-weight hash comparison of values.
* **Speculative Read**: Optional retry from additional replicas if latency is high.
* **Read Repair**: Synchronizes inconsistent replicas asynchronously.

### 4.3 Update

* Behaves like insert; no in-place update.
* **Compare-and-Set**: Achieved using lightweight transactions (Paxos).
* **Tombstone**: Markers used to represent deleted values for conflict resolution.

### 4.4 Delete

* Deletes write a tombstone.
* **Anti-Entropy Repair**: Synchronizes tombstones across nodes.
* **gc\_grace\_seconds**: Determines when tombstones are purged during compaction.

### 4.5 Quorum in Multi-DC

* Calculated per data center using `LOCAL_QUORUM`.
* `QUORUM` spans all replicas in the cluster, which may impact latency across regions.

---

## 5. Advanced and Must-Know Concepts

### 5.1 Lightweight Transactions

* Paxos-based consensus mechanism.
* Guarantees linearizability for conditional updates.

### 5.2 Materialized Views

* Denormalized secondary representations automatically updated.
* Subject to known consistency limitations.

### 5.3 Secondary Indexes

* Global or local to a node; less performant on high-cardinality columns.

### 5.4 TTLs (Time-to-Live)

* Expiry timestamp set on a per-cell basis.
* Auto-expiry without manual deletes.

### 5.5 Shard (Token) Distribution

* Poor token distribution leads to hot partitions.
* Balanced token assignment is essential for capacity planning.

### 5.6 Schema Design Principles

* Based on query patterns, not normalization.
* One table per access pattern is often recommended.

---

## 6. Conclusion / Cheat-Sheet

### 6.1 Cassandra vs MongoDB Summary

| Feature            | Cassandra        | MongoDB                  |
| ------------------ | ---------------- | ------------------------ |
| Storage Model      | Wide-column      | Document (BSON)          |
| Architecture       | Peer-to-peer     | Primary-secondary        |
| Consistency        | Tunable          | Strong (per replica set) |
| Schema Flexibility | Static per table | Dynamic                  |
| Query Language     | CQL              | MongoDB Query Language   |

### 6.2 Consistency Levels in Cassandra

| Level         | Description                       |
| ------------- | --------------------------------- |
| ONE           | One replica must respond          |
| QUORUM        | Majority of replicas must respond |
| ALL           | All replicas must respond         |
| LOCAL\_QUORUM | Majority of local DC replicas     |
| EACH\_QUORUM  | Quorum in each data center        |

### 6.3 CRUD Guarantees in Cassandra

| Operation | Behavior                         | Tunable Consistency | Final Consistency Type |
| --------- | -------------------------------- | ------------------- | ---------------------- |
| Create    | Append-only with replication     | Yes                 | Eventual or Strong     |
| Read      | Replica-based digest comparison  | Yes                 | Eventual or Strong     |
| Update    | Upsert with potential tombstones | Yes                 | Eventual or Strong     |
| Delete    | Tombstone write and purge later  | Yes                 | Eventual               |
