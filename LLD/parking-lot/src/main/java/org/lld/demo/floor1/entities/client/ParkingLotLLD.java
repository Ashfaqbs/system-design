package org.lld.demo.floor1.entities.client;

import org.lld.demo.floor1.entities.*;

import java.util.ArrayList;
import java.util.List;

 public class ParkingLotLLD {
    public static void main(String[] args) throws InterruptedException {
        // Step 1: Create parking spots (3 for bikes, 2 for cars)
        List<ParkingSpot> parkingSpots = new ArrayList<>();
        parkingSpots.add(new ParkingSpot(1, VehicleType.TWO_WHEELER));
        parkingSpots.add(new ParkingSpot(2, VehicleType.TWO_WHEELER));
        parkingSpots.add(new ParkingSpot(3, VehicleType.TWO_WHEELER));
        parkingSpots.add(new ParkingSpot(4, VehicleType.FOUR_WHEELER));
        parkingSpots.add(new ParkingSpot(5, VehicleType.FOUR_WHEELER));

        // Step 2: Create Parking Lot and Gates
        ParkingLot parkingLot = new ParkingLot(parkingSpots);
        EntranceGate entranceGate = new EntranceGate(parkingLot);
        ExitGate exitGate = new ExitGate(parkingLot);

        // Step 3: Vehicle enters
        Vehicle bike = new Vehicle("KA-01-HH-1234", VehicleType.TWO_WHEELER);
        ParkingTicket ticket = entranceGate.allocateSpotAndGenerateTicket(bike);

        // Simulate parking duration
        Thread.sleep(2000);

        // Step 4: Vehicle exits
        if (ticket != null) {
            exitGate.processVehicleExit(ticket);
        }
    }
}