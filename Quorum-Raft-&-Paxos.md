# Quorum, Raft & Paxos

---

## 1) Initial Understanding

* **Quorum (minimum agreement):** A decision is valid only after **enough nodes say “yes.”** Often an odd number of voters is used to avoid ties.
* **Who votes:**

  * **Kafka, Flink (with ZooKeeper):** the **ZooKeeper ensemble** votes.
  * **Kafka (KRaft / ZooKeeper‑less):** the **Kafka controller quorum (brokers)** vote using **Raft**.
  * **Cassandra & MongoDB:** the **replica nodes** that store the data vote/acknowledge.
* **Where quorum applies (beyond leader election):** confirming writes/reads, topic/partition metadata changes, checkpoint validation, configuration changes, failover.
* **Replication model independence:** works the same whether replicas are **peer‑to‑peer** or **dedicated followers**.
* **Consistency knobs:**

  * **Strong/immediate consistency:** require **ALL/majority** acknowledgements before success → slower but up‑to‑date.
  * **Eventual consistency:** allow **ONE/few** acks → fast; **other replicas catch up automatically** in the background.

Analogy (simple): quorum is like a committee vote; Raft is the written procedure that runs the vote and records the minutes so everyone’s notebook (log) matches.

---

## 2) Quorum — the core idea

**Definition:** Quorum is the **minimum number of nodes that must agree** for an action to be considered committed.

**Why odd sizes?** To avoid ties and maximize tolerance for failures. With (N) voters, a majority is (\lfloor N/2 \rfloor + 1).

**Common uses:**

* **Leader election** (choose a coordinator/controller).
* **Write/Read confirmation** (databases with replication).
* **Metadata/config changes** (e.g., topic creation, partition moves).
* **Checkpointing/state validation** (stream processing).

**Read/Write quorum rule of thumb:** for data correctness with replication factor (N), pick **write quorum** (W) and **read quorum** (R) such that **(R + W > N)**. (Ensures reads overlap with the latest committed write.)

---

## 3) Who actually “agrees” in each system

### A) Apache Kafka

**With ZooKeeper (legacy):** ZooKeeper’s **Zab** protocol keeps the ensemble consistent; the **ZK servers** form the quorum and elect a ZK leader. Kafka brokers rely on ZK for controller election and metadata storage.

**Without ZooKeeper (KRaft):** Kafka embeds a **Raft‑based controller quorum** among brokers. The brokers themselves elect an **active controller** and replicate a **metadata log** via Raft.

**What the voters are:**

* ZooKeeper era → **ZooKeeper servers** vote.
* KRaft era → **Controller quorum brokers** vote.

### B) Apache Flink

* In **HA mode with ZooKeeper**, the **ZooKeeper ensemble** votes to elect the **JobManager** leader; checkpoints and recovery use the agreed state.
* In **K8s-native HA**, Kubernetes/etcd (which uses Raft) provides coordination; effectively the **etcd cluster members** are the voters.

### C) Cassandra (distributed database)

* **Replica nodes** acknowledge reads/writes based on a chosen **consistency level** (e.g., ONE, QUORUM, ALL). The voters are the **replicas holding the data** for the partition.

### D) MongoDB (replica sets)

* **Replica set members** vote to elect a **primary** and acknowledge writes according to **writeConcern** (e.g., `majority`). The voters are the **replica set nodes**.

---

## 4) Consistency models & replication

* **Strong (immediate) consistency:** wait for **ALL/majority** of replicas → **slower, but up‑to‑date**.
* **Eventual consistency:** accept **ONE/few** acks → **fast**; **lagging replicas catch up automatically** (async replication).
* **Independent of replication topology:** quorum logic is the same for **peer‑to‑peer** or **leader/follower** replication.

---

## 5) Kafka without ZooKeeper (KRaft) — how Raft works in practice

**Cluster example:** 4 brokers, 1 topic, 2 partitions.

### Startup & election

1. **Controller quorum forms** among designated brokers.
2. Nodes start as **followers**; if they don’t hear heartbeats, a node becomes a **candidate** and starts an **election**.
3. The node that wins **majority votes** becomes the **active controller (leader)**.

### Leader (controller) responsibilities — what the leader can do

* **Maintain the metadata log** (authoritative history of cluster metadata).
* **Assign partition leaders & replicas** across brokers.
* **Track ISR (in‑sync replicas)** and handle replica promotion/demotion.
* **Process metadata changes:** topic create/delete, partition count changes, config updates.
* **Broker lifecycle:** handle broker registration, controlled shutdown, fencing of stale brokers.
* **Coordinate reassignments & recovery:** move partitions, rebalance, publish snapshots of metadata for fast catch‑up.

