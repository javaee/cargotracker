package net.java.cargotracker.interfaces.tracking.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import static net.java.cargotracker.application.util.LocationUtil.getCode;
import static net.java.cargotracker.application.util.LocationUtil.getCoordinatesForLocation;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.CargoRepository;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.handling.HandlingEventRepository;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 * Backing bean for tracking cargo. This interface sits immediately on top of
 * the domain layer, unlike the booking interface which has a facade and
 * supporting DTOs in between.
 * <p/>
 * An adapter class, designed for the tracking use case, is used to wrap the
 * domain model to make it easier to work with in a web page rendering context.
 * We do not want to apply view rendering constraints to the design of our
 * domain model and the adapter helps us shield the domain model classes where
 * needed.
 * <p/>
 * In some very simplistic cases, it is fine to not use even an adapter.
 */
@Named
@ViewScoped
public class Track implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CargoRepository cargoRepository;
    @Inject
    private HandlingEventRepository handlingEventRepository;

    private String trackingId;
    private CargoTrackingViewAdapter cargo;
    private String destinationCoordinates;
    private String lastKnownCoordinates;

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        // TODO See if a more global trimming mechanism is needed.
        if (trackingId != null) {
            trackingId = trackingId.trim();
        }

        this.trackingId = trackingId;
    }

    public CargoTrackingViewAdapter getCargo() {
        return cargo;
    }

    public void setCargo(CargoTrackingViewAdapter cargo) {
        this.cargo = cargo;
    }

    // This is belongs in the view adapter.
    public String getDestinationCoordinates() {
        return destinationCoordinates;
    }

    public String getLastKnownCoordinate() {
        return lastKnownCoordinates;
    }

    public MapModel getMapModel() {
        MapModel mapModel = new DefaultMapModel();

        String origin = getCode(cargo.getOrigin());
        String destination = getCode(cargo.getDestination());
        String lastKnownLocation = "XXXX";

        lastKnownLocation = cargo.getLastKnowLocation().getUnLocode().getIdString();

        if (origin != null && !origin.isEmpty()) {
            mapModel.addOverlay(new Marker(getCoordinatesForLocation(origin), "Origin: " + cargo.getOrigin()));
        }

        if (destination != null && !destination.isEmpty()) {
            mapModel.addOverlay(new Marker(getCoordinatesForLocation(destination), "Final destination: " + cargo.getDestination()));
        }
        if (lastKnownLocation != null && !lastKnownLocation.isEmpty() && !lastKnownLocation.toUpperCase().contains("XXXX")) {
            String lastKnownLocName = cargo.getLastKnowLocation().getName();
            mapModel.addOverlay(new Marker(getCoordinatesForLocation(lastKnownLocation), "Last known location: " + lastKnownLocName));
        }

        return mapModel;
    }

    // The query parameter is required by PrimeFaces but we don't need it.
    public List<String> getTrackingIds(String query) {
        List<TrackingId> trackingIds = cargoRepository.getAllTrackingIds();

        List<String> trackingIdStrings = new ArrayList(trackingIds.size());

        for (TrackingId trackingId : trackingIds) {
            trackingIdStrings.add(trackingId.getIdString());
        }

        return trackingIdStrings;
    }

    public void onTrackById() {
        Cargo cargo = cargoRepository.find(new TrackingId(trackingId));

        if (cargo != null) {
            List<HandlingEvent> handlingEvents = handlingEventRepository
                    .lookupHandlingHistoryOfCargo(new TrackingId(trackingId))
                    .getDistinctEventsByCompletionTime();
            this.cargo = new CargoTrackingViewAdapter(cargo, handlingEvents);
            this.destinationCoordinates = net.java.cargotracker.application.util.LocationUtil.getPortCoordinates(cargo.getRouteSpecification().getDestination().getUnLocode().getIdString());
        } else {
            // TODO See if this can be injected.
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(
                    "Cargo with tracking ID: " + trackingId + " not found.");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(null, message);
            this.cargo = null;
        }
    }

    public void onPointSelect(PointSelectEvent event) {
    }
}
