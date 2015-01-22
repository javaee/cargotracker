package net.java.cargotracker.application;

import java.util.Date;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.CannotCreateHandlingEventException;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.location.UnLocode;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;

public interface HandlingEventService {

    /**
     * Registers a handling event in the system, and notifies interested parties
     * that a cargo has been handled.
     */
    void registerHandlingEvent(Date completionTime,
            TrackingId trackingId,
            VoyageNumber voyageNumber,
            UnLocode unLocode,
            HandlingEvent.Type type) throws CannotCreateHandlingEventException;
}
