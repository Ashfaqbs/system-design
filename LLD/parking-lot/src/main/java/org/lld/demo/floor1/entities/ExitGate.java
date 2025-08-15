package org.lld.demo.floor1.entities;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ExitGate {
    private ParkingLot parkingLot;
    private Map<VehicleType, Integer> ratePerMinute;

    public ExitGate(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        ratePerMinute = new HashMap<>();
        ratePerMinute.put(VehicleType.TWO_WHEELER, 5);
        ratePerMinute.put(VehicleType.FOUR_WHEELER, 10);
    }

    public void processVehicleExit(ParkingTicket ticket) {
        ticket.markExitTime();
        long minutesParked = Duration.between(ticket.getEntryTime(), ticket.getExitTime()).toMinutes();
        if (minutesParked == 0) minutesParked = 1; // Minimum charge 1 min

        int pricePerMinute = ratePerMinute.get(ticket.getVehicle().getVehicleType());
        int totalCost = (int) minutesParked * pricePerMinute;

        System.out.println("Vehicle " + ticket.getVehicle().getLicensePlateNumber() +
                " parked for " + minutesParked + " minutes.");
        System.out.println("Parking Charges: ₹" + totalCost);
        System.out.println("Payment successful. Thank you!");

        parkingLot.releaseSpot(ticket.getAssignedSpot());
        System.out.println("Spot " + ticket.getAssignedSpot().getSpotNumber() + " is now available.");
    }
}
