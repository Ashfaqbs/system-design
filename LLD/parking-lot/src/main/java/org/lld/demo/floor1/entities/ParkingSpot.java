package org.lld.demo.floor1.entities;

public class ParkingSpot {
    private int spotNumber;
    private VehicleType allowedVehicleType;
    private boolean isAvailable;
    private Vehicle parkedVehicle;

    public ParkingSpot(int spotNumber, VehicleType allowedVehicleType) {
        this.spotNumber = spotNumber;
        this.allowedVehicleType = allowedVehicleType;
        this.isAvailable = true;
    }

    public boolean isAvailable() { return isAvailable; }
    public VehicleType getAllowedVehicleType() { return allowedVehicleType; }
    public int getSpotNumber() { return spotNumber; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }

    public void parkVehicle(Vehicle vehicle) {
        this.parkedVehicle = vehicle;
        this.isAvailable = false;
    }

    public void freeSpot() {
        this.parkedVehicle = null;
        this.isAvailable = true;
    }
}








