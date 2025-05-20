## Database Sharding

### 1. Layman's Explanation

**Analogy:**

Imagine a library with an ever-growing collection of books. Initially, all books are stored in a single room. As the collection expands, finding a specific book becomes time-consuming, and the room becomes overcrowded. To address this, the library decides to distribute books across multiple rooms based on genres—fiction in one room, non-fiction in another, and so on. This distribution allows for quicker access and better organization.

**Explanation:**

In this analogy, the library represents a database, and the books symbolize data entries. Distributing books across rooms based on genres is akin to **database sharding**, where data is partitioned into smaller, more manageable pieces (shards) and stored across multiple servers. This approach enhances performance, scalability, and manageability.

---

### 2. Technical Definition

**Definition:**

Database sharding is a horizontal partitioning technique that divides a large database into smaller, distinct pieces called shards. Each shard contains a subset of the data and operates as an independent database. Collectively, these shards represent the complete dataset. Sharding enables databases to handle large volumes of data and high traffic by distributing the load across multiple servers.

**Correlation with Analogy:**

Just as the library distributes books across rooms to manage an extensive collection efficiently, sharding distributes data across multiple servers to optimize performance and scalability.

---

### 3. Underlying Algorithms

#### a. Hash-Based Sharding

**Layman's Explanation:**

Consider assigning books to rooms based on the first letter of the author's last name. For instance, authors whose last names start with 'A' to 'F' go to Room 1, 'G' to 'L' to Room 2, and so on. This method ensures an even distribution of books.

**Technical Breakdown:**

* **Mechanism:** A hash function is applied to a sharding key (e.g., user ID), producing a hash value.
* **Assignment:** The hash value determines the shard where the data resides.
* **Advantage:** Promotes uniform data distribution, preventing hotspots.
* **Disadvantage:** Adding or removing shards requires rehashing, which can be complex.

**Analogy Correlation:**

Assigning books based on the author's last name initial ensures an even spread, similar to how hash-based sharding distributes data uniformly across shards.

#### b. Range-Based Sharding

**Layman's Explanation:**

Imagine organizing books in rooms based on publication years. Room 1 holds books from 1900-1950, Room 2 from 1951-2000, and so on. This setup allows readers interested in a specific era to go directly to the relevant room.

**Technical Breakdown:**

* **Mechanism:** Data is partitioned based on ranges of a sharding key (e.g., date ranges).
* **Assignment:** Each shard handles a specific range.
* **Advantage:** Efficient for range queries.
* **Disadvantage:** Uneven data distribution can lead to hotspots.

**Analogy Correlation:**

Grouping books by publication years parallels range-based sharding, where data is divided based on specific ranges.

#### c. Directory-Based Sharding

**Layman's Explanation:**

Suppose the library maintains a catalog that maps each book to its room. Regardless of genre or publication year, the catalog directs readers to the correct room.

**Technical Breakdown:**

* **Mechanism:** A lookup table maintains mappings between data records and their corresponding shards.
* **Assignment:** The directory determines the shard for each data entry.
* **Advantage:** Offers flexibility in data distribution.
* **Disadvantage:** The directory can become a single point of failure and may introduce latency.

**Analogy Correlation:**

Using a catalog to locate books mirrors directory-based sharding, where a central directory guides data access.

---

### 4. Historical Context

**Challenges Leading to Sharding:**

* **Scalability Issues:** Traditional databases struggled with increasing data volumes and user loads.
* **Performance Bottlenecks:** Single-server databases couldn't handle high traffic efficiently.
* **Hardware Limitations:** Vertical scaling (adding more resources to a single server) had physical and cost constraints.

**Milestones:**

* **Early 2000s:** Web applications like LiveJournal adopted sharding to manage growing user bases.
* **Mid-2000s:** NoSQL databases like MongoDB and Cassandra incorporated sharding for scalability.
* **Present Day:** Sharding is a standard practice in distributed database systems to ensure performance and scalability.

---

### 5. Problems It Solves

* **Enhanced Scalability:** Distributes data across multiple servers, allowing the system to handle increased loads.
* **Improved Performance:** Reduces query response times by limiting the data each server processes.
* **Fault Isolation:** Issues in one shard don't necessarily impact others, enhancing system reliability.
* **Cost Efficiency:** Enables the use of commodity hardware instead of investing in high-end servers.

---

### 6. Database Support

**Relational Databases (RDBMS):**

* **MySQL:** Supports sharding through application-level logic or third-party tools.
* **PostgreSQL:** Offers sharding capabilities via extensions like Citus.
* **Oracle:** Provides native sharding features in its enterprise editions.

