package net.java.cargotracker.interfaces.handling.mobile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import net.java.cargotracker.application.ApplicationEvents;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.location.UnLocode;
import net.java.cargotracker.domain.model.voyage.VoyageNumber;
import net.java.cargotracker.interfaces.booking.facade.BookingServiceFacade;
import net.java.cargotracker.interfaces.booking.facade.dto.CargoRoute;
import net.java.cargotracker.interfaces.booking.facade.dto.Leg;
import net.java.cargotracker.interfaces.booking.web.CargoDetails;
import net.java.cargotracker.interfaces.handling.HandlingEventRegistrationAttempt;

/**
 *
 * @author davidd
 */
@ManagedBean
@ViewScoped
public class EventBackingBean implements Serializable {

    @Inject
    private CargoDetails cargoDetails;

    @Inject
    private BookingServiceFacade bookingServiceFacade;

    @Inject
    private ApplicationEvents applicationEvents;

    private List<CargoRoute> cargos;

    //private String trackingId;
    private VoyageNumber voyageNumber;
    private Date completionDate;
    private String eventType;
    private String location;

    //private CargoTrackingViewAdapter cargo;
    private String trackId;
    private List<SelectItem> trackIds;
    private List<SelectItem> locations;
    private List<SelectItem> voyages;

    private boolean voyageSelectable = false;

    @PostConstruct
    public void init() {

        cargos = bookingServiceFacade.listAllCargos();

        trackIds = new ArrayList<>();
        for (CargoRoute route : cargos) {
            if (route.isRouted() && !route.isClaimed()) { // we just need getRoutedUnclaimedCargos
                String routedUnclaimedId = route.getTrackingId();
                trackIds.add(new SelectItem(routedUnclaimedId, routedUnclaimedId));
            }
        }

        locations = new ArrayList<>();
        List<String> allLocations = net.java.cargotracker.application.util.LocationUtil.getLocationsCode();
        for (String tempLoc : allLocations) {
            locations.add(new SelectItem(tempLoc));
        }

        voyages = new ArrayList<>(1);
        voyages.add(new SelectItem("Select cargo first", ""));

    }

    public boolean isVoyageSelectable() {
        return voyageSelectable;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTrackId() {
        return trackId;
    }

    public List<SelectItem> getLocations() {
        return locations;
    }

    public List<SelectItem> getTrackIds() {
        return trackIds;
    }

    public List<SelectItem> getVoyages() {
        return voyages;
    }

    public void updateVoyage() {

        // Updating voyage list for the selectTrackid
        cargoDetails.setTrackingId(trackId);
        cargoDetails.load();
        int nbrLegs = cargoDetails.getCargo().getLegs().size();

        //if (nbrLegs >= 1) { // at this stage, we can't get a zero leg cargo
        List<SelectItem> somevoyages = new ArrayList<>(nbrLegs);
        for (Leg leg : cargoDetails.getCargo().getLegs()) {
            String voyage = leg.getVoyageNumber();
            somevoyages.add(new SelectItem(voyage, voyage));
        }
        voyageSelectable = true;

        //}
        this.voyages = somevoyages;
        //RequestContext.getCurrentInstance().update("eventForm:voyage");
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public VoyageNumber getVoyageNumber() { // TODO : should the proposed voyage be related to the tracking ID only?
        return voyageNumber;
    }

    public void setVoyageNumber(VoyageNumber voyageNumber) {
        this.voyageNumber = voyageNumber;
    }

    public Date getCompletionTime() { // todo : can a completion be in the past?
        return completionDate;
    }

    public void setCompletionTime(Date completionTime) {
        this.completionDate = completionTime;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public String handleEventSubmission() {

        //Date completionTime = new SimpleDateFormat(ISO_8601_FORMAT).parse(completionDate);                                
        HandlingEvent.Type type = HandlingEvent.Type.valueOf(eventType);
        //UnLocode unLocode = new UnLocode(unLocode);
        TrackingId trackingId = new TrackingId(trackId);
        Date registrationTime = new Date();
        UnLocode unLocode = new UnLocode(this.location);
        
        // todo : check how event reg works
        HandlingEventRegistrationAttempt attempt
                = new HandlingEventRegistrationAttempt(registrationTime, completionDate, trackingId, voyageNumber, type, unLocode);
        applicationEvents.receivedHandlingEventRegistrationAttempt(attempt);

        // todo : provide some feedback
        
        voyageNumber = null;
        completionDate = null;
        unLocode = null;
        eventType = null;
        location = null;
        trackId = null;

        FacesContext context = FacesContext.getCurrentInstance(); 
        context.addMessage(null, new FacesMessage("Info",  "Event submitted") );
        
        return null;
    }

}