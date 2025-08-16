package org.lld.demo.floorN;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PerMinutePaymentStrategyN implements  PaymentStrategyN {
    private final Map<VehicleTypeN, Integer> perMinuteRates = new HashMap<>();

    public PerMinutePaymentStrategyN() {
        perMinuteRates.put(VehicleTypeN.TWO_WHEELER, 5);   // ₹5 / min
        perMinuteRates.put(VehicleTypeN.FOUR_WHEELER, 10); // ₹10 / min
    }

    @Override
    public int calculateAmount(ParkingTicketN ticket) {
        long minutes = Duration.between(ticket.getEntryTime(), LocalDateTime.now()).toMinutes();
        if (minutes <= 0) minutes = 1; // minimum 1 minute billing
        int rate = perMinuteRates.get(ticket.getVehicle().getType());
        return (int) minutes * rate;
    }
}