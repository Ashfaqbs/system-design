The canvas tool is currently unavailable, so the latest content has been prepared here instead:

---

## Master-Slave Architecture: Core Concepts

### Layman’s Explanation

Imagine a classroom where one teacher (master) writes notes on the board and multiple students (slaves) copy the notes. If the teacher leaves, one of the best students is chosen to take over and continue teaching. This ensures note-taking (data replication) continues smoothly without loss.

This mirrors how master-slave systems work: one central entity handles updates, and others copy the updates. If the central entity fails, one of the followers is promoted to take over.

---

### Technical Explanation

Master-Slave architecture (also called Primary-Replica or Leader-Follower) is a distributed system pattern where:

* The **master node** (leader/primary) handles all **write operations** and pushes updates to the replicas.
* The **slave nodes** (followers/replicas) are read-only and synchronize their data from the master.
* If the master node fails, a leader election is triggered to promote one of the slaves to become the new master.

This design supports **read scalability**, **fault tolerance**, and **data redundancy** in distributed systems.

---

### Core Algorithms: Leader Election

#### 1. Bully Algorithm

**Layman View**: The node with the highest ID or rank takes over if the leader is missing. Nodes with lower rank notify higher ones and wait for a response. If none respond, the initiator becomes the leader.

**Technical View**:

* Each node knows all other node IDs.
* If a node suspects the leader is down, it sends election messages to higher-ID nodes.
* If no higher-ID node responds, it declares itself the new leader.
* Otherwise, it waits for the leader declaration from a higher node.

#### 2. Raft Consensus Algorithm

**Layman View**: Like a democratic vote. All nodes vote, and a majority decision elects the new leader.

**Technical View**:

* Nodes start in a follower state.
* If no heartbeat is received, a node becomes a candidate and requests votes.
* If it receives a majority, it becomes the leader.
* Leader sends regular heartbeats to maintain authority.

---

### Types of Master-Slave Setups

| Type                         | Description                                                          |
| ---------------------------- | -------------------------------------------------------------------- |
| **Static Master-Slave**      | Master is fixed, changes require manual intervention.                |
| **Dynamic with Election**    | Master is automatically chosen based on priority or votes.           |
| **Synchronous Replication**  | Writes are acknowledged only after all replicas confirm replication. |
| **Asynchronous Replication** | Master acknowledges writes immediately; replicas catch up later.     |

---

### Roles

| Node Role | Responsibility                                                                 |
| --------- | ------------------------------------------------------------------------------ |
| Master    | Processes all write requests, coordinates replication, manages data integrity. |
| Slave     | Processes read requests, continuously replicates data from master.             |

---

### Problems Solved

* Reduces load on a single node by distributing reads.
* Provides high availability by failing over to replicas.
* Improves fault tolerance and data reliability.

---

### Use in Real Systems

| System               | Role of Master-Slave Architecture                                    |
| -------------------- | -------------------------------------------------------------------- |
| **MySQL/PostgreSQL** | Master-slave replication for read scaling and failover.              |
| **Kafka**            | Partition leaders act as masters; followers replicate the partition. |
| **MongoDB**          | Replica sets use a primary (master) with secondaries (slaves).       |
| **Redis**            | Redis Sentinel enables automatic failover in a master-slave cluster. |

---

### Failover and Recovery

* **Heartbeat detection** monitors the master.
* On failure, **election** starts using predefined logic (Raft, Bully, etc.).
* Once a slave is promoted, others adjust roles and resume replication.

---

### Considerations

* **Consistency trade-offs** between sync and async replication.
* **Split-brain scenarios** must be managed using quorum-based systems like Zookeeper or etcd.
* **Lag monitoring** is critical to ensure slaves are up-to-date.

---
