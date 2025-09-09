# 📖 Document: Disk Scheduling & SCAN (Elevator) Algorithm

---

## 1. The Initial Understanding (Foundation)

Your computer’s **disk** (especially HDD) stores data on **circular paths called tracks**.
To fetch or store data, the disk uses a **disk arm** (like the needle on a record player) with a **read/write head** at its tip.

* The **arm moves across tracks** (like moving across the grooves of a record).
* At any given moment, it can serve **only one track**.
* So if multiple requests arrive, the arm needs a **strategy to decide the order**.

👉 Without order, the arm would zigzag wildly, wasting time.

---

### Analogy: **A Library Elevator**

* Imagine a library with **100 floors** (tracks).
* People (requests) are waiting on different floors.
* The **elevator (disk arm)** can carry one set of people at a time.
* It **moves up and down** through the building, deciding where to stop.
* If it moves randomly (first to floor 5, then to 95, then back to 20), a lot of time is wasted.

This is exactly the problem **disk scheduling algorithms** solve.

---

## 2. Read and Write (What the Arm Actually Does)

* **Read** = Fetching data → like picking up a book from a shelf.
* **Write** = Storing data → like placing a new book on the shelf.

Both require the arm to move to the **right shelf (track)** before the operation happens.

---

## 3. HDD vs SSD (The Big Difference)

### HDD (Hard Disk Drive)

* Data stored on spinning **platters** (like stacked vinyl records).
* One **disk arm with multiple heads** moves together across tracks.
* Only **one head reads/writes at a time**.
* Mechanical → slower, needs smart scheduling.

### Analogy

Think of one **elevator shaft** with several **doors on each floor**.
The elevator positions itself at a floor, and one door opens → that’s the active head reading/writing.

---

### SSD (Solid State Drive)

* No moving parts.
* Data is accessed electronically, like instantly teleporting to any floor.
* No need for elevator-like scheduling.

👉 **Disk scheduling algorithms like SCAN are relevant for HDDs, not SSDs.**

---

## 4. What is Disk Scheduling?

* The **OS receives multiple read/write requests**.
* The **Disk Scheduler** decides the order.
* Goal = reduce wasted movement → improve performance.

Analogy: A **traffic cop** directing cars (requests) so they move efficiently rather than all rushing randomly.

---

## 5. SCAN Algorithm (The Elevator Rule)

* The arm starts at a position.
* It moves in **one direction** (say, outward toward higher track numbers).
* It **serves requests on the way**.
* When it reaches the **end of the disk**, it **reverses direction**.
* Repeats back and forth, like an elevator.

---

### Why Called "Elevator Algorithm"?

* Because it mimics an elevator:

  * Elevator moves up, stopping where people wait.
  * Reaches the top, then comes down, again serving floors.
  * Doesn’t randomly bounce for each call.

👉 This **reduces travel time** and ensures fairness.

---

## 6. Example Simulation of SCAN

### Setup

* Disk tracks = 0 to 199
* Current arm at **50**
* Direction = outward (towards higher numbers)
* Requests = \[82, 170, 43, 140, 24, 16, 190]

---

### Step 1: Divide Requests

* Ahead (≥50): \[82, 140, 170, 190]
* Behind (<50): \[43, 24, 16]

Sort them:

* Ahead: \[82, 140, 170, 190]
* Behind: \[43, 24, 16]

---

### Step 2: Serve Outward First

* Path: 50 → 82 → 140 → 170 → 190 → **199 (end)**

### Step 3: Reverse Direction

* Path: 199 → 43 → 24 → 16

---

### Final Order

**50 → 82 → 140 → 170 → 190 → 199 → 43 → 24 → 16**

---

## 7. Observations

* Arm does **not zigzag** → smoother travel.
* Everyone gets served (fairness).
* Total arm travel is much less than naive methods like FCFS.

---

## 8. Where Is This Used?

* **Operating Systems (Linux, Windows, Unix)**: Classical disk scheduling.
* **Database Systems**: To minimize disk I/O overhead.
* **Storage Controllers**: HDD firmware often implements SCAN-like behavior.

---

✅ **Final Analogy Recap**

* Disk = Building with floors.
* Arm = Elevator car.
* Tracks = Floors.
* Requests = People waiting.
* SCAN Algorithm = Elevator rule: go one way, serve everyone, then reverse.
* HDD = Elevator that must move floor by floor.
* SSD = Teleporter → no elevator needed.

---