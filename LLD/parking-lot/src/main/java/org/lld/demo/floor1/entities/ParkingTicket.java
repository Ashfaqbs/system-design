package org.lld.demo.floor1.entities;

import java.time.LocalDateTime;

public class ParkingTicket {
    private static int ticketCounter = 1;
    private int ticketId;
    private Vehicle vehicle;
    private ParkingSpot assignedSpot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public ParkingTicket(Vehicle vehicle, ParkingSpot assignedSpot) {
        this.ticketId = ticketCounter++;
        this.vehicle = vehicle;
        this.assignedSpot = assignedSpot;
        this.entryTime = LocalDateTime.now();
    }

    public int getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSpot getAssignedSpot() { return assignedSpot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }

    public void markExitTime() {
        this.exitTime = LocalDateTime.now();
    }
}