**NoSQL Databases:**

* **MongoDB:** Built-in sharding support, distributing data across multiple shards.
* **Cassandra:** Employs consistent hashing for data distribution.
* **Redis:** Supports sharding through clustering mechanisms.

---

### 7. Use Cases & Importance

**When to Use Sharding:**

* **High Traffic Applications:** E-commerce platforms, social media sites, and online gaming services.
* **Large Datasets:** Systems handling massive amounts of data, like analytics platforms.
* **Geographically Distributed Users:** Applications requiring data localization for compliance or latency reasons.

**Importance in Modern Systems:**

Sharding is crucial for building scalable, resilient, and high-performing applications. It allows systems to grow seamlessly with user demands and data volumes, ensuring consistent user experiences.

---

### 8. Scaling & Transactions

**Horizontal Scaling:**

Sharding enables horizontal scaling by adding more servers (shards) to handle increased loads, distributing data and queries across these servers.

**Data Duplication & Consistency:**

* **Replication:** Often used alongside sharding to duplicate data across nodes for redundancy.
* **Consistency Models:** Systems may adopt eventual consistency or implement distributed transactions to maintain data integrity.

**Transactions in Sharded Architectures:**

Managing transactions across shards can be complex. Techniques include:

* **Two-Phase Commit (2PC):** Ensures atomic transactions across multiple shards.
* **Application-Level Coordination:** The application manages transaction logic to maintain consistency.

---

**Conclusion:**

Database sharding is a fundamental technique for achieving scalability and performance in modern applications. By partitioning data across multiple servers, it addresses the limitations of traditional databases, ensuring systems can handle growing demands efficiently.

---


## Database Sharding – FAQ's

---

### Q1: If there are 10 million records and they are divided across 10 servers (1 million per node), does this help with horizontal scaling? Can each node follow a master-slave architecture for better fault tolerance?

Yes. Distributing 10 million records across 10 servers—each handling 1 million records—is a form of **horizontal scaling**. This architecture allows the system to handle larger datasets and more concurrent queries by spreading the workload across multiple machines.

Each server (or node) in the sharded cluster can be configured to use **replication**, such as **master-slave (primary-replica)** architecture. In this setup:

* The **master node** handles write operations.
* One or more **replica (slave) nodes** continuously pull updates from the master.
* If the master fails, a **failover mechanism** can promote a replica to become the new master (auto-election), ensuring **high availability**.

This combination of sharding and replication improves both scalability and fault tolerance.

---

### Q2: If a query is made across the entire 10 million records (spread across shards), will all servers be queried? How does the system decide which shard to query? Is an API Gateway involved?

**Not all shards are always queried**, depending on the nature of the query and the sharding strategy:

* **If the query includes the sharding key** (e.g., user ID, order ID), the system can route the query directly to the relevant shard. This avoids a full-cluster scan and improves efficiency.
* **If the query lacks a sharding key**, or if it's a **global query** (e.g., full data scan or aggregation), the query is **broadcast** to all shards. The results are then aggregated and returned.

The routing mechanism can be built into the **application layer**, the **database driver**, or managed via an **API Gateway** or **database proxy/router** like:

* **MongoS** (for MongoDB)
* **Vitess** (for MySQL)
* **Citus** (for PostgreSQL)
* **Custom-built query routers**

An API Gateway is generally not responsible for directing database queries directly but may coordinate microservices that in turn talk to correct data partitions or backend databases.

---

### Q3: Does sharding support ACID transactions? If not, why?

**ACID support in sharded environments is limited and complex.** Here's why:

* **Atomicity**: Within a single shard, ACID is supported (if the underlying DB supports it).
* **Cross-shard transactions** complicate atomicity because they span multiple nodes and may require distributed transaction coordination (e.g., 2PC – Two-Phase Commit).
* **Consistency**: Difficult across shards without coordination, especially with concurrent updates.
* **Isolation**: Isolation levels are shard-local unless explicitly managed globally.
* **Durability**: Managed at the shard level through replication or WAL (Write-Ahead Logging).

Some modern databases offer **limited or eventual consistency** for performance and scalability. Others, like **Google Spanner**, use advanced techniques (e.g., atomic clocks) to provide globally-consistent transactions across shards, but with trade-offs in latency and complexity.

**Summary**: Full ACID across shards is non-trivial and often avoided in favor of **eventual consistency** or **application-managed consistency**.

---

