## **Why Big Tech Still Trusts Relational Databases: Scaling the Old School Way**

### Introduction

There's a common assumption that NoSQL databases like MongoDB are the go-to choice for large-scale systems due to their out-of-the-box support for horizontal scaling. However, a deeper look into the backend architectures of major platforms like Instagram, YouTube, and Stack Overflow reveals a different story: traditional relational databases are still widely used — and they scale just fine with the right architecture.

---

### Database Choices in Major Platforms

#### Instagram

* **Primary Database**: PostgreSQL
* **Additional Systems**:

  * Cassandra for distributed storage
  * Redis and Memcached for caching
  * Kafka for event streaming
  * Hadoop and Hive for analytics

Instagram employs a heavily sharded PostgreSQL setup to manage massive scale, with extensive use of caching and asynchronous processing.

#### YouTube

* **Primary Database**: Google Bigtable
* **Additional Systems**:

  * Spanner for relational storage
  * Colossus for file storage
  * Dremel/BigQuery for data analytics

YouTube leverages Google’s proprietary infrastructure, which includes Bigtable and Spanner, tailored for massive global scale.

#### Stack Overflow

* **Primary Database**: Microsoft SQL Server
* **Additional Systems**:

  * Redis for caching
  * Elasticsearch for full-text search
  * Cloudflare for CDN and protection

Stack Overflow relies on a vertically scaled SQL Server instance and detailed optimizations at the schema and query levels.

---

### RDBMS vs. Horizontal Scaling

Relational databases traditionally excel in consistency and structured data management but do not scale horizontally as easily as NoSQL systems. However, large-scale services overcome these limitations using architectural patterns such as:

#### Sharding

Manual partitioning of data across multiple databases. Each shard holds a subset of the dataset, often split by user ID or region.

#### Read Replication

Creating read-only replicas of the database to offload query traffic from the primary instance.

#### Caching Layers

In-memory stores like Redis and Memcached are used to serve frequently accessed data, reducing database load significantly.

#### Application-Level Routing

The application logic determines which shard or instance should be queried, often based on deterministic rules.

#### Job Queues

Non-essential writes are handled asynchronously using background processing, helping to manage peak loads effectively.

#### Schema Optimization

Proper indexing, selective normalization/denormalization, and avoiding expensive JOINs are crucial for performance at scale.

---

### Why RDBMS Still Thrive

Despite the appeal of NoSQL systems for their ease of scaling, relational databases continue to dominate in systems where data integrity, strong consistency, and complex query capabilities are essential. The architectural overhead of scaling RDBMS solutions pays off with reliable, predictable behavior and mature tooling.

---