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
Here is an extended section covering **Master-Slave (Leader-Follower) architecture** usage in **Kubernetes, Apache Pulsar, and Apache Flink**, following documentation style:

---



## Master-Slave / Leader-Follower Architecture in Modern Systems

### Kubernetes (K8s)

**Architecture Usage:**
Kubernetes uses a **leader-election mechanism** primarily within its control plane components, especially among **kube-controller-manager** instances.

| Component                 | Role of Leader/Follower                                                     |
| ------------------------- | --------------------------------------------------------------------------- |
| `kube-controller-manager` | Multiple replicas run, but only one is active (leader) at a time.           |
| `etcd`                    | Backend key-value store using **Raft** for leader election and consistency. |

**Election Mechanism:**

* Kubernetes uses a **lease-based election** via API server annotations (`configmaps` or `endpoints`) or etcd.
* Only the elected controller-manager performs operations like node management, deployment rollouts, etc.
* `etcd` uses the **Raft consensus algorithm** to elect a leader for consistent data access across cluster members.

**Purpose Solved:**

* Prevents split-brain in control logic.
* Ensures only one instance manages critical cluster state updates.

---

### Apache Pulsar

**Architecture Usage:**
Pulsar uses a **multi-layered architecture** with leader-follower patterns in its components:

| Component      | Role of Leader/Follower                                                             |
| -------------- | ----------------------------------------------------------------------------------- |
| **BookKeeper** | Handles storage; uses `Bookie` nodes with optional leader logic.                    |
| **ZooKeeper**  | Coordinates Pulsar clusters, manages leader election.                               |
| **Brokers**    | Stateless, but ZooKeeper assigns **topic ownership** to brokers (leader per topic). |

**Election Mechanism:**

* **ZooKeeper** elects leaders among **brokers** to assign partitions and topics.
* **BookKeeper** doesn’t strictly use master-slave, but data is written to a **quorum** of bookies and read from a subset.
* **Segment Ownership** behaves like partition-level leadership.

**Purpose Solved:**

* Ensures high availability of topic ownership.
* Supports automatic rebalancing and failover of topic assignment.

---

### Apache Flink

**Architecture Usage:**
Flink’s architecture includes a **JobManager (master)** and **TaskManagers (slaves)**.

| Component       | Role                                                             |
| --------------- | ---------------------------------------------------------------- |
| **JobManager**  | Acts as master, handles scheduling, checkpointing, coordination. |
| **TaskManager** | Executes tasks assigned by JobManager, acts like slaves.         |

**High Availability:**

* Multiple JobManagers can run in **HA mode** High Availability mode.
* **ZooKeeper (or Kubernetes API)** coordinates leader election.
* Only one JobManager is active at a time; others are passive standby.

**Election Mechanism:**

* JobManager leader is elected via ZooKeeper.
* On failure, a standby JobManager is promoted.

**Purpose Solved:**

* Prevents single point of failure for job orchestration.
* Ensures continuity in long-running data pipelines or streaming jobs.

---

## Summary: Modern Systems with Leader Election

| System            | Uses Master-Slave / Leader-Follower? | Election Type        | Purpose Solved                         |
| ----------------- | ------------------------------------ | -------------------- | -------------------------------------- |
| **Kubernetes**    | Yes (control plane components)       | Lease/ZooKeeper/etcd | Ensures control-plane consistency      |
| **Apache Pulsar** | Yes (Brokers, BookKeeper, ZooKeeper) | ZooKeeper            | Manages topic ownership & availability |
| **Apache Flink**  | Yes (JobManagers & TaskManagers)     | ZooKeeper/K8s API    | Ensures failover for job scheduling    |

---