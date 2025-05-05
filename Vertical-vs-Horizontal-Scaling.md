# Scaling Systems: Vertical vs Horizontal Scaling

---

## What Is Scaling?

Scaling refers to increasing a system’s **capacity to handle more load**, such as more users, more data, or higher traffic. This is achieved by adding more computing power. There are two main strategies:

* **Vertical Scaling**: Increasing the power of a single machine
* **Horizontal Scaling**: Increasing the number of machines

---

## What Is Vertical Scaling?

Vertical scaling (also called **scale-up**) means upgrading the **resources of a single server** — such as adding more CPU, RAM, or storage — to make it handle more work.

### Characteristics:

* Uses **a single machine**
* The application still runs on one server, but that server becomes more powerful
* Requires **no changes to application code**
* Easier to manage in the short term

### Example:

A Java web application running on an 8 GB RAM server is moved to a 32 GB RAM server to support more users.

---

## What Is Horizontal Scaling?

Horizontal scaling (also called **scale-out**) means adding **more machines (nodes or servers)** to a system and distributing the load across them.

### Characteristics:

* Involves **multiple servers**
* Each server may handle a portion of traffic or data
* Requires application design to support distribution (e.g., stateless services, load balancers)
* More complex to manage

### Example:

Instead of upgrading a single server, three identical servers are deployed with a load balancer distributing incoming requests across all of them.

---

## Key Differences

| Factor                 | Vertical Scaling                          | Horizontal Scaling                     |
| ---------------------- | ----------------------------------------- | -------------------------------------- |
| **Strategy**           | Increase power of one machine             | Add more machines                      |
| **Complexity**         | Simple to implement                       | Complex architecture                   |
| **Cost**               | Cost increases quickly (premium hardware) | Cost-effective for large scale         |
| **Limitations**        | Physical hardware limits                  | Can scale almost infinitely            |
| **Fault Tolerance**    | Single point of failure                   | Higher availability, failover possible |
| **Application Design** | No major changes needed                   | Requires distributed system design     |

---

## When to Use Vertical Scaling

* The system has **modest growth needs**
* The architecture is **monolithic** or not designed for distribution
* Operations need to remain **simple and quick to set up**
* Upgrading the server meets current load requirements

Vertical scaling is suitable for early-stage applications, internal tools, or systems with **low to moderate load** that does not increase frequently.

---

## When to Use Horizontal Scaling

* The system must handle **large or unpredictable traffic spikes**
* The architecture is **stateless** or distributed by design
* **High availability and fault tolerance** are critical
* The system needs to scale **beyond what a single machine can offer**

Horizontal scaling is better for web applications, microservices, cloud-native systems, and real-time data processing where the workload can be shared across nodes.

---

## Pros and Cons

### Vertical Scaling

**Advantages:**

* Simpler to set up and deploy
* No need to change the architecture
* Suitable for databases and legacy systems

**Disadvantages:**

* Hardware limits apply (can only upgrade so far)
* Downtime may be required during upgrades
* Single point of failure

### Horizontal Scaling

**Advantages:**

* Scales out as needed
* Redundant and fault-tolerant
* No single point of failure

**Disadvantages:**

* More complex infrastructure (load balancers, clustering)
* Requires distributed architecture patterns
* Harder to debug and manage

---

## Alternatives and Hybrid Approaches

1. **Hybrid Scaling**
   Start with vertical scaling, and when limits are reached, transition to horizontal. This is a common path for many systems.

2. **Cloud Auto Scaling**
   Cloud platforms (AWS, Azure, GCP) provide **auto-scaling** capabilities that automatically add/remove machines based on current load.

3. **Serverless Architecture**
   Systems can scale at the **function level**. Only the code runs, and the infrastructure auto-scales behind the scenes. No manual provisioning is needed. Examples include AWS Lambda or Google Cloud Functions.

4. **Container Orchestration (e.g., Kubernetes)**
   Handles horizontal scaling of containers automatically. Applications must be containerized, and resources are managed across nodes.

---

## Which to Choose?

| Situation                                       | Preferred Scaling Approach   |
| ----------------------------------------------- | ---------------------------- |
| Fast growth, unpredictable load                 | Horizontal Scaling           |
| Low-to-medium consistent traffic                | Vertical Scaling             |
| Application not designed for distribution       | Vertical Scaling (initially) |
| Microservices, stateless APIs, cloud-native     | Horizontal Scaling           |
| Need for high availability and redundancy       | Horizontal Scaling           |
| Legacy system, no time for architectural change | Vertical Scaling             |

---

## Summary

