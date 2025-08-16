package org.lld.demo.floorN;

public class EntranceGateN {
    private final ParkingLotN lot;
    public EntranceGateN(ParkingLotN lot) { this.lot = lot; }

    public ParkingTicketN admit(VehicleN vehicle) {
        System.out.println("ENTRY: Vehicle " + vehicle.getLicensePlate() + " (" + vehicle.getType() + ")");
        ParkingSpotN spot = lot.findSpot(vehicle.getType());
        if (spot == null) {
            System.out.println("No spot available for type: " + vehicle.getType() + "\n");
            return null;
        }
        spot.park(vehicle);
        ParkingTicketN ticket = new ParkingTicketN(vehicle, spot);
        System.out.println("Assigned Spot: " + spot.getSpotId() + " | Ticket#" + ticket.getTicketId() + "\n");
        return ticket;
    }
}