package org.lld.demo.floorN;

import java.util.List;

public class ParkingFloorN {
    private final String floorName;              // Floor-1, Floor-2, Floor-3
    private final List<ParkingSpotN> spots;

    public ParkingFloorN(String floorName, List<ParkingSpotN> spots) {
        this.floorName = floorName;
        this.spots = spots;
    }
    public String getFloorName() { return floorName; }
    public List<ParkingSpotN> getSpots() { return spots; }

    public ParkingSpotN findFirstAvailable(VehicleTypeN type) {
        for (ParkingSpotN s : spots) {
            if (s.getAllowedType() == type && s.isAvailable()) return s;
        }
        return null;
    }
}