### **1. Rough Flow**

```
[Entry Gate]  →  [Parking Spot Allocation]  →  [Exit Gate]
```

* **Entry Gate:** Vehicles enter and are registered.
* **Spot Allocation:** Based on vehicle type and availability.
* **Exit Gate:** Payment is calculated and vehicle exits.

---

### **2. Gather Requirements/Clarifications**

#### a. Vehicle Types Supported
- For now

* **Two-Wheeler**
* **Four-Wheeler**

> ✅ Code should allow **easy addition** of more vehicle types in the future (e.g., trucks, EVs, etc.)

#### b. Payment Strategy

* **Billing Unit:** Per minute
* **Rates:**
- Assume
  * Two-Wheeler → ₹4/minute
  * Four-Wheeler → ₹10/minute

> ✅ Billing system should be **vehicle-type aware** and **dynamic**, i.e., rates may change or new pricing strategies (e.g., per hour, slabs, etc.) can be added.

#### c. Entry/Exit Gates
- For now

* **Number of Entrances:** 1
* **Number of Exits:** 1

> ✅ System should support **multiple gates** (entry/exit) in future if needed.


---

### **Code Objects Involved**

#### **1. `Vehicle ` (Enum)**
* `licenseNumber`
* `Vehicle_Type` (Enum) : 
  *  `TWO_WHEELER`
  * `FOUR_WHEELER`

---

#### **2. `Ticket`**

* `entryTime: LocalDateTime`
* `parkingSpot: ParkingSpot`

---

#### **3. `EntranceGate`**

**Responsibilities/Functions:**

* `findParkingSlot(vehicleType: VehicleType): ParkingSpot`
* `updateParkingSpace(parkingSpot: ParkingSpot): void`
* `generateTicket(parkingSpot: ParkingSpot, vehicle: Vehicle): Ticket`

---

#### **4. `ParkingSpot`**

**Attributes:**

* `id: String`
* `isEmpty: boolean`
* `vehiclePrice: int` *(₹ per minute, based on vehicle type)*
* `type: VehicleType`
* `vehicle: Vehicle` *(reference to the parked vehicle)*

---

#### **5. `ExitGate`**

**Responsibilities/Functions:**

* `costCalculation(ticket: Ticket): int`
* `payment(amount: int): boolean`
* `updateParkingSpot(parkingSpot: ParkingSpot): void` *(Free up the spot)*