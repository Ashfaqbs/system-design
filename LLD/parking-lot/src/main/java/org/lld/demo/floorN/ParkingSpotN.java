package org.lld.demo.floorN;

public class ParkingSpotN {
    private final String spotId;                 // e.g., F1-TW-1, F2-FW-3
    private final VehicleTypeN allowedType;       // TWO_WHEELER or FOUR_WHEELER
    private boolean available = true;
    private VehicleN parkedVehicle = null;

    public ParkingSpotN(String spotId, VehicleTypeN allowedType) {
        this.spotId = spotId;
        this.allowedType = allowedType;
    }
    public String getSpotId() { return spotId; }
    public VehicleTypeN getAllowedType() { return allowedType; }
    public boolean isAvailable() { return available; }
    public VehicleN getParkedVehicle() { return parkedVehicle; }

    public void park(VehicleN vehicle) {
        this.parkedVehicle = vehicle;
        this.available = false;
    }
    public void free() {
        this.parkedVehicle = null;
        this.available = true;
    }
}
