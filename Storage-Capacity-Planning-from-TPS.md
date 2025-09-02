# Storage Capacity Planning from TPS

## 1. Purpose

Storage capacity planning is the process of estimating how much storage an application will require over a period of time, based on workload characteristics such as transaction rates, record sizes, and retention policies. It ensures that storage systems are sized adequately to handle both throughput (I/O rate) and capacity (long-term data growth).

---

## 2. Core Process

### Step 1: Determine Workload Traffic

* **Monthly traffic (requests):** Many systems report traffic as a total number of requests per month.
* **Conversion to TPS:** To size infrastructure, this traffic is converted into an average transactions-per-second (TPS).

  * Formula:

    $$
    TPS = \frac{\text{Total Monthly Requests}}{\text{Seconds in a Month}}
    $$
  * Basis: For simplicity, a month is assumed as 30 days.

    * 30 × 24 × 60 × 60 = **2,592,000 seconds**

### Step 2: Identify the Fraction of Stored Transactions

* Not every request results in persisted data. Some may only read or validate.
* The application used for illustration here is a **Q\&A (question and answer) platform**, similar to a community forum.

  * In such a system:

    * Some users **ask questions**.
    * Some users **answer questions**.
    * Other interactions (e.g., browsing, voting, searching) do not create stored records.
* Example assumption: 20% of traffic is “ask question” requests, and 20% is “answer question” requests. Both require persistent storage.

  * Together this represents a **40% total write fraction** of all requests.
* Effective stored TPS is therefore:

  $$
  T_{eff} = TPS \times \text{Write Fraction}
  $$

### Step 3: Average Record Size

* Each stored transaction consumes disk space.
* The **worked example** is based on the Q\&A platform described above.

  * Average size of a stored **question** or **answer** record is assumed to be **200 KB**.
  * This 200 KB estimate includes:

    * The text body (question or answer).
    * Associated metadata (user info, timestamps, tags).
  * Basis: Such values are typically obtained from schema design (field sizes), inspecting sample payloads, or running benchmark tests with representative data.

### Step 4: Write Bandwidth

* Write bandwidth describes the data ingestion rate per second.

  * Formula:

    $$
    \text{MB/s} = T_{eff} \times \frac{\text{Record Size (KB)}}{1000}
    $$

### Step 5: Data Growth Over Time

* Daily Growth:

  $$
  \text{GB/day} = \text{MB/s} \times 86,400 / 1000
  $$

  Basis: 86,400 seconds in a day.
* Monthly Growth:

  $$
  \text{TB/month} = \text{GB/day} \times 30 / 1000
  $$
* Yearly Growth:

  $$
  \text{TB/year} = \text{GB/day} \times 365 / 1000
  $$

### Step 6: Real-World Multipliers

Raw storage estimates are adjusted with system-level multipliers:

* **Replication (R):** Accounts for redundancy in storage systems (e.g., primary + replica, Kafka RF=3).
* **Index/Metadata Overhead (I):** Extra space for database indexes or inverted indices.
* **Write Amplification (A):** Additional space usage from write-ahead logs or compaction.
* **Compression (C):** Reduction factor if data is compressed (e.g., JSON compresses to 0.5–0.7 of original).
* Final capacity:

  $$
  \text{TB/year(real)} = \text{TB/year(raw)} \times R \times I \times A \times C
  $$

---

## 3. Worked Example: Q\&A Application

### Application Description

Consider a Q\&A platform similar to community forums. It processes \~135 million requests per month.

* 20% of the requests are **asking questions** (stored).
* 20% are **answering questions** (stored).
* Other requests such as browsing or voting do not require storage.
* Average stored record size (either question or answer) is **200 KB**.

### Calculation

1. **Convert to TPS**

   * Requests/month = 135,000,000
   * Seconds/month = 2,592,000
   * TPS = 135,000,000 ÷ 2,592,000 ≈ **52.08 TPS**

2. **Effective Stored TPS**

   * Write fraction = 40% (20% ask + 20% answer)
   * Stored TPS = 52.08 × 0.40 ≈ **20.83 TPS**

3. **Write Bandwidth**

   * Stored TPS × 200 KB/record ÷ 1000 = 20.83 × 200 ÷ 1000 ≈ **4.17 MB/s**

4. **Growth per Day**

   * 4.17 MB/s × 86,400 ÷ 1000 ≈ **360 GB/day**

5. **Growth per Year**

   * 360 GB/day × 365 ÷ 1000 ≈ **131 TB/year (raw data)**

6. **Adjusted Storage with Multipliers**
   Example assumptions:

   * Replication = 3
   * Index overhead = 1.3
   * Write amplification = 1.2
   * Compression ratio = 0.6
   * Total factor = 3 × 1.3 × 1.2 × 0.6 ≈ 2.8
   * Adjusted yearly storage = 131 TB × 2.8 ≈ **367 TB/year**

---

## 4. Interpretation

* Raw data estimates give the baseline growth purely from traffic and record size.
* Adjusted estimates represent actual storage consumption once system factors like replication and indexing are included.
* Retention policies (e.g., keeping 90 days in hot storage, archiving older data to object storage) further influence how much storage is required in different storage tiers.
* Peak traffic should also be considered to size I/O throughput capacity.

---

## 5. Terminology

* **Capacity Planning:** Forecasting compute, memory, network, and storage requirements.
* **Storage Capacity Planning:** The subset focusing specifically on storage.
* **System Sizing:** Informal term for estimating how much resource is needed.
* **Data Growth Estimation:** Long-term projection of data volume increase.

---

## 6. Summary

To calculate storage capacity needs for an application:

1. Convert monthly traffic into TPS.
2. Determine the fraction of requests that create stored records.
3. Multiply stored TPS by average record size to compute ingestion rate.
4. Project growth over days, months, and years.
5. Apply multipliers for replication, indexing, write amplification, and compression.
6. Adjust further based on retention policies and peak workload expectations.

The Q\&A application example demonstrates how 135 million requests/month translates into approximately 131 TB/year of raw storage growth, and about 367 TB/year once system multipliers are included. This methodology shows the direct link between TPS and long-term storage requirements.

---