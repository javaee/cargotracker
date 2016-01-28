package net.java.cargotracker.interfaces.booking.facade.internal.assembler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import net.java.cargotracker.domain.model.cargo.Cargo;
import net.java.cargotracker.domain.model.cargo.Itinerary;
import net.java.cargotracker.domain.model.cargo.Leg;
import net.java.cargotracker.domain.model.cargo.RouteSpecification;
import net.java.cargotracker.domain.model.cargo.TrackingId;
import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.SampleLocations;
import net.java.cargotracker.domain.model.voyage.SampleVoyages;
import net.java.cargotracker.interfaces.booking.facade.dto.CargoRoute;

import org.junit.Test;

// TODO This set of tests is very trivial, consider removing them.
public class CargoRouteDtoAssemblerTest {

    @Test
    public void testToDto() {
        CargoRouteDtoAssembler assembler = new CargoRouteDtoAssembler();

        Location origin = SampleLocations.STOCKHOLM;
        Location destination = SampleLocations.MELBOURNE;
        Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(
                origin, destination, new Date()));

        Itinerary itinerary = new Itinerary(
                Arrays.asList(new Leg(SampleVoyages.CM001, origin,
                                SampleLocations.SHANGHAI, new Date(), new Date()),
                        new Leg(SampleVoyages.CM001, SampleLocations.ROTTERDAM,
                                destination, new Date(), new Date())));

        cargo.assignToRoute(itinerary);

        CargoRoute dto = assembler.toDto(cargo);

        assertEquals(2, dto.getLegs().size());

        net.java.cargotracker.interfaces.booking.facade.dto.Leg legDto = dto
                .getLegs().get(0);
        assertEquals("CM001", legDto.getVoyageNumber());
        assertEquals("SESTO", legDto.getFrom());
        assertEquals("CNSHA", legDto.getTo());

        legDto = dto.getLegs().get(1);
        assertEquals("CM001", legDto.getVoyageNumber());
        assertEquals("NLRTM", legDto.getFrom());
        assertEquals("AUMEL", legDto.getTo());
    }

    @Test
    public void testToDtoNoItinerary() throws Exception {
        CargoRouteDtoAssembler assembler = new CargoRouteDtoAssembler();

        Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(
                SampleLocations.STOCKHOLM, SampleLocations.MELBOURNE,
                new Date()));
        CargoRoute dto = assembler.toDto(cargo);

        assertEquals("XYZ", dto.getTrackingId());
        assertEquals("Stockholm", dto.getOrigin());
        assertEquals("Melbourne", dto.getFinalDestination());
        assertTrue(dto.getLegs().isEmpty());
    }
}
