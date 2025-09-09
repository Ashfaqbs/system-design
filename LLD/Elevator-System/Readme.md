# 🔍 LOOK Algorithm vs SCAN Algorithm

---

## 1. SCAN (Elevator Algorithm)

* The disk arm (elevator) moves in one direction (say upward).
* It serves all requests in its path.
* It **keeps going until the very end** of the disk (top floor), even if no one is waiting there.
* Then it reverses direction and does the same.

👉 Problem: Sometimes it goes all the way to the edge **unnecessarily**.

---

## 2. What is LOOK Algorithm?

* LOOK is like a **smarter version of SCAN**.
* Instead of always going to the very end (last track), the arm **only goes as far as the last request in that direction**.
* Then it **reverses immediately**, without wasting movement.

---

## 3. Elevator Analogy

* SCAN = Elevator always goes to the **top floor** and then comes down, even if nobody pressed the button for the top floor.
* LOOK = Elevator goes **only up to the highest requested floor** (say floor 12), then turns around.
* Same on the downward journey: it goes only as far as the lowest requested floor.

👉 LOOK “looks ahead” to see where the last request is, and stops there. That’s why it’s called **LOOK**.

---

## 4. Example Simulation (with LOOK)

### Setup

* Disk tracks = 0 to 199
* Current arm at **50**
* Direction = outward
* Requests = \[82, 170, 43, 140, 24, 16, 190]

---

### Step 1: Sort Requests

* Ahead: \[82, 140, 170, 190]
* Behind: \[43, 24, 16]

---

### Step 2: Move Outward (but stop at farthest request, not 199)

* Path: 50 → 82 → 140 → 170 → 190 (**stop here**)

### Step 3: Reverse Direction

* Path: 190 → 43 → 24 → 16

---

### Final Order

**50 → 82 → 140 → 170 → 190 → 43 → 24 → 16**

👉 Notice: In SCAN, we forced the arm to go all the way to **199** before reversing.
👉 In LOOK, we **stopped at 190**, because that was the farthest request.

---

## 5. Observations

* **LOOK saves time** by avoiding useless travel.
* It is often considered more **efficient** than SCAN.
* Both ensure **fairness** (all requests get served in order).
* LOOK is used in practice more often than pure SCAN.

---

✅ **Final Analogy Recap**

* SCAN = Elevator always goes to the **top floor**, even if no one requested it.
* LOOK = Elevator only goes as far as the **highest floor where someone pressed a button**, then turns back.

---