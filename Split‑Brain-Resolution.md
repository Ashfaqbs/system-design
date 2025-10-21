# Split‑Brain Resolution


---


## 1) What is split‑brain?

A **split‑brain** happens when a cluster breaks into **isolated subgroups** that cannot exchange messages (a **network partition**) or the **leader becomes unreachable**, and separate groups continue operating as if each were the legitimate authority. The result can be **two leaders**, **conflicting writes**, and **diverging state**.

*Analogy:* Two floors of the same company lose the staircase and phones. Both keep approving budgets independently. When the link returns, the numbers do not match.

**Key properties**

* Temporary **loss of connectivity** is enough; nodes need not crash.
* Either side may be healthy locally yet **inconsistent globally**.
* Progress must be **gated by quorum** or **leadership leases** to prevent damage.

---

## 2) Why it matters

* Prevents **double leadership** and **split writes** that cause data loss on recovery.
* Preserves **data integrity, ordering, and uniqueness** (ids, offsets, counters).
* Reduces **mean time to recovery** by avoiding messy conflict resolution later.
* Enables predictable **SLOs** for availability and correctness.

*Rule:* Prefer **safe stop** over unsafe progress on the minority side.

---

## 3) Where it shows up (use cases)

* **Cluster leadership:** controller/primary election during broker, node, or link failures.
* **Replicated data paths:** concurrent writes accepted by disconnected groups.
* **Schedulers and stream coordinators:** two JobManagers or controllers coordinating the same work.
* **Multi‑AZ / multi‑DC layouts:** inter‑site links flap; each side believes the other is down.
* **Edge deployments:** intermittent links create small, isolated islands.

---

## 4) Canonical example

* 5‑node cluster splits **3 vs. 2**.
* The **3‑node majority** elects a leader and continues committing decisions.
* The **2‑node minority** must **stand down** (no new leadership or writes) until connectivity returns.

**Majority math**

* With (N) voters, majority = (\lfloor N/2 \rfloor + 1).
* Fault tolerance: can survive up to **(f = \lfloor (N-1)/2 \rfloor)** failures without losing the ability to elect.
* Prefer **odd‑sized quorums** to avoid ties and maximize (f).

---

## 5) Resolution patterns (toolbox)

1. **Majority quorum** (voting committee): only the side with **>50%** can elect leaders or commit changes; minority **freezes**.
2. **Leader lease / term/epoch** (season ticket): actions are only valid in the current **term**; old leaders are **fenced** in later terms.
3. **Fencing tokens** (turnstile ticket): storage/workers require a **monotonic token**; stale tokens from old leaders are rejected.
4. **Write admission control:** require acknowledgements from a **quorum of replicas** (e.g., `majority`, `QUORUM`, `min.insync.replicas`) before success.
5. **Witness/arbiter voters:** add a lightweight voter (no data) to break ties in even counts.
6. **Minority backoff / read‑only mode:** explicit behavior for nodes without quorum: refuse leadership and write traffic.
7. **Client‑side safety:** producers/writers use **strong acks** and **retries** so only majority‑committed results are considered successful.

---

## 6) Generic configuration knobs (by concept)

* **Cluster size & placement:** choose **odd counts**; place a **majority** in the same AZ/DC when latency between sites is unstable.
* **Election timeouts:** too low → flapping; too high → slow failover. Use **randomized** ranges to dodge tie elections.
* **Heartbeat intervals:** balance fast detection vs. network overhead.
* **Write quorum levels:** `majority` / `QUORUM` / `ALL` vs. `ONE` (latency vs. safety trade).
* **Unclean leadership:** disable promotions of **out‑of‑date** replicas.
* **Client acknowledgements:** prefer `acks=all` or majority‑style acks for critical writes.
* **Clock discipline:** NTP/chrony; big clock skew undermines leases and token freshness.

---

## 7) Kafka (KRaft and legacy ZooKeeper)

### What & why

* **ZooKeeper era:** controller election and metadata lived in ZooKeeper’s quorum; only the majority side progressed.
* **KRaft (ZooKeeper‑less):** Kafka brokers form a **Raft controller quorum**. Only a **majority** can elect an **active controller** and **commit metadata** to the metadata log.

### Split‑brain scenarios