### 1. **How does routing happen in a sharded database?**

In a sharded system with five servers (shards), each shard holds a part of the data based on a **sharding key** (e.g., `user_id`). When a user sends a query, the system needs to figure out which shard holds the relevant data and route the query there.

**Let's break it down:**

Imagine five shards (Server 1 to Server 5) and some data. Let’s assume the data is users, and the sharding key is the `user_id`.

* **Shards and data:**

  * **Server 1**: user IDs 1–100
  * **Server 2**: user IDs 101–200
  * **Server 3**: user IDs 201–300
  * **Server 4**: user IDs 301–400
  * **Server 5**: user IDs 401–500

Now, if a user with `user_id = 250` queries for their data, the system uses the `user_id` to figure out that **Server 3** holds this data (since the `user_id = 250` falls within 201–300).

The routing process works like this:

* The system looks at the `user_id` in the query.
* It uses the **sharding key** (here, the `user_id`) to calculate which shard the data belongs to.
* The query is routed directly to **Server 3** where the data for `user_id = 250` resides.

**Routing logic:**

* If the query needs to find a user, the system simply checks the `user_id` and maps it to the appropriate shard using the sharding key.
* This way, only the relevant shard is queried, not all five.

---

### 2. **How is ACID maintained across a single shard and not across multiple shards?**

In a **single shard** system, ACID properties (Atomicity, Consistency, Isolation, Durability) can be easily maintained because all the data for that shard is in one place, and transactions happen within that shard.

However, across **multiple shards**, maintaining ACID becomes difficult, particularly **Atomicity** and **Consistency**.

#### Example with 5 Shards:

Let’s say we have a transaction where **two updates** need to happen at once:

1. Update `user_id = 100` on **Shard 1**.
2. Update `user_id = 250` on **Shard 3**.

In a **single-shard** setup:

* The transaction will either **succeed or fail** as a whole. If the update on **Shard 1** fails, the system can roll back both updates, ensuring Atomicity.

In a **multi-shard** setup:

* If the update on **Shard 1** succeeds, but the update on **Shard 3** fails, the system cannot guarantee Atomicity across the shards because the data is distributed.
* This leads to **eventual consistency** where one shard might reflect the change before another.

In multi-sharded systems, achieving **true ACID** across multiple shards is difficult because:

* **Atomicity** is lost because transactions cannot be rolled back easily across shards.
* **Consistency** is difficult to maintain because one shard might update before others.

---

### 3. **Understanding the CAP Theorem**

The **CAP theorem** explains the trade-offs between **Consistency**, **Availability**, and **Partition Tolerance** in distributed systems. The theorem states that in a distributed database, it is impossible to achieve all three simultaneously. You can achieve at most two of the following:

* **Consistency (C):** Every read gets the most recent write.
* **Availability (A):** Every request gets a response, but it might not be the most recent data.
* **Partition Tolerance (P):** The system continues to operate even if some parts of it cannot communicate.

Here’s how it works with examples:

#### 1. **CA (Consistency + Availability):**

* **Scenario:** Achieving **Consistency** and **Availability** means the system always returns the most recent data, and every request will receive a response, but **Partition Tolerance** is sacrificed.
* **What happens:** If there is a network partition, some parts of the system may be unable to communicate, causing those parts to stop working.
* **Example:** A relational database where consistency and availability are more important than partition tolerance. A system like this might struggle if there’s a network failure between shards.

#### 2. **CP (Consistency + Partition Tolerance):**

* **Scenario:** The system ensures **Consistency** and **Partition Tolerance**, but sacrifices **Availability**.
* **What happens:** During a network partition, the system will refuse to answer requests until it can ensure that all nodes have consistent data.
* **Example:** A system like HBase or MongoDB can focus on ensuring that data is consistent and available after a partition, but if there's a network failure, it might stop responding to queries altogether.

#### 3. **AP (Availability + Partition Tolerance):**

* **Scenario:** The system ensures **Availability** and **Partition Tolerance**, but sacrifices **Consistency**.
* **What happens:** The system will continue operating even during network partitions, but the data might not be up-to-date or consistent across all nodes.
* **Example:** In a system like Cassandra, even if there is a network issue, it will still respond to queries, but the data might not be the most recent or consistent across all nodes.

---

**To summarize the CAP Theorem:**

* **C:** Always return the latest data, but may sacrifice availability if a partition occurs.
* **A:** Always respond to queries, even if it means returning stale data during partitions.
* **P:** Continue operating during partitions, but either availability or consistency may be compromised.

---