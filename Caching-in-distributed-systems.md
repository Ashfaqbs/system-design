## Caching and Distributed Caching – A Complete Reference

---

### Part 1: Introduction – The Problem

Imagine an e-commerce application with heavy read traffic. Every time a user visits a product page, the application hits the database to fetch the same product details. As traffic grows, database load increases, response time slows down, and performance drops.

---

### Part 2: In-Memory Caching – Single App Scenario

#### Situation:

A Spring Boot application serving thousands of users, many of whom request the same data repeatedly (e.g., product catalog, config files).

#### Solution:

Use **in-memory caching** (like `ConcurrentHashMap` or libraries like Caffeine or Ehcache).

* **How It Works**:

  * Data is stored inside the application's JVM memory.
  * On first request: fetch from DB → cache it.
  * Next requests: fetch directly from memory.

#### Pros:

* Fastest (no network calls)
* Easy to implement
* Zero external dependencies

#### Cons:

* Memory size is limited
* Cache is **not shared** across multiple instances (e.g., in load-balanced setup)
* Data inconsistency risk across instances

#### When to Use:

* Small-scale systems
* Single-instance applications
* Non-critical data (e.g., user preferences)

---

### Part 3: Distributed Caching – Redis Cluster with Multiple Applications

#### Situation:

A microservice architecture with multiple instances of each service (e.g., user-service, product-service) behind a load balancer.

#### Problem:

Each instance having its own cache causes **data inconsistency** and **cache duplication**.

#### Solution:

Introduce a **centralized Redis cluster**.

* Each application instance connects to this Redis cluster.
* Cache reads/writes go over the network to this central store.
* Redis cluster can scale horizontally and manage partitions.

#### Redis Cluster Architecture:

* Nodes are sharded
* Data is distributed using hash slots
* Supports replication and failover

#### Pros:

* Shared cache among all app instances
* Better memory usage
* Scales horizontally
* Resilient with replication and persistence

#### Cons:

* Slight network latency compared to in-memory
* Requires Redis server(s) to be managed or hosted

#### When to Use:

* Distributed systems
* Large user base with heavy read/write
* Need for cache consistency and central control

---

### Part 4: Cache Invalidation Strategies – What If the Data Changes?

#### Problem:

Cached data may become stale if the underlying source (e.g., database) is updated.

#### Strategy 1: Time-to-Live (TTL)

**Situation**: Product prices are updated rarely.

**What To Do**: Set a TTL (e.g., 10 minutes). After that, the cache auto-expires and data is fetched again.

**Use Case**: Semi-static data

---

#### Strategy 2: Write-through Cache

**Situation**: Insert/update happens frequently (e.g., user settings).

**What To Do**:

* Write data to cache and DB **together** in the same flow.
* Cache always has fresh data.

**Use Case**: Systems needing strong consistency.

---

#### Strategy 3: Write-behind Cache

**Situation**: High write frequency, but DB writes can be delayed.

**What To Do**:

* Write to cache first.
* Write to DB **asynchronously** in batches.

**Use Case**: Analytics, log collectors, temporary session info

---

#### Strategy 4: Cache Aside (Lazy Loading)

**Situation**: Read-heavy, occasional writes.

**What To Do**:

* App checks cache first.
* If not found, load from DB, update cache.
* For updates, app manually invalidates the cache.

**Use Case**: General read-heavy microservices

---

### Part 5: Cache Eviction Policies

Redis and in-memory caches use eviction policies to manage memory:

* **LRU**: Least Recently Used
* **LFU**: Least Frequently Used
* **FIFO**: First In, First Out
* **TTL**: Time-based expiry

Choose based on access pattern.

---

### Part 6: Other Important Concepts

#### A. Hot Key Problem

**Situation**: A single key (e.g., homepage config) is accessed heavily.

**Problem**: Load imbalance, cache server becomes bottleneck.

**Solution**:

* Use **replicated cache** for hot keys
* Use **local caching layer** (e.g., Caffeine + Redis hybrid)

---

#### B. Cache Stampede

**Situation**: A popular key expires; multiple instances hit the DB at once.

**Solution**:

* Use **lock or mutex** while refreshing the cache
* Use **randomized TTL** to avoid simultaneous expiry

---

#### C. Data Partitioning in Distributed Cache

Redis Cluster partitions keys using **hash slots (0-16383)**. Each node owns a range. Requests are routed accordingly.

Redis handles this using **CRC16(key) % 16384** to determine which node should handle the request.

---

### Final Summary: When to Use What?

| Setup                             | Use Case                                 |
| --------------------------------- | ---------------------------------------- |
| In-Memory Cache (Local)           | Small apps, fast reads, no need to share |
| Single Redis Instance             | Simpler apps, minimal scaling            |
| Redis Cluster (Distributed Cache) | Microservices, high scale, shared state  |
| Hybrid (Local + Redis)            | Ultra-low latency, high reliability      |


