package org.lld.demo.floorN;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingTicketN {
    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    private final int ticketId = COUNTER.getAndIncrement();
    private final VehicleN vehicle;
    private final ParkingSpotN spot;
    private final LocalDateTime entryTime = LocalDateTime.now();
    private LocalDateTime exitTime;
    private TicketStatusN status = TicketStatusN.OPEN;

    public ParkingTicketN(VehicleN vehicle, ParkingSpotN spot) {
        this.vehicle = vehicle;
        this.spot = spot;
    }
    public int getTicketId() { return ticketId; }
    public VehicleN getVehicle() { return vehicle; }
    public ParkingSpotN getSpot() { return spot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public TicketStatusN getStatus() { return status; }

    public void close() {
        this.exitTime = LocalDateTime.now();
        this.status = TicketStatusN.CLOSED;
    }
}