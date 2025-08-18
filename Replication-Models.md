# Replication in Distributed Systems

---

## 1. What is Replication?

Replication means keeping **multiple synchronized (or partially synchronized) copies of data across nodes**. These nodes may be **physical servers, VMs, or containers** deployed across datacenters.

*Analogy*: A rare book is copied across multiple libraries. If one library burns down, the book survives elsewhere.

Replication ensures:

* **Availability** (service stays up).
* **Fault tolerance** (survive machine crashes).
* **Performance** (faster access from nearby replicas).

---

## 2. Replication Models

---

### 2.1 Synchronous Replication (Leader–Follower)

**Definition:** Writes are acknowledged only after **leader + all followers** confirm.

**Analogy:** A manager signs a document, and waits until every assistant has copied it before declaring it official.

**Example in Practice (PostgreSQL on VMs):**

* A PostgreSQL **Primary** is running on VM-1 in Data Center A.
* A **Synchronous Replica (Follower)** runs on VM-2 in Data Center B.
* When a client issues a transaction:

  1. Primary writes the data into its WAL (Write-Ahead Log).
  2. WAL is immediately streamed over the network to VM-2.
  3. VM-2 acknowledges once WAL is persisted.
  4. Only then does the primary confirm success to the client.

This ensures zero data loss, but if VM-2 is slow or unavailable, commits on VM-1 block.

**Example in Practice (Apache Flink):**

* Flink jobs maintain state in memory on **TaskManager JVMs**.
* Periodically, the **JobManager** triggers a **checkpoint**.
* Each TaskManager writes its state snapshot to **HDFS/S3 (replicated storage)**.
* Only after all snapshots are persisted, Flink marks the checkpoint “completed.”

**Flow:**

```
Client → Leader (Primary, VM-1) 
            ↘ (sync WAL shipping)
            Replica (VM-2)
Ack returned only after both confirm
```

---

### 2.2 Asynchronous Replication (Leader–Follower)

**Definition:** Writes are acknowledged once the leader logs them; replicas fetch later.

**Analogy:** A teacher writes notes, students copy them later during free time.

**Example in Practice (MySQL Async):**

* MySQL Primary on **Physical Server A**.
* Replica on **VM B**.
* Primary logs updates into its binary log (binlog).
* Replica’s I/O thread continuously pulls binlog entries (long polling).
* Replica SQL thread replays those logs asynchronously.

If the primary crashes before logs are shipped, the replica may miss the last few transactions.

**Example in Practice (MongoDB Replica Set):**

* Primary node (Container A) accepts writes.
* Secondaries (VM B, VM C) **tail the primary’s oplog** asynchronously.
* A client reading from a secondary may see stale data.

**Flow:**

```
Client → Primary (ack immediately) 
          ↘
     Replica-1 (poll binlog / oplog)
          ↘
     Replica-2 (poll binlog / oplog)
```

---

### 2.3 Quorum-based Replication (Peer-to-Peer)

**Definition:** A write is successful when a **majority of replicas** confirm.

**Analogy:** A jury decides by majority, even if some judges are missing.

**Example in Practice (Cassandra):**

* Cluster of 5 Cassandra nodes (VMs across 2 data centers).
* Replication Factor = 3.
* Client writes a record with **Consistency Level = QUORUM**.
* Coordinator node sends the write to 3 replicas.
* If 2 replicas (majority) confirm, the write is acknowledged.

Nodes use **hinted handoff** to catch up lagging replicas, and **gossip protocol** to exchange state.

**Flow:**

```
Client → Coordinator Node
       → Replica-1 (ack)
       → Replica-2 (ack)
       → Replica-3 (pending)
Write confirmed with quorum (2/3)
```

---

### 2.4 Leader–Follower Replication

**Definition:** A single leader handles all writes, followers replicate logs.

**Analogy:** A professor dictates to one scribe (leader), who then shares notes with assistants (followers).

**Example in Practice (Kafka):**

