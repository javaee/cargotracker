package net.java.cargotracker.interfaces.booking.facade.internal.assembler;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import net.java.cargotracker.domain.model.location.Location;
import net.java.cargotracker.domain.model.location.SampleLocations;

//TODO This set of tests is very trivial, consider removing them.
public class LocationDtoAssemblerTest {

    @Test
    public void testToDTOList() {
        LocationDtoAssembler assembler = new LocationDtoAssembler();
        List<Location> locationList = Arrays.asList(SampleLocations.STOCKHOLM,
                SampleLocations.HAMBURG);

        List<net.java.cargotracker.interfaces.booking.facade.dto.Location> dtos = assembler
                .toDtoList(locationList);

        assertEquals(2, dtos.size());

        net.java.cargotracker.interfaces.booking.facade.dto.Location dto = dtos
                .get(0);
        assertEquals("SESTO", dto.getUnLocode());
        assertEquals("Stockholm", dto.getName());

        dto = dtos.get(1);
        assertEquals("DEHAM", dto.getUnLocode());
        assertEquals("Hamburg", dto.getName());
    }
}
