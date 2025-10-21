### Database Decision Guide

---

### Background & Evolution

Historically, traditional RDBMS systems like Oracle, MySQL, and PostgreSQL were the backbone of data storage—offering reliable, ACID-compliant transactional operations. However, with the advent of the internet era and cloud-scale applications, new demands such as flexible data models, global distribution, real-time analytics, and high write throughput exposed the limitations of monolithic relational systems.

This led to the rise of **polyglot persistence**: using different database engines optimized for specific use cases. This guide consolidates practical and architectural details for PostgreSQL, MongoDB, Redis (cache and persistent KV), Elasticsearch, and Cassandra.

---

### 1. PostgreSQL (Relational Database)

**Category**: RDBMS

**Strengths**:

* ACID transactions and referential integrity
* Mature support for joins, subqueries, window functions, and stored procedures
* Rich indexing (B-Tree, GIN, GiST, BRIN)
* Extensions (PostGIS, TimescaleDB, Citus)

**Joins & Filters**:

* Excellent support for multi-table joins
* Filtering with `WHERE`, `HAVING`, subqueries, windowed functions

**Ideal For**:

* Structured data, OLTP systems
* Applications needing strong consistency and transactional isolation

**Trade-offs**:

* Schema rigidity (requires migrations)
* Vertical scaling (horizontal possible via sharding tools)

**Examples**:

* E-commerce (users, products, orders)
* Banking (ledgers, accounts)
* HR systems

**Sample Schema**:

```sql
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  name TEXT,
  email TEXT UNIQUE
);

CREATE TABLE orders (
  id SERIAL PRIMARY KEY,
  user_id INT REFERENCES users(id),
  total DECIMAL,
  created_at TIMESTAMP
);
```

---

### 2. MongoDB (Document Store)

**Category**: NoSQL Document-oriented

**Strengths**:

* Flexible, dynamic schemas (BSON)
* Horizontal scalability and high availability
* Built-in replication and sharding
* Nested documents and arrays for hierarchical data

**Joins & Filters**:

* `$lookup` provides basic join support (not performant for deep joins)
* Aggregation pipeline for filtering, grouping, transformations

**Ideal For**:

* Evolving schemas
* Hierarchical, document-based storage
* Fast development cycles

**Trade-offs**:

* Denormalization required for performance
* Weaker transactional guarantees (multi-doc transactions supported since v4.0)

**Examples**:

* CMS, blogs, catalogs
* IoT metadata

**Sample Schema**:

```json
{
  "_id": "user_123",
  "name": "John Doe",
  "orders": [
    { "id": 1, "total": 100 },
    { "id": 2, "total": 250 }
  ]
}
```

---

### 3. Redis (Cache Layer)

**Category**: In-memory KV store

**Strengths**:

* Sub-millisecond latency
* Rich data structures (hashes, sets, lists, sorted sets, bitmaps)
* TTL and expiration
* Pub/Sub messaging

**Joins & Filters**:

* No joins
* Key-based access; basic pattern filtering (non-scalable)

**Ideal For**:

* Session storage, API response caching
* Rate limiting, leaderboards

**Trade-offs**:

* Volatile unless persistence enabled
* Memory-constrained unless configured with Redis-on-Flash

**Examples**:

* Token/session stores
* Caching auth lookups

**Key Example**:

```text
Key: user:session:123
Value: { "token": "abc", "expires": 1696345600 }
```

---

### 4. Redis (Persistent KV Store)

**Category**: Persistent KV (Redis with AOF/RDB)

**Strengths**:

* Same performance benefits as in-memory Redis
* Durability with AOF or RDB snapshots

**Joins & Filters**:

* None; data access is by explicit key
* Filtering by key pattern (non-performant for large sets)

**Ideal For**:

* Persistent feature flag stores
* Distributed locking, configuration settings

**Trade-offs**:

* Not queryable; values are opaque unless externally structured (e.g., JSON)

**Examples**:

* Global config store
* Feature toggle systems

**PostgreSQL KV Emulation**:

```sql
CREATE TABLE kv_store (
  key TEXT PRIMARY KEY,
  value JSONB
);
```

---

### 5. Elasticsearch (Search & Analytics Engine)

**Category**: Full-text Search Engine / NoSQL

**Strengths**:

* Text search with fuzzy matching, scoring, relevance
* Aggregations, filters, geo-search, highlighting
* Distributed architecture; near real-time ingestion

**Joins & Filters**:

* No relational joins (uses parent/child relationships)
* Strong filtering using Query DSL (term, range, bool, nested queries)

**Ideal For**:

* Full-text search, logging, observability, analytics

**Trade-offs**:

* Not ACID compliant
* High storage overhead (due to indexing and replicas)
* Denormalization required

**Examples**:

* Product search
* Log analytics (ELK stack)
* Real-time dashboards

**Sample Document**:

```json
{
  "product": "Running Shoes",
  "brand": "Nike",
  "desc": "Lightweight running shoes",
  "price": 89.99
}
```

---

### 6. Apache Cassandra (Wide-column NoSQL)

**Category**: Distributed, columnar NoSQL

**Strengths**:

* High throughput, low latency writes
* Decentralized peer-to-peer architecture
* Multi-region and multi-datacenter ready
* Tunable consistency

**Joins & Filters**:

* No joins
* Filters only on primary key or indexed columns

**Ideal For**:

* High-ingest, write-heavy systems
* Event sourcing, metrics, sensor data

**Trade-offs**:

* Query-first schema modeling
* Anti-patterns (e.g., wide partitions) can cause performance degradation
* No full-table scans or ad-hoc queries

**Examples**:

* IoT ingestion pipelines
* Messaging and time-series platforms

**Schema Example**:

```sql
CREATE TABLE sensor_data (
  device_id TEXT,
  ts TIMESTAMP,
  temperature FLOAT,
  humidity FLOAT,
  PRIMARY KEY ((device_id), ts)
);
```

---

### Choosing the Right Database (Analogy)

Choosing a database is like picking the right tool for a job:

* **PostgreSQL**: A precision toolset—measured, structured, reliable
* **MongoDB**: A whiteboard—you can sketch freely and change as needed
* **Redis (Cache)**: RAM in your brain—blazingly fast but volatile
* **Redis (KV)**: A sticky note—quick reference and simple structure
* **Elasticsearch**: A powerful search engine—built for exploration
* **Cassandra**: A write-optimized factory—made for heavy ingestion

---

### Summary Table

| Database      | Category          | Strengths                        | Ideal For                             | Trade-offs                                 |
| ------------- | ----------------- | -------------------------------- | ------------------------------------- | ------------------------------------------ |
| PostgreSQL    | RDBMS             | Joins, ACID, indexing            | Structured, transactional workloads   | Vertical scaling, rigid schema             |
| MongoDB       | NoSQL Document    | Flexible schema, nested docs     | Evolving data, hierarchical documents | Inefficient joins, data duplication        |
| Redis (Cache) | In-memory KV      | Speed, TTL, Pub/Sub              | Session/token caching, rate limiting  | Volatile unless persisted                  |
| Redis (KV)    | Persistent KV     | Fast access, lightweight storage | Configs, feature flags                | Opaque structure, weak filtering           |
| Elasticsearch | Search Engine     | Full-text search, analytics      | Logs, metrics, product search         | High storage, denormalization              |
| Cassandra     | Wide-column NoSQL | High ingest, geo-distributed     | IoT, logs, write-heavy apps           | Complex schema, no joins or ad-hoc queries |
