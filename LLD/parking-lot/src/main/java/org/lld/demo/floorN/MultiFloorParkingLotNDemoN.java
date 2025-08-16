package org.lld.demo.floorN;

import java.util.Arrays;
import java.util.List;

// -------------------- Demo (3 floors, 10 spots each: 5×2W + 5×4W) --------------------

public class MultiFloorParkingLotNDemoN {
    public static void main(String[] args) throws Exception {
        // Floor-1 spots (explicit, no loops)
        List<ParkingSpotN> f1 = Arrays.asList(
            new ParkingSpotN("F1-TW-1", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F1-TW-2", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F1-TW-3", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F1-TW-4", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F1-TW-5", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F1-FW-1", VehicleTypeN.FOUR_WHEELER),
            new ParkingSpotN("F1-FW-2", VehicleTypeN.FOUR_WHEELER),
            new ParkingSpotN("F1-FW-3", VehicleTypeN.FOUR_WHEELER),
            new ParkingSpotN("F1-FW-4", VehicleTypeN.FOUR_WHEELER),
            new ParkingSpotN("F1-FW-5", VehicleTypeN.FOUR_WHEELER)
        );
        ParkingFloorN floor1 = new ParkingFloorN("Floor-1", f1);

        // Floor-2 spots (explicit)
        List<ParkingSpotN> f2 = Arrays.asList(
            new ParkingSpotN("F2-TW-1", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F2-TW-2", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F2-TW-3", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F2-TW-4", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F2-TW-5", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F2-FW-1", VehicleTypeN.FOUR_WHEELER),
            new ParkingSpotN("F2-FW-2", VehicleTypeN.FOUR_WHEELER),
            new ParkingSpotN("F2-FW-3", VehicleTypeN.FOUR_WHEELER),
            new ParkingSpotN("F2-FW-4", VehicleTypeN.FOUR_WHEELER),
            new ParkingSpotN("F2-FW-5", VehicleTypeN.FOUR_WHEELER)
        );
        ParkingFloorN floor2 = new ParkingFloorN("Floor-2", f2);

        // Floor-3 spots (explicit)
        List<ParkingSpotN> f3 = Arrays.asList(
            new ParkingSpotN("F3-TW-1", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F3-TW-2", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F3-TW-3", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F3-TW-4", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F3-TW-5", VehicleTypeN.TWO_WHEELER),
            new ParkingSpotN("F3-FW-1", VehicleTypeN.FOUR_WHEELER),
            new ParkingSpotN("F3-FW-2", VehicleTypeN.FOUR_WHEELER),
            new ParkingSpotN("F3-FW-3", VehicleTypeN.FOUR_WHEELER),
            new ParkingSpotN("F3-FW-4", VehicleTypeN.FOUR_WHEELER),
            new ParkingSpotN("F3-FW-5", VehicleTypeN.FOUR_WHEELER)
        );
        ParkingFloorN floor3 = new ParkingFloorN("Floor-3", f3);

        ParkingLotN lot = new ParkingLotN("MainLot", Arrays.asList(floor1, floor2, floor3));
        EntranceGateN entry = new EntranceGateN(lot);
        ExitGateN exit = new ExitGateN(lot, new PerMinutePaymentStrategyN());

        lot.printStatus();

        // Entries
        ParkingTicketN t1 = entry.admit(new VehicleN("KA-01-A1111", VehicleTypeN.TWO_WHEELER));
        ParkingTicketN t2 = entry.admit(new VehicleN("KA-02-C2222", VehicleTypeN.FOUR_WHEELER));
        ParkingTicketN t3 = entry.admit(new VehicleN("KA-03-B3333", VehicleTypeN.TWO_WHEELER));

        lot.printStatus();

        // Simulate time passage (keep it small; billing has min 1 min anyway)
        Thread.sleep(1500);

        // Exits
        exit.checkout(t1);
        exit.checkout(t2);

        lot.printStatus();

        // Try parking more vehicles
        ParkingTicketN t4 = entry.admit(new VehicleN("KA-04-C4444", VehicleTypeN.FOUR_WHEELER));
        ParkingTicketN t5 = entry.admit(new VehicleN("KA-05-B5555", VehicleTypeN.TWO_WHEELER));

        lot.printStatus();

        // Close remaining
        Thread.sleep(1200);
        exit.checkout(t3);
        exit.checkout(t4);
        exit.checkout(t5);

        lot.printStatus();
    }
}