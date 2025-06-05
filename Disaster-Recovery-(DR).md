# **Disaster Recovery (DR) Reference Guide**

### **1. What is Disaster Recovery?**

Disaster Recovery (DR) is a strategy designed to ensure the continuity and recovery of IT systems, data, and services in the event of catastrophic events such as natural disasters, cyberattacks, or system failures.

The core objective of DR is to minimize downtime and data loss by having predefined mechanisms and infrastructure in place.

---

### **2. DR Configurations**

#### **a. Active-Active Setup**

* **Definition**: Multiple systems or data centers run in parallel, actively handling requests simultaneously.
* **Availability**: High. If one node fails, the others continue without interruption.
* **Cost**: High, due to duplication of full-scale systems.
* **Use Case**: Systems demanding high availability and real-time failover.

#### **b. Active-Passive Setup**

* **Definition**: One system is active while the standby system remains passive until failover is required.
* **Availability**: Moderate. Some downtime expected during switch.
* **Cost**: Lower than active-active, since only one system handles traffic at a time.
* **Use Case**: Systems with some tolerance for recovery delay but requiring cost optimization.

---

### **3. Components of a DR Plan**

* **Recovery Time Objective (RTO)**: The maximum acceptable downtime after a failure.
* **Recovery Point Objective (RPO)**: The maximum acceptable data loss measured in time.
* **Regular DR Testing**: Periodic simulations are necessary to validate the effectiveness of the DR strategy.
* **Documentation**: Clearly define steps, responsibilities, and communication protocols.
* **Security & Access Control**: Ensure data is secure during failover or data recovery operations.
* **Monitoring & Alerting**: Set up mechanisms to detect failures and trigger automated failovers.

---

### **4. Implementation in Kubernetes Environment**

#### **Active-Active**

* **Setup**:

  * Deploy application pods across multiple nodes/zones.
  * Use a Kubernetes LoadBalancer or Ingress to route traffic evenly.
  * Backend: Use distributed database (e.g., Cassandra, CockroachDB) and distributed cache (e.g., Redis Cluster).
  * Messaging: Kafka or RabbitMQ configured in a clustered mode.

#### **Active-Passive**

* **Setup**:

  * Deploy active pods and keep passive pods ready but not serving traffic.
  * Use readiness and liveness probes to monitor health.
  * Configure failover logic with external controllers or using StatefulSets with manual intervention.
  * Standby replicas stay in sync using stateful storage or replication services.

---

### **5. Implementation in Non-Kubernetes Environment**

#### **Active-Active**

* **Setup**:

  * Multiple servers (or VMs) behind a load balancer.
  * Shared state managed using distributed databases and cache.
  * Kafka or RabbitMQ used in clustered or replicated mode.
  * Auto-healing and failover via custom scripts or orchestration platforms.

#### **Active-Passive**

* **Setup**:

  * One primary server handles live traffic.
  * Standby server remains in sync through real-time database replication or file syncing tools.
  * Failover achieved using DNS switch, virtual IPs, or orchestration tools.

---

### **6. Kafka in DR Context**

* **Partitioning**:

  * Each Kafka topic is split into partitions.
  * A partition is the basic unit of parallelism and storage.

* **Replication Factor**:

  * Each partition can be replicated across multiple brokers.
  * A replication factor of 2 means 1 leader + 1 follower replica per partition.

* **Replica Placement**:

  * Kafka uses a round-robin strategy to distribute partition replicas across brokers.
  * Leader is elected, and followers are assigned from available brokers.
  * With rack-awareness enabled, Kafka ensures replicas are spread across different racks (or availability zones).

---



In the context of disaster recovery (DR) for applications deployed across virtual machines (VMs) or Kubernetes (K8s) clusters, two primary architectures are commonly considered: **Active-Active** and **Active-Passive**. These architectures are pivotal in ensuring high availability (HA), resilience, and business continuity.([medium.com][1])

---

## Active-Active Architecture

**Overview**:
In an Active-Active setup, multiple instances of an application are deployed across different regions or zones, all actively serving traffic simultaneously. This configuration ensures that if one instance fails, others continue to operate without interruption.([learn.microsoft.com][2])

**Implementation in VMs**:

* **Deployment**: Applications are deployed on VMs across multiple regions.
* **Load Balancing**: A global load balancer distributes traffic among active instances.
* **Data Synchronization**: Databases and caches require real-time replication to maintain consistency.

**Implementation in Kubernetes**:

