# High Cardinality in Databases: Concepts, Internals, and Implications

---

## 1. Understanding Cardinality

**Cardinality** refers to the number of distinct values present in a column of a database table. It plays a significant role in query optimization, indexing decisions, and schema design.

### 1.1 Types of Cardinality

| Type               | Description                                        | Example                                 |
| ------------------ | -------------------------------------------------- | --------------------------------------- |
| High Cardinality   | Column contains a large number of unique values    | `email`, `timestamp`, `uuid`, `user_id` |
| Medium Cardinality | Column contains a moderate number of unique values | `country_code`, `device_type`           |
| Low Cardinality    | Column contains few unique values                  | `gender`, `status`, `region_code`       |

---

## 2. Applicability in Database Types

### 2.1 Relational Databases (RDBMS)

Examples: MySQL, PostgreSQL, Oracle, SQL Server

High cardinality fields are common in relational databases and can be:

* Present as unique constraints or primary keys when logical uniqueness is required.
* Indexed selectively based on query patterns and system capacity.

**Behavior**:

* RDBMS indexing mechanisms (usually B-Trees) can support high-cardinality indexes.
* However, frequent updates or inserts into such indexes can cause overhead in terms of:

  * Tree rebalancing
  * Page splits
  * Cache invalidations

**Key Notes**:

* Indexing high-cardinality columns is technically supported.
* Practical performance degradation occurs if the column is frequently written to or not commonly queried.

### 2.2 NoSQL Databases

#### Cassandra

* High cardinality columns are often found in time-series or event-based data.
* Not ideal for use in WHERE clauses unless modeled correctly.
* Secondary indexes on high-cardinality fields are discouraged due to their performance cost.

#### MongoDB

* Allows high-cardinality fields in schema-flexible documents.
* Indexing such fields impacts write throughput significantly.
* For analytical queries, MongoDB Atlas Search or aggregation pipelines are preferred over indexing volatile, high-cardinality fields.

---

## 3. Why High Cardinality Columns Are Not Ideal for Keys or Indexes

### 3.1 Not Suitable as Primary Keys

* Primary keys enforce uniqueness and are backed by **clustered indexes** in many RDBMS systems.
* High-cardinality fields may be unique, but they are often:

  * Volatile (e.g., timestamp)
  * Not semantically significant
  * Not optimized for read path queries

### 3.2 Indexing Considerations

#### Data Structures Used

| Index Type   | Underlying Data Structure | Description                                                          |
| ------------ | ------------------------- | -------------------------------------------------------------------- |
| B-Tree Index | Balanced Tree (B+Tree)    | Used in most RDBMS systems like MySQL, PostgreSQL                    |
| Hash Index   | Hash Table                | Used in specific engines (e.g., MEMORY table in MySQL)               |
| Bitmap Index | Bitmap Vector             | Efficient for low-cardinality fields (Oracle, PostgreSQL extensions) |

#### Performance Issues with High Cardinality Indexes

* **B-Tree Index**:

  * Height of the tree increases with unique values.
  * Insertions may trigger page splits and rebalancing.
  * Leaf nodes may have poor cache locality.

* **Hash Index**:

  * Each unique value maps to a different slot.
  * Very high cardinality leads to large hash tables, reducing in-memory performance.
  * Not suitable for range queries.

* **Bitmap Index**:

  * Inefficient when there are many distinct values because each unique value adds a new bitmap.
  * Performance and size degrade drastically.

---

## 4. High Cardinality and Selectivity

**Selectivity** refers to the ratio of the number of rows returned by a query to the total number of rows in a table.

* High cardinality ≠ High selectivity.

  * Example: A timestamp column may have many distinct values, but querying for a specific timestamp often returns very few rows.
* Indexes are most efficient when selectivity is high for actual queries.

---

## 5. Indexing Overhead with High Cardinality Columns

| Impact Area         | Description                                                                                                                    |
| ------------------- | ------------------------------------------------------------------------------------------------------------------------------ |
| Write Amplification | Each insert or update causes index maintenance. With high-cardinality, this leads to excessive page movements and rebalancing. |
| Disk I/O            | B-Trees or hash maps grow large and span multiple pages. This increases disk seeks.                                            |
| Cache Inefficiency  | Frequent changes to unique entries result in cache misses and invalidations.                                                   |
| Query Optimization  | Query planner may ignore high-cardinality indexes for low-selectivity filters.                                                 |

---

## 6. Use Cases Where High Cardinality is Acceptable

| Use Case               | Description                                                                                            |
| ---------------------- | ------------------------------------------------------------------------------------------------------ |
| Surrogate Keys         | `uuid` fields used as stable identifiers; indexed and used as PKs where natural keys are absent.       |
| Analytics or Logging   | High-cardinality columns (e.g., timestamps, request IDs) stored but not indexed; used for aggregation. |
| Sharding Keys in NoSQL | Sometimes needed for even data distribution (e.g., hashing a `user_id`).                               |

---

## 7. Summary

* High cardinality refers to columns with many distinct values.
* It is supported in both RDBMS and NoSQL systems, but performance varies based on usage.
* Indexing high-cardinality columns is often expensive due to overhead in structure maintenance and low cache efficiency.
* Such columns should only be indexed if they are:

  * Frequently used in queries
  * Stable
  * Have high selectivity

---