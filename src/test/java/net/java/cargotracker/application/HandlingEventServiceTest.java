package net.java.cargotracker.application;

import net.java.cargotracker.application.ApplicationEvents;
import java.util.Date;
import net.java.cargotracker.application.internal.DefaultHandlingEventService;
import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.CargoRepository;
import net.java.cargotracker.domain.model.cargo.RouteSpecification;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.handling.HandlingEvent;
import net.java.cargotracker.domain.model.handling.HandlingEventRepository;
import net.java.cargotracker.domain.model.location.LocationRepository;
import net.java.cargotracker.domain.model.location.SampleLocations;
import net.java.cargotracker.domain.model.voyage.SampleVoyages;
import net.java.cargotracker.domain.model.voyage.VoyageRepository;

public class HandlingEventServiceTest {

    private DefaultHandlingEventService service;
    private ApplicationEvents applicationEvents;
    private CargoRepository cargoRepository;
    private VoyageRepository voyageRepository;
    private HandlingEventRepository handlingEventRepository;
    private LocationRepository locationRepository;
    private Cargo cargo = new Cargo(new TrackingId("ABC"),
            new RouteSpecification(SampleLocations.HAMBURG, SampleLocations.TOKYO,
            new Date()));

    protected void setUp() throws Exception {
//        cargoRepository = createMock(CargoRepository.class);
//        voyageRepository = createMock(VoyageRepository.class);
//        handlingEventRepository = createMock(HandlingEventRepository.class);
//        locationRepository = createMock(LocationRepository.class);
//        applicationEvents = createMock(ApplicationEvents.class);
//        HandlingEventFactory handlingEventFactory = new HandlingEventFactory(
//                cargoRepository, voyageRepository, locationRepository);
//        service = new DefaultHandlingEventService(handlingEventRepository, applicationEvents, handlingEventFactory);
    }

    protected void tearDown() throws Exception {
//        verify(cargoRepository, voyageRepository, handlingEventRepository, applicationEvents);
    }

    public void testRegisterEvent() throws Exception {
//        expect(cargoRepository.find(cargo.getTrackingId())).andReturn(cargo);
//        expect(voyageRepository.find(SampleVoyages.CM001.getVoyageNumber()))
//                .andReturn(SampleVoyages.CM001);
//        expect(locationRepository.find(SampleLocations.STOCKHOLM.getUnLocode()))
//                .andReturn(SampleLocations.STOCKHOLM);
//        handlingEventRepository.store(isA(HandlingEvent.class));
//        applicationEvents.cargoWasHandled(isA(HandlingEvent.class));

//        replay(cargoRepository, voyageRepository, handlingEventRepository, 
//                locationRepository, applicationEvents);

        service.registerHandlingEvent(new Date(), cargo.getTrackingId(),
                SampleVoyages.CM001.getVoyageNumber(),
                SampleLocations.STOCKHOLM.getUnLocode(),
                HandlingEvent.Type.LOAD);
    }
}
