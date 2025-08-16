package org.lld.demo.floorN;

public class ExitGateN {
    private final ParkingLotN lot;
    private final PaymentStrategyN paymentStrategy;

    public ExitGateN(ParkingLotN lot, PaymentStrategyN paymentStrategy) {
        this.lot = lot;
        this.paymentStrategy = paymentStrategy;
    }

    public void checkout(ParkingTicketN ticket) {
        if (ticket == null || ticket.getStatus() == TicketStatusN.CLOSED) {
            System.out.println("Invalid or already closed ticket.\n");
            return;
        }
        int amount = paymentStrategy.calculateAmount(ticket);
        ticket.close(); // mark exit time and close
        // mock payment
        System.out.println("EXIT: Vehicle " + ticket.getVehicle().getLicensePlate()
                + " | Parked Spot " + ticket.getSpot().getSpotId());
        System.out.println("Amount Due: ₹" + amount + "  [Payment processed]");
        lot.release(ticket.getSpot());
        System.out.println("Spot " + ticket.getSpot().getSpotId() + " is now available.\n");
    }
}