*(Analogy: the controller is the project coordinator keeping the master spreadsheet; Raft ensures every coordinator candidate uses the same spreadsheet and update order.)*

### Log replication (Raft)

* The leader appends a **new metadata entry** and sends **AppendEntries** heartbeats to followers.
* When a **majority** have the entry, it’s **committed** and becomes visible/active.

### Failure & recovery scenarios

* **Leader dies (4 → 3 brokers):** remaining controller quorum holds a **new election**; one follower becomes leader.
* **Only 2 brokers remain in a 4‑node quorum:** **no majority**; controller leadership **cannot be elected**. Metadata changes pause until a quorum returns. (Data serving by existing partition leaders may continue, but **no new metadata decisions**.)
* **Failed broker returns:** it **rejoins as a follower**, **catches up** by replaying the **metadata log**, and does **not preempt** the current leader unless a new election later occurs.

---

## 6) Flink — where quorum shows up

* **JobManager leader election:** decided by **ZooKeeper/etcd quorum**.
* **Checkpoint validation:** a checkpoint is **considered valid** only when the required acknowledgements are recorded (guards against partial/old state).
* **Recovery:** on JobManager failover, the new leader **restores from the last successful checkpoint/savepoint** and resumes tasks.

---

## 7) Cassandra — quorum for reads/writes

* **Write path:** client chooses consistency level → e.g., `QUORUM` requires **majority of replicas** for that partition to ack before success.
* **Read path:** read from enough replicas (often `QUORUM`) and **reconcile** to ensure the freshest value.
* **Eventual catch‑up:** slower replicas **sync via background repair**; you can tune read repair/anti‑entropy processes.

---

## 8) MongoDB — replica set quorum

* **Election:** members vote to choose a **primary**.
* **Writes:** with `writeConcern: "majority"`, a write is successful only after a **majority** ack.
* **Reads:** `readConcern: "majority"` ensures reading data that’s been majority‑committed.
* **Eventual catch‑up:** secondaries replicate the oplog and **catch up automatically**.

---

## 9) Edge questions answered

* **“With only two brokers and one fails, who ‘waits’ for quorum?”**

  * No central poller. Each node runs **Raft timers** (heartbeat/election timeouts). Without a majority, elections **cannot succeed**, so leadership **stalls** until another node returns.

* **“If only one replica acked my write, do others fetch later?”**

  * Yes. Others **pull or receive** the updates via the replication stream and **converge** (eventual consistency).

* **“Does quorum depend on replication type?”**

  * No. It’s a **policy** about **how many** must agree, independent of how replicas are connected.

---

## 10) Raft vs. Paxos — quick compare

* **Goal:** both achieve **consensus** (agree on a single ordered log of decisions) despite failures.
* **Paxos:** roles (proposer/acceptor/learner), phased messaging; **powerful but harder** to implement.
* **Raft:** roles (leader/follower/candidate), clear **leader‑based log replication**, **easier to reason about**.

Key Raft mechanics (plain words):

* **Randomized election timeouts** prevent tie elections.
* **Heartbeats (AppendEntries)** keep followers in sync.
* **Commit index** marks the highest log entry known to be committed.
* **Snapshots** compact old log history for fast recovery.

---

## 11) Practical knobs — “important Raft configs” (generic)

Use these to tune safety vs. latency:

* **Cluster size (N):** choose odd sizes; majority = (\lfloor N/2 \rfloor + 1).
* **Election timeout:** randomized range; too low → flapping; too high → slow failover.
* **Heartbeat interval:** leader’s AppendEntries cadence; smaller = faster detection, higher overhead.
* **Replication batch/pipe:** bytes or entries per AppendEntries; more batching = throughput, more latency.
* **Snapshot/compaction policy:** when to snapshot logs; impacts recovery time and storage.
* **Max in‑flight appends:** controls backpressure to slow followers.
* **Read path mode:** linearizable reads (via quorum/read‑index) vs. lease/leader reads (lower latency).
* **Pre‑vote toggle (if supported):** reduces disruptive elections on partitioned/isolated nodes.
* **Membership change (joint consensus):** safe add/remove voters without losing availability.
* **Commit ack policy:** when clients are told “success” (on leader append vs. on majority commit).

---

## 12) Quick reference — when to prefer what

* **Need simple mental model & implementation:** **Raft**.
* **Interoperate with systems already using Zab/ZK:** **ZooKeeper** (legacy stacks).
* **Database client‑side tunables for latency vs. safety:** **Replica quorum levels** (Cassandra/MongoDB).

---

### One‑liners (glossary)

* **Quorum:** minimum number of “yes” votes for a safe decision.
* **Leader election:** process to pick a coordinator node.
* **Log replication:** making each node’s ordered record of changes identical.
* **Checkpoint:** point‑in‑time snapshot of running state for fast recovery.