* **Vertical Scaling** increases capacity by upgrading the machine.
* **Horizontal Scaling** increases capacity by adding more machines.
* Vertical scaling is easier but has limits.
* Horizontal scaling is more powerful but requires design and operational complexity.
* Modern systems often use **a combination of both**, depending on the workload and growth stage.




# Consistent Hashing: Why It’s Needed and How It Solves Real Problems

---

## Background: Naive Hashing With Modulo

In distributed systems, hashing is often used to determine which server (or cache node) should handle a request. The **simplest approach** is:

```
serverIndex = hash(requestKey) % totalServers
```

This sends the request to one of the N servers based on the remainder of the hash value.

### Example:

* Let total servers = 4 → Servers: `0, 1, 2, 3`
* For request ID = `10`, hash(10) = 10 → 10 % 4 = **2** → Route to Server 2

However, when the number of servers changes, this calculation becomes unreliable.

---

## Problem: What Happens When Servers Are Added or Removed?

Adding or removing servers **breaks the original mapping** between requests and servers.

### Continuing the Example:

* Original setup: 4 servers
  `hash(10) % 4 = 2` → Server 2

* After adding 1 more server (total = 5):
  `hash(10) % 5 = 0` → Server **0**

* After adding another (total = 6):
  `hash(10) % 6 = 4` → Server **4**

### Problem: The hash target **keeps shifting** every time the total number of servers changes.

---

## Issues With Naive Hashing (Modulo-Based)

| Issue                            | Description                                                                       |
| -------------------------------- | --------------------------------------------------------------------------------- |
| **Cache Misses**                 | Request may reach a different server than before, causing cache to be missed.     |
| **Data Rebalancing**             | Most (or all) of the data must be moved across servers, increasing network usage. |
| **Hot Keys**                     | Certain keys may hash to the same server repeatedly, causing imbalance.           |
| **Downtime / Performance Drops** | Adding or removing nodes causes inconsistency in routing during transitions.      |
| **Scalability Limitations**      | Cannot scale smoothly because each change causes widespread remapping.            |

---


## 🔍 Breaking Down Each Issue with Examples

### **1. Cache Misses**

#### Scenario:

* There are 3 cache servers: `Server A`, `Server B`, `Server C`
* Client requests `User ID 10` → `hash(10) % 3 = 1` → Routed to `Server B`
* Server B caches the user profile (e.g., password, session token)

Now, **one more server is added** → total = 4 servers

* `hash(10) % 4 = 2` → Now routed to `Server C`
* But `Server C` has **no cached data**, so the system must fetch from DB again

#### Real Impact:

* Increased response time
* More load on the database
* Wasted memory on `Server B` that is no longer receiving that request

> This is the most **direct and visible problem** — the cache is present, but the request no longer goes there.

---

### **2. Data Rebalancing**

#### What It Means:

After adding/removing a server, **most of the keys (cached data, database shards, etc.) must be redistributed** across servers.

#### Example:

* Suppose 1000 keys (users) were spread across 4 servers via `hash(key) % 4`
* Keys get divided like:

  * Server A: 250 users
  * Server B: 250 users
  * Server C: 250 users
  * Server D: 250 users

Now, 1 more server is added → 5 total → `hash(key) % 5`

* All existing keys will now map to **different servers**, not just a few.
* Each server must now **move large chunks** of data to other servers

  * Server A may now handle 200 users instead of 250
  * Server E (new) needs to receive 200 keys from others

#### Real Impact:

* High **network bandwidth usage** as data is moved
* Temporary **inconsistency** if data transfer is not atomic (complete in one go)
* **Increased CPU/disk usage** during rebalancing

> So this is not just about cache but applies to **sharded databases**, file stores, even object stores like S3 behind the scenes.

---

### **3. Hot Keys**

#### Hot Keys = Frequently Accessed Keys (e.g., same user ID, product ID, etc.)

Even in consistent hashing, **some keys** may get hit millions of times (e.g., a trending item on an e-commerce site).

#### If Most Requests Go to Same Server:

* That server gets overloaded
* Others sit idle
* Caching helps *only if* that server can handle the load

#### When It’s a Problem:

* When the **hot key causes server saturation**, while others are underutilized
* E.g., Redis or Memcached node handling 90% of requests due to one key

####  Real Impact:

* High CPU, RAM, or network usage on one server
* System-wide **performance bottleneck**

> So it's not about cache hits/misses here, but **uneven load**.

---

### **4. Downtime / Performance Drops During Transitions**

When nodes are added/removed, routing logic and key ownership change.

#### Example:

