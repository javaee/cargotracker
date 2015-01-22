package net.java.cargotracker.domain.model.handling;

import net.java.cargotracker.domain.model.cargo.TrackingId;

public interface HandlingEventRepository {

    void store(HandlingEvent event);

    HandlingHistory lookupHandlingHistoryOfCargo(TrackingId trackingId);
}
