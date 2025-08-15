package org.lld.demo.floor1.entities;

public class EntranceGate {
    private ParkingLot parkingLot;

    public EntranceGate(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    public ParkingTicket allocateSpotAndGenerateTicket(Vehicle vehicle) {
        ParkingSpot availableSpot = parkingLot.getFirstAvailableSpot(vehicle.getVehicleType());
        if (availableSpot == null) {
            System.out.println("No parking spot available for " + vehicle.getVehicleType());
            return null;
        }
        availableSpot.parkVehicle(vehicle);
        ParkingTicket ticket = new ParkingTicket(vehicle, availableSpot);
        System.out.println("Allocated Spot " + availableSpot.getSpotNumber() +
                " to Vehicle " + vehicle.getLicensePlateNumber());
        return ticket;
    }
}