package org.lld;

import java.util.LinkedList;
import java.util.Queue;

public class Elevator {
    int id;
    int currentFloor;
    Direction direction;
    ElevatorStatus status;
    Queue<Integer> internalRequests;

    Elevator(int id) {
        this.id = id;
        this.currentFloor = 0; // Default ground floor
        this.direction = Direction.IDLE;// Default
        this.status = ElevatorStatus.ACTIVE; // Default active
        this.internalRequests = new LinkedList<>();
    }

    void moveToFloor(int floor) {
        // logic to simulate movement
        System.out.println("Elevator " + id + " moving to floor " + floor);
        currentFloor = floor;
    }

    void addInternalRequest(int floor) {
        if (status == ElevatorStatus.ACTIVE) {
            internalRequests.add(floor);
        }
    }

    void setMaintenanceMode() {
        status = ElevatorStatus.MAINTENANCE;
        internalRequests.clear();
    }

    void setActiveMode() {
        status = ElevatorStatus.ACTIVE;
    }
}