* **Controller quorum partitioned:** only the majority voter set can continue metadata changes; minority stalls.
* **Broker isolation:** an out‑of‑sync replica should not become leader if **ISR** checks and **unclean election** are configured safely.

### Example walkthrough (4 brokers, 2 partitions)

1. Start: brokers form the **controller quorum**; one becomes **active controller**.
2. A link failure isolates 1 broker (split 3‑vs‑1).
3. The **3** retain controller leadership; metadata commits continue.
4. The **1** cannot form a majority → **no metadata changes**; it serves only what is already safe.
5. When the link heals, the isolated broker **catches up** from the metadata log and re‑joins as follower.

### Key configs (safety‑focused)

* **Controller quorum**: `process.roles=controller,broker` as needed, `controller.quorum.voters`, `controller.listener.names`, `node.id`.
* **Write path safety**: `min.insync.replicas` per topic + producer `acks=all` and sensible `delivery.timeout.ms`/`retries`.
* **Leadership hygiene**: `unclean.leader.election.enable=false`; ensure ISR monitoring and lag thresholds (e.g., `replica.lag.time.max.ms`).
* **Replication factor**: set `replication.factor` ≥ 3 for important topics to allow majority during failures.
* **Placement**: spread replicas across racks/AZs while keeping a **majority** where links are strongest.

*Effect:* Majority‑only metadata commits plus ISR‑checked leadership prevent stale leaders and unsafe writes during partitions.

---

## 8) Flink (HA with ZooKeeper or Kubernetes/etcd)

### What & why

* **HA mode** ensures a **single JobManager leader** coordinates scheduling. State like checkpoints and job graphs live in **shared HA storage**. Coordination is backed by **ZooKeeper** or **Kubernetes/etcd**.

### Split‑brain scenarios

* **Dual JobManagers**: partition or timing skew causes two candidates to believe they are leader.
* **Dispatcher/ResourceManager duplication**: if exclusivity is not enforced by the HA backend, multiple components may try to manage the same cluster.
* **TaskManager isolation**: workers keep running but lose contact; heartbeats expire.

### Resolution & configs

* **ZooKeeper HA**: `high-availability.mode: zookeeper`, `high-availability.zookeeper.quorum`, `high-availability.storageDir`.
* **Kubernetes HA**: `high-availability.mode: kubernetes` (leadership via Kubernetes API/etcd).
* **Heartbeat & timeouts**: adjust JobManager ↔ TaskManager intervals to detect isolation without false positives.
* **Checkpointing**: periodic checkpoints to durable storage; on failover the new leader **restores the last successful** checkpoint/savepoint.
* **Exactly‑once sinks**: use two‑phase sinks or idempotent outputs to avoid duplicates after recovery.

*Effect:* The HA backend’s majority rules and persisted checkpoints guarantee **one coordinator** and consistent restart after partitions.

---

## 9) MongoDB (replica sets)

### What & why

* A replica set elects **one primary**; secondaries replicate the **oplog**. Elections are majority‑based; only a majority side can host a primary.

### Split‑brain scenarios

* Primary becomes isolated while a majority elsewhere elects a **new primary**.
* Isolated former primary keeps serving if not fenced; conflicting writes appear when connectivity returns.

### Resolution & configs

* **Write admission**: `writeConcern: "majority"` so only majority‑replicated writes are acknowledged.
* **Read safety**: `readConcern: "majority"` (or stronger modes for linearizable reads when required).
* **Election shaping**: tune `electionTimeoutMillis`, member `priority`, and `votes`; add an **arbiter** to break ties in even counts (no data redundancy).
* **Fencing & step‑down**: heartbeat timeouts ensure isolated primaries **step down**; clients discover the new primary via drivers.
* **Hidden/priority‑0 members**: hold extra copies without affecting elections; useful for analytics.

*Effect:* Majority‑only primaries plus majority write concerns stop minority partitions from accepting durable writes.

---

## 10) Cassandra

### What & why

* Data is partitioned by token ranges and replicated with a **replication factor (RF)**. Clients choose a **consistency level (CL)** per operation.

### Split‑brain scenarios

* A partition divides replicas; with weak CL (e.g., `ONE`) both sides may accept writes, creating divergent versions.

### Resolution & configs

