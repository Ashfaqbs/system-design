# Load Balancing: Concepts, Types, Algorithms, Tools

---

## What Is Load Balancing?

Load balancing is the technique of **distributing incoming network traffic or workload across multiple servers** to ensure that no single server becomes overwhelmed. It helps improve:

* Performance (by parallelizing load)
* Availability (by avoiding single points of failure)
* Scalability (by supporting horizontal growth)

It acts as a **traffic manager** that sits in front of a group of servers (often called a server pool or backend pool).

---

## Why Load Balancing Is Needed

* A single server may not handle high volumes of concurrent requests.
* Server failure should not cause downtime.
* Application must maintain consistent performance during traffic spikes.
* Dynamic scaling (up/down) requires an intelligent distribution mechanism.

---

## Benefits of Load Balancing

| Benefit               | Description                                                                |
| --------------------- | -------------------------------------------------------------------------- |
| **High Availability** | Redirects traffic away from failed nodes                                   |
| **Scalability**       | Supports adding/removing backend servers                                   |
| **Efficiency**        | Balances CPU/memory usage across multiple instances                        |
| **Maintainability**   | Enables zero-downtime deployments by routing traffic during server updates |

---

## Types of Load Balancers

### 1. **Layer 4 Load Balancer (Transport Layer)**

* Works on TCP/UDP level (based on IP, port)
* No understanding of HTTP or content
* Very fast and efficient

**Use case:** Games, video streaming, low-latency systems, database proxies

### 2. **Layer 7 Load Balancer (Application Layer)**

* Works on HTTP/HTTPS level
* Can route based on URLs, cookies, headers, query params
* Supports smart routing, SSL termination, content-based routing

**Use case:** Web applications, microservices, REST APIs, frontend-backend separation

---

## Load Balancing Algorithms

### 1. **Round Robin**

* Requests are sent to servers in order, one after another.
* Simple, but does not account for server load.

### 2. **Least Connections**

* Sends traffic to the server with the fewest active connections.
* Better for long-lived connections.

### 3. **Weighted Round Robin / Least Connections**

* Allows assigning weights to servers (e.g., a powerful server gets more traffic).

### 4. **IP Hash / Source Hash**

* Routes requests based on a hash of the client’s IP.
* Useful for session stickiness (same user always hits the same server).

### 5. **Consistent Hashing**

* A hashing technique used for routing based on keys (e.g., user ID, session ID).
* Used in systems where servers/nodes are frequently added/removed.
* Popular in distributed caches and databases (e.g., Redis, Cassandra).

**Key idea:** Only a small portion of keys need to move when nodes change.

---

## Session Persistence (Sticky Sessions)

Some applications need the same client to always be routed to the same server (e.g., for login sessions stored in memory). This is achieved using:

* IP hashing
* Cookies
* Session Affinity (set by the load balancer)

**Trade-off:** Reduces load distribution effectiveness but needed in some legacy or stateful systems.

---

## Load Balancing Tools and Technologies

| Tool/Service           | Type               | Notes                                                    |
| ---------------------- | ------------------ | -------------------------------------------------------- |
| **Nginx**              | L4 + L7            | Lightweight, flexible, supports reverse proxy and SSL    |
| **HAProxy**            | L4 + L7            | High-performance, widely used in production environments |
| **AWS ELB/ALB/NLB**    | L4 (NLB), L7 (ALB) | Managed cloud load balancers                             |
| **Kubernetes Ingress** | L7                 | Controls external access to services in a K8s cluster    |
| **Envoy Proxy**        | L7                 | Modern service proxy with observability features         |
| **Traefik**            | L7                 | Dynamic reverse proxy for microservices                  |
| **F5 / Citrix ADC**    | L4 + L7 (Hardware) | Enterprise-grade hardware load balancers                 |

---

## When to Choose Which Load Balancer

| Scenario                                       | Recommended Load Balancer       |
| ---------------------------------------------- | ------------------------------- |
| Simple static websites                         | Nginx, Traefik                  |
| Enterprise applications with strict SLAs       | HAProxy, F5                     |
| Cloud-native systems                           | AWS ALB/NLB, GCP Load Balancer  |
| Container-based apps (Kubernetes)              | Kubernetes Ingress + Envoy      |
| Stateful sessions needing affinity             | IP Hash or Cookie-based routing |
| Systems with dynamic node sets (e.g., caching) | Consistent Hashing              |

---

## Key Design Considerations

* **Health Checks**: Periodically ping backend servers to avoid routing to failed instances.
* **SSL Termination**: Offload SSL processing to the load balancer to reduce app server load.
* **Failover**: Ensure traffic switches to healthy nodes automatically.
* **Rate Limiting**: Protect backends from abuse by controlling traffic volume.
* **Logging/Monitoring**: Essential for diagnosing issues and tracking traffic patterns.

---

## Summary

| Concept            | Description                                            |
| ------------------ | ------------------------------------------------------ |
| Load Balancing     | Evenly distributes traffic across multiple servers     |
| Layer 4            | Based on IP/Port (TCP/UDP)                             |
| Layer 7            | Based on HTTP/Content (e.g., URL, headers)             |
| Round Robin        | Even rotation, ignores server load                     |
| Least Connections  | Favors less busy servers                               |
| Consistent Hashing | Smart routing that handles node changes efficiently    |
| Sticky Sessions    | Maintains session state by routing client consistently |
| Tools              | Nginx, HAProxy, AWS ALB, Envoy, Kubernetes Ingress     |

