package net.java.cargotracker.interfaces.booking.facade;

import java.util.Date;
import java.util.List;
import net.java.cargotracker.interfaces.booking.facade.dto.CargoRoute;
import net.java.cargotracker.interfaces.booking.facade.dto.Location;
import net.java.cargotracker.interfaces.booking.facade.dto.RouteCandidate;

/**
 * This facade shields the domain layer - model, services, repositories - from
 * concerns about such things as the user interface and remoting.
 */
public interface BookingServiceFacade {

    String bookNewCargo(String origin, String destination, Date arrivalDeadline);

    CargoRoute loadCargoForRouting(String trackingId);

    void assignCargoToRoute(String trackingId, RouteCandidate route);

    void changeDestination(String trackingId, String destinationUnLocode);

    List<RouteCandidate> requestPossibleRoutesForCargo(String trackingId);

    List<Location> listShippingLocations();

    List<CargoRoute> listAllCargos();
}
