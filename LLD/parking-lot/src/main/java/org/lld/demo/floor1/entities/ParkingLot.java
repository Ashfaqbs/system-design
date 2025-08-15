package org.lld.demo.floor1.entities;

import java.util.List;

public class ParkingLot {
    private List<ParkingSpot> parkingSpots;

    public ParkingLot(List<ParkingSpot> parkingSpots) {
        this.parkingSpots = parkingSpots;
    }

    public ParkingSpot getFirstAvailableSpot(VehicleType vehicleType) {
        for (ParkingSpot spot : parkingSpots) {
            if (spot.isAvailable() && spot.getAllowedVehicleType() == vehicleType) {
                return spot;
            }
        }
        return null;
    }

    public void releaseSpot(ParkingSpot spot) {
        spot.freeSpot();
    }
}


