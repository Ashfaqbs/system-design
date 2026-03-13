package org.lld;

public class Request {
    int floorNumber;
    Direction direction; // only for external requests
    boolean isExternal; // true = external, false = internal

    Request(int floorNumber, Direction direction, boolean isExternal) {
        this.floorNumber = floorNumber;
        this.direction = direction;
        this.isExternal = isExternal;
    }
}