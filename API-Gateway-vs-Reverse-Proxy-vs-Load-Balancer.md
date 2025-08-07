## 🧭 Overview and Analogy

### 1. API Gateway

**What it is**:  
An **API Gateway** is like the **front desk of a hotel**. When a guest walks in (client request), the front desk knows exactly where to send them (which backend service). It also checks their identity (auth), gives them access cards (tokens), and logs their entry (metrics/logs).

**Where it’s used**:  
Mostly in **microservices** systems where multiple services work together. It acts as the single entry point for client apps (like mobile or frontend).

**When to use**:  
- You want a **single point** of contact for all APIs.
- You need **authentication**, **rate limiting**, **logging**, **request transformation**, etc.
- You work with **multiple backend services** (e.g., orders, payments, users).

---

### 2. Reverse Proxy

**What it is**:  
A **Reverse Proxy** is like a **concierge who takes requests from guests and fetches them what they need**, without letting them talk directly to the staff. The guest doesn't even know who exactly is serving them.

**Where it’s used**:  
- To **hide internal services** (for security).
- To **serve static content** (like images, HTML).
- To enable **SSL termination** (handle HTTPS).

**When to use**:  
- You need to **shield backend servers**.
- You want to **route traffic internally** based on URL or headers.
- You’re exposing **few or no APIs**, mainly web servers or services.

---

### 3. Load Balancer

**What it is**:  
A **Load Balancer** is like the **host at a restaurant** who checks how busy each waiter is and assigns a new table accordingly. The idea is to **distribute guests evenly** so no one is overworked.

**Where it’s used**:  
- To **distribute load** among multiple instances of a service.
- To improve **high availability and fault tolerance**.

**When to use**:  
- You have **multiple servers** serving the same app or service.
- You want to **avoid overloading** any single machine.

---

## 🔧 Tool Examples and Configs

---

### 🔹 API Gateway: Spring Cloud Gateway (v3.1.9)

**Scenario**: A single entry for microservices — `/orders`, `/users`.

**Minimum working config**:

**Gradle build**:
```groovy
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-gateway'
}
```

**`application.yml`**:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: order-service
          uri: http://localhost:8081
          predicates:
            - Path=/orders/**
        - id: user-service
          uri: http://localhost:8082
          predicates:
            - Path=/users/**
```

**Use-case**:
- Central point for auth
- Logs all requests
- Rate limits if needed

---

### 🔹 Reverse Proxy: Nginx (v1.24)

#### 📦 Non-K8s Setup

**Scenario**: Proxy to backend app running on port `3000`.

**`nginx.conf`**:
```nginx
http {
  server {
    listen 80;
    location / {
      proxy_pass http://localhost:3000;
      proxy_set_header Host $host;
      proxy_set_header X-Real-IP $remote_addr;
    }
  }
}
```

**Use-case**:
- Shields app from direct access
- Handles HTTPS externally
- Can add caching, compression

---

#### ☸️ K8s Setup

Use **Nginx Ingress Controller** as reverse proxy:

**Ingress YAML**:
```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: example-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: myapp.local
    http:
      paths:
      - path: /app
        pathType: Prefix
        backend:
          service:
            name: my-app-service
            port:
              number: 80
```

**Use-case**:
- Routes incoming requests to internal services
- Enables SSL
- Centralized routing

---

### 🔹 Load Balancer: HAProxy (v2.8)

#### 📦 Non-K8s Setup

**Scenario**: Load balance between 2 app servers (`8081`, `8082`)

**`haproxy.cfg`**:
```haproxy
frontend http_front
    bind *:80
    default_backend app_servers

backend app_servers
    balance roundrobin
    server app1 127.0.0.1:8081 check
    server app2 127.0.0.1:8082 check
```

**Use-case**:
- Spreads load across app servers
- Detects unhealthy servers
- Can handle stickiness (same client goes to same server)

---

#### ☸️ K8s Setup

K8s Service with `type: LoadBalancer`:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  type: LoadBalancer
  selector:
    app: my-app
  ports:
    - port: 80
      targetPort: 8080
```

**Use-case**:
- Lets cloud provider expose service via external IP
- Load balances across pod replicas

---

## 🧾 Summary Table

| Feature          | API Gateway                  | Reverse Proxy              | Load Balancer              |
|------------------|------------------------------|-----------------------------|-----------------------------|
| Primary Role     | Route, auth, transform       | Hide backend, route         | Distribute traffic          |
| External Facing  | Yes                          | Yes                         | Sometimes (mostly internal) |
| Service Mapping  | Fine-grained (paths, headers)| Moderate (URL based)        | Broad (just spread load)    |
| Best Tool        | Spring Gateway               | Nginx                       | HAProxy / K8s LB            |
| Complex Routing  | ✅                            | ✅                           | ❌                          |
| Load Distribution| ❌                            | ❌                           | ✅                          |
| K8s Ready        | ✅ (via Ingress or Gateway)  | ✅ (Ingress)                | ✅ (Service LB)             |

---

## 📘 Extra Notes

- In **microservices**, **API Gateway** usually comes before everything else.
- In **traditional monoliths**, **Reverse Proxy + Load Balancer** are often enough.
- In **Kubernetes**, many setups combine **Ingress (reverse proxy)** + **Service (load balancer)**.