* **Consistency levels**: choose `QUORUM`/`LOCAL_QUORUM`/`ALL` for critical data; follow **(R + W > N)** (read quorum (R), write quorum (W), RF (N)).
* **Failure detection**: Cassandra uses **phi‑accrual** detectors; tune `phi_convict_threshold` to avoid premature eviction under jittery networks.
* **Repair & reconciliation**: periodic **anti‑entropy repairs** (e.g., `nodetool repair`) and **read repair** ensure replicas converge.
* **Conditional updates**: **Lightweight Transactions (Paxos)** for compare‑and‑set operations requiring strict agreement.
* **Topology**: `NetworkTopologyStrategy` with `LOCAL_QUORUM` to contain cross‑DC partitions while keeping safety inside a DC.

*Effect:* Majority‑based reads/writes prevent divergence; background repair resolves residual drift after partitions heal.

---

## 11) Failure taxonomy

* **Leader isolation**: leader loses contact with majority; majority elects a new leader; old leader must be fenced.
* **Minority write acceptance**: weak acks allow writes on the small side; conflicts on heal.
* **Dual leadership**: two leaders due to misconfigured timeouts or clocks.
* **Asymmetric partitions**: A can reach B, B cannot reach A; indirect probes or leases reduce mistakes.

---

## 12) Observability & alerts

**Signals to track**

* Election counts, term/epoch increments, and time to elect.
* Heartbeat RTT/timeout rates; follower lag (log end offset/oplog position).
* ISR size and churn (Kafka), number of in‑sync replicas below `min.insync.replicas`.
* Replica set primary changes (MongoDB) and step‑down reasons.
* Failed/suspect nodes and gossip suspicion (Cassandra/Flink via backing store).

**Alert ideas**

* “No controller quorum” (Kafka), “primary unreachable” (MongoDB), “JobManager leader lost” (Flink).
* “Acknowledged writes below quorum,” “unclean leader election attempted,” “repair overdue.”

---

## 13) Operational playbook (quick steps)

1. **Identify scope**: which side has majority voters?
2. **Freeze minority**: enforce read‑only or stop writers on the minority side.
3. **Verify safety knobs**: acks/quorum settings, unclean election disabled, fencing active.
4. **Restore connectivity**: networking, routes, security groups, DNS.
5. **Reintegrate**: allow lagging nodes to catch up from logs/oplogs/checkpoints.
6. **Repair**: run anti‑entropy/repair jobs where applicable; validate data shape.
7. **Post‑mortem**: adjust timeouts, placement, and alerts to prevent recurrence.

---

## 14) Common pitfalls and practical checks

* **Even voter counts** without a tiebreaker → deadlock risk.
* **Unclean leader election** in log‑replicated systems → potential data loss.
* **Weak client acks** (e.g., `acks=1`, `ONE`) on critical writes → divergence under partitions.
* **Cross‑AZ layouts** with no majority in any single site → frequent stalls.
* **Clock skew** → bogus leases and overlapping leadership.

**Chaos drills**

* Cut links between halves (simulate 3‑vs‑2) and verify only the majority proceeds.
* Kill leaders and measure election times; clients should surface clear, bounded errors.
* Validate that minority sides refuse writes and leaders step down quickly.

---

## 15) Summary

Split‑brain is a **communication isolation** problem that creates **competing authorities**. The durable cures are: **majority quorum**, **term/epoch‑based leadership with fencing**, and **write admission control** requiring acknowledgements from an up‑to‑date quorum. Observability and drills complete the picture.

Applied to common platforms:

* **Kafka (KRaft)**: Raft controller quorum, `min.insync.replicas`, safe leader election; replicas catch up from the metadata log.
* **Flink**: HA backends (ZooKeeper/Kubernetes) guarantee a **single JobManager leader** and checkpointed recovery.
* **MongoDB**: majority elections and `writeConcern`/`readConcern` protect writes and reads; isolated primaries step down.
* **Cassandra**: operation‑level consistency (`QUORUM`, `LOCAL_QUORUM`, `ALL`) with regular repair; Paxos for strict CAS.

Design with **odd‑sized quorums**, keep a **majority near each other** when links are weak, fence outdated leaders, and verify behavior with **failure drills** before production traffic depends on the cluster.