* Topic partition P1 has **Leader Broker** on VM-1.
* Followers on VM-2, VM-3 subscribe to logs.
* Producers always write to leader.
* Followers continuously pull log segments via replication threads.
* Consumers may read from leader or followers (depending on settings).

If leader VM-1 crashes, ZooKeeper (or KRaft in newer Kafka) elects a new leader among in-sync replicas.

**Example in Practice (PostgreSQL Hot Standby):**

* Primary on **Bare Metal Server A**.
* Replica on **VM B** streaming WAL.
* Clients can read from VM B, but writes always go to A.

**Flow:**

```
Producer → Leader Broker (VM-1) 
              ↘
          Follower (VM-2, async copy)
              ↘
          Follower (VM-3, async copy)
```

---

### 2.5 Multi-Leader Replication

**Definition:** Multiple leaders accept writes and replicate to each other.

**Analogy:** Multiple authors write separate chapters, then reconcile conflicts during merging.

**Example in Practice (PostgreSQL BDR):**

* Two PostgreSQL nodes (VM-1 and VM-2).
* Both can accept writes.
* Each streams WAL entries to the other (bi-directional).
* Conflict resolution logic needed if both update the same row.

**Example in Practice (MongoDB Replica Sets with Failover):**

* Normally one Primary + multiple Secondaries.
* If the primary fails, a secondary is elected leader automatically.
* Over time, leadership can shift → effectively **multi-leader across time**.

**Flow:**

```
Client-1 → Leader A (VM-1)
Client-2 → Leader B (VM-2)
Both replicate writes to each other
Conflicts resolved if overlap occurs
```

---

### 2.6 Peer-to-Peer / Gossip Replication

**Definition:** No fixed leader; all peers replicate via gossip or anti-entropy protocols.

**Analogy:** A rumor spreads in a social group until everyone eventually hears it.

**Example in Practice (Cassandra):**

* 6-node cluster spread across multiple racks.
* Any node can accept a write.
* Gossip protocol exchanges membership and state every few seconds.
* Replication factor ensures data is copied across racks/data centers.

**Example in Practice (Dynamo / Riak):**

* Nodes hash keys using consistent hashing.
* Writes are sent to N replicas responsible for that key.
* Vector clocks track version histories for conflict resolution.

**Flow:**

```
Node A ↔ Node B ↔ Node C ↔ Node D
Gossip ensures all eventually sync
```

---

## 3. Technology Case Studies

### PostgreSQL

* **Model:** Leader–Follower (Synchronous or Asynchronous).
* **Mechanism:** WAL shipping over TCP. Replica replays logs.
* **Deployment:** Primary on VM-1 (DC-A), Replica on VM-2 (DC-B).

### MongoDB

* **Model:** Leader–Follower with automatic elections.
* **Mechanism:** Primary writes oplog; secondaries tail and apply. Failover triggers election.
* **Deployment:** Replica Set across containers/VMs.

### Kafka

* **Model:** Leader–Follower per partition.
* **Mechanism:** Leader handles writes; followers pull via replication threads. ISR (In-Sync Replicas) ensure durability.
* **Deployment:** Brokers across physical machines/VMs.

### Cassandra

* **Model:** Peer-to-Peer with quorum.
* **Mechanism:** Any node accepts write, forwards to replicas. Gossip protocol for coordination.
* **Deployment:** Multi-DC clusters, often spanning continents.

### Flink

* **Model:** Synchronous checkpoint replication.
* **Mechanism:** TaskManagers persist state snapshots to DFS; recovery uses last checkpoint.
* **Deployment:** Containers on Kubernetes, checkpoints in HDFS/S3.

---

## 4. Key Takeaways

* **Replication model choice = application trade-off.**
* Strong consistency → Synchronous (Postgres, Flink).
* Eventual consistency → Async / Peer-to-Peer (MongoDB, Cassandra).
* Hybrid (Quorum, Multi-Leader) = balance between consistency and availability.
* Deployment context matters: VMs, physical servers, or containers change **latency, failover speed, and durability guarantees**.

---