* **Clusters**: Multiple Kubernetes clusters are set up in different regions.
* **Traffic Management**: Tools like Azure Front Door or AWS Global Accelerator route traffic to the nearest active cluster.
* **Data Replication**: Stateful components utilize synchronous replication mechanisms to ensure data consistency.([learn.microsoft.com][3], [docs.azure.cn][4], [medium.com][1])

**Pros**:

* High availability with minimal downtime.
* Efficient resource utilization.
* Improved performance through load distribution.

**Cons**:

* Increased complexity in deployment and management.
* Higher costs due to resource duplication.
* Challenges in maintaining data consistency across regions.

---

## Active-Passive Architecture

**Overview**:
An Active-Passive configuration involves a primary active instance handling all traffic, while a secondary passive instance remains on standby, ready to take over in case of failure.([geeksforgeeks.org][5])

**Implementation in VMs**:

* **Deployment**: Primary application runs on VMs in one region; secondary is deployed in another region but remains inactive.
* **Failover Mechanism**: Monitoring tools detect failures and redirect traffic to the passive instance.
* **Data Synchronization**: Regular backups and asynchronous replication ensure data is up-to-date.([learn.microsoft.com][3])

**Implementation in Kubernetes**:

* **Clusters**: Two Kubernetes clusters are set up; only one actively serves traffic.
* **Traffic Management**: Services like Azure Front Door manage traffic routing and failover.
* **Data Replication**: Asynchronous replication methods keep data synchronized between clusters.([learn.microsoft.com][3], [learn.microsoft.com][2], [medium.com][1])

**Pros**:

* Simpler setup and management.
* Cost-effective due to reduced active resource usage.
* Easier to maintain data consistency.([medium.com][6])

**Cons**:

* Potential downtime during failover.
* Underutilization of standby resources.
* Longer recovery times compared to Active-Active setups.([medium.com][6], [blog.purestorage.com][7], [medium.com][1])

---

## Choosing Between Active-Active and Active-Passive

**Active-Active** is suitable when:

* High availability and minimal downtime are critical.
* The application experiences high traffic volumes.
* Resources are available to manage increased complexity and costs.([jscape.com][8])

**Active-Passive** is appropriate when:

* The application can tolerate some downtime.
* Cost constraints limit resource duplication.
* Simpler management and maintenance are preferred.

---

## Considerations for DR in Applications with External Services

In scenarios where databases (e.g., PostgreSQL, MongoDB), caches (e.g., Redis), and messaging queues (e.g., Kafka) are managed by external teams or services, the focus shifts to ensuring that the application layer is resilient and capable of handling failovers.

**Strategies**:

* **Stateless Design**: Design applications to be stateless, allowing for easier scaling and failover.
* **Health Monitoring**: Implement robust health checks and monitoring to detect failures promptly.
* **Automated Failover**: Utilize tools and services that support automated failover to minimize downtime.
* **Regular Testing**: Conduct regular DR drills to ensure failover mechanisms function as intended.

---

By carefully evaluating the application's requirements, resource availability, and acceptable downtime, an appropriate DR strategy—be it Active-Active or Active-Passive—can be selected and implemented to ensure business continuity and resilience.([medium.com][6])


- Reference:
[1]: https://medium.com/%40geethaanne/disaster-recovery-strategies-for-kubernetes-ensuring-business-continuity-da72bcdebcb5 "Disaster Recovery Strategies for Kubernetes: Ensuring Business ..."
[2]: https://learn.microsoft.com/en-us/azure/aks/ha-dr-overview "High availability and disaster recovery overview for Azure ..."
[3]: https://learn.microsoft.com/en-us/azure/aks/active-passive-solution "Active passive disaster recovery solution overview for Azure ..."
[4]: https://docs.azure.cn/en-us/aks/active-active-solution "Recommended active-active high availability solution overview for ..."
[5]: https://www.geeksforgeeks.org/active-active-vs-active-passive-architecture/ "Active Active vs. Active Passive Architecture | GeeksforGeeks"
[6]: https://medium.com/%40bryan_56456/kafka-resilience-strategies-active-active-vs-active-passive-34644f946114 "Kafka Resilience Strategies: Active-Active vs. Active-Passive - Medium"
[7]: https://blog.purestorage.com/purely-educational/active-active-vs-active-passive-decoding-high-availability-configurations-for-massive-data-networks/ "Decoding High-availability Configurations for Massive Data Networks"
[8]: https://www.jscape.com/blog/active-active-vs-active-passive-high-availability-cluster "Active-Active vs. Active-Passive High-Availability Clustering | JSCAPE"
