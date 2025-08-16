package org.lld.demo.floorN;

import java.util.List;

public class ParkingLotN {
    private final String lotName;
    private final List<ParkingFloorN> floors;

    public ParkingLotN(String lotName, List<ParkingFloorN> floors) {
        this.lotName = lotName;
        this.floors = floors;
    }

    public ParkingSpotN findSpot(VehicleTypeN type) {
        for (ParkingFloorN f : floors) {
            ParkingSpotN s = f.findFirstAvailable(type);
            if (s != null) return s;
        }
        return null;
    }

    public void release(ParkingSpotN spot) { spot.free(); }

    public void printStatus() {
        System.out.println("== " + lotName + " Status ==");
        for (ParkingFloorN f : floors) {
            long free2 = f.getSpots().stream().filter(s -> s.getAllowedType()==VehicleTypeN.TWO_WHEELER && s.isAvailable()).count();
            long free4 = f.getSpots().stream().filter(s -> s.getAllowedType()==VehicleTypeN.FOUR_WHEELER && s.isAvailable()).count();
            System.out.println(f.getFloorName() + " -> Free(2W): " + free2 + ", Free(4W): " + free4);
        }
        System.out.println();
    }
}