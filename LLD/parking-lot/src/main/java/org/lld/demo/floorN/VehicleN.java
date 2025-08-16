package org.lld.demo.floorN;

public class VehicleN {
    private final String licensePlate;
    private final VehicleTypeN type;

    public VehicleN(String licensePlate, VehicleTypeN type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }
    public String getLicensePlate() { return licensePlate; }
    public VehicleTypeN getType() { return type; }
}