* A server is removed (e.g., crashed or for maintenance)
* Suddenly, its share of keys must be handled by other servers
* They might not have that data cached, or replicated

####  Real Impact:

* Increased DB load to re-fetch missed keys
* Higher latency
* Temporary **inconsistency** if background sync or replication is not fast enough

> This also includes *manual* server additions (e.g., scaling up during traffic spikes).

---

### **5. Scalability Limitations**

This refers to how **fragile** the naive hash model is during scaling.

#### Example:

* Starting with 2 servers → all hash(key) % 2
* Want to scale to 3, 4, or 10 servers

Each time the server count changes:

* The entire hash space changes
* Most requests now point to **different servers**
* All prior caching or partitioning logic becomes invalid

#### Real Impact:

* Makes **auto-scaling** impractical
* Makes **dynamic infrastructure** (Kubernetes, cloud autoscaling) harder to use
* Developers may avoid scaling out due to impact on routing logic

> The system becomes **rigid** and unscalable unless consistent hashing or sticky routing is used.

---

##  Summary Table (With Revised Clarifications)

| Issue                       | Clarified Description                                                                        |
| --------------------------- | -------------------------------------------------------------------------------------------- |
| **Cache Misses**            | Requests go to new servers after a node count change → cache becomes useless                 |
| **Data Rebalancing**        | Data (or keys) must be reshuffled heavily between nodes, causing network and memory overhead |
| **Hot Keys**                | Overloaded keys create imbalance if they always map to a single node                         |
| **Downtime / Transitions**  | During node changes, servers may not have relevant data → higher latency or failure          |
| **Scalability Limitations** | Naive hashing fails when infrastructure needs to scale dynamically or frequently             |

---

## Solution: What Is Consistent Hashing?

Consistent hashing changes the approach: instead of using modulo, it maps **both servers and keys onto a circular space (hash ring)**.

### Key Idea:

* The hash ring is a fixed-size number line (0 to MAX\_HASH)
* Both **server identifiers** and **keys** are hashed onto this ring
* A key is stored on the **next server node in the clockwise direction**

### Visual Concept:

```
Ring: 0 -------------------> MAX_HASH (circle wraps)
        ^         ^        ^
     Key K1     Node A   Node B

→ K1 will be stored in the first node clockwise (say Node B)
```

---

## How Does Consistent Hashing Fix the Issues?

| Problem Solved           | How It’s Solved                                                                  |
| ------------------------ | -------------------------------------------------------------------------------- |
| **Minimal Rebalancing**  | When a server is added/removed, **only a small portion** of keys are reassigned. |
| **Stable Routing**       | The hash ring ensures most keys stay on the same server after changes.           |
| **Scalable**             | New servers can be added with almost zero disruption to existing traffic.        |
| **Resilient to Failure** | When a server goes down, its keys are reassigned to the next available node.     |

---

## Virtual Nodes (VNodes) — A Common Enhancement

To avoid uneven load (especially when hash values cluster), each physical server is represented by **multiple virtual nodes** on the ring.

### Benefits of VNodes:

* Smooths out **imbalanced data distribution**
* Makes **server capacity planning** easier
* Improves **load fairness**

---

## When to Use Consistent Hashing?

| Scenario                        | Why It’s Suitable                                                         |
| ------------------------------- | ------------------------------------------------------------------------- |
| **Distributed Cache Systems**   | Keeps cache hits high after node changes (e.g., Redis Cluster, Memcached) |
| **Sharded Databases**           | Reduces migration costs when scaling out                                  |
| **Load Balanced Microservices** | Helps sticky routing of specific request IDs                              |
| **Key-Value Stores / DHTs**     | Used in systems like Cassandra, DynamoDB                                  |

---

## Real-World Tools and Systems That Use It

| System              | Purpose Using Consistent Hashing                 |
| ------------------- | ------------------------------------------------ |
| **Cassandra**       | Distributes data across partitions               |
| **ElasticSearch**   | Shards documents evenly across nodes             |
| **Redis Cluster**   | Maps keys to slots on nodes                      |
| **Amazon DynamoDB** | Ensures high availability in distributed storage |
| **HAProxy / Envoy** | Routing decisions with sticky sessions           |

---

## Summary Table

| Concept                | Description                                        |
| ---------------------- | -------------------------------------------------- |
| Naive Hashing          | Uses modulo; breaks on server count change         |
| Consistent Hashing     | Uses a ring; minimizes remapping                   |
| Virtual Nodes (VNodes) | Multiple points per server to balance distribution |
| Use Cases              | Caching, sharding, load routing, storage systems   |

---