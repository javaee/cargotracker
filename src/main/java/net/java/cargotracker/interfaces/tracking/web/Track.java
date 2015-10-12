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
import static net.java.cargotracker.application.util.LocationUtil.getPortLatLng;
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
 * domain model, and the adapter helps us shield the domain model classes.
 * <p/>
 * In some very simplistic cases, it may be fine to not use even an adapter.
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
    private String lastKnowCoordinates;
    private MapModel markersModel;

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

    public MapModel getMarkersModel() {
        
        String origin = getCode(cargo.getOrigin());
        String dest = getCode(cargo.getDestination());
        String lastKnowLoc = "XXXX"; // "XXXX" = unknow
        
        try {
            lastKnowLoc = cargo.getLastKnowLocation().getUnLocode().getIdString();
        } catch (Exception e) {
            // todo : check why lastloc is null
        }        
                
        if (origin != null && !origin.isEmpty()) {
            markersModel.addOverlay(new Marker(getPortLatLng(origin), "Origin: " + cargo.getOrigin()));
        } 
        
        if (dest != null && !dest.isEmpty()) {
            markersModel.addOverlay(new Marker(getPortLatLng(dest), "Final destination: " + cargo.getDestination()));
        }
        if (lastKnowLoc != null && !lastKnowLoc.isEmpty() && !lastKnowLoc.toUpperCase().contains("XXXX")) {
                String lastKnownLocName = cargo.getLastKnowLocation().getName();
                markersModel.addOverlay(new Marker(getPortLatLng(lastKnowLoc), "Last known location: " + lastKnownLocName)); 
        } 
                
        return markersModel;
    }

    public List<String> completeTracking(String query) {
        List<TrackingId> oldList = cargoRepository.getAllTrackingId();
        List<String> newList = new ArrayList<>(oldList.size());
        for (TrackingId oldId : oldList) {
            newList.add(oldId.getIdString());
        }
        return newList;
    }

    public String getDestinationCoordinates() {
        return destinationCoordinates;
    }
    
    public String getLastKnowCoordinate() {
        return lastKnowCoordinates;
    }

    public void onTrackById() {

        markersModel = new DefaultMapModel();

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
        // TODO: handle whe  Map clicked
        //LatLng latlng = event.getLatLng();
        //addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "CLick", ""));
